package fr.android.projetJeux.client;

import fr.android.projetJeux.client.fx.Pion;
import fr.android.projetJeux.client.fx.Spot;
import fr.android.projetJeux.client.fx.elements.GameButton;
import fr.android.projetJeux.client.fx.elements.GameInput;
import fr.android.projetJeux.client.fx.elements.GameLine;
import fr.android.projetJeux.client.fx.elements.GameText;
import fr.android.projetJeux.security.SecurityDES;
import fr.android.projetJeux.server.game.morpion.Coords;
import fr.android.projetJeux.server.game.morpion.GridGame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.apache.commons.lang3.SerializationUtils;

public class App extends Application {
    /**
     * couleur de fond
     */
    public static Color background = Color.GRAY;
    /**
     * largeur
     */
    public static double sizeX = 600;
    /**
     * hauteur
     */
    public static double sizeY = 600;
    /**
     * conteneur de tous les éléments
     */
    public static Group group = new Group();
    /**
     * conteneur des éléments
     */
    public static Group contentGroup = new Group();
    /**
     * conteneur du message erreur
     */
    public static Group errorGroup = new Group();
    /**
     * client
     */
    public static Client client = new Client();
    /**
     * la game
     */
    public static GridGame grid;
    /**
     * informations sur le jeu
     */
    private static GameText text;
    /**
     * message d'erreur
     */
    private static final GameText error = new GameText();
    /**
     * liste des lignes de victoire
     */
    private static final GameLine[] lineList = new GameLine[8];

    /**
     * démarrage de l'app
     *
     * @param stage stage
     */
    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(group, sizeX, sizeY, background));
        stage.setTitle("Morpion");
        stage.show();
        new Thread(() -> client.start()).start();

        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });

        error.setPosition(25, sizeY * 0.7);
        error.setTextFill(Color.RED);
        error.setFontSize(25);
        errorGroup.getChildren().add(error);

        group.getChildren().addAll(contentGroup, errorGroup);

    }

    /**
     * Afficher le textField pour entrer le pseudo
     */
    public static void displayPseudoField() {
        contentGroup.getChildren().clear();

        GameText label = new GameText("Pseudo");
        label.setPosition(15, sizeY / 2);
        label.setFontSize(25);

        GameInput field = new GameInput(100, sizeY / 2, sizeX - 200, 40);

        GameButton submit = new GameButton("Valider");
        submit.setPosition(field.getMinWidth() + field.getLayoutX() - 60, field.getLayoutY() + 45);
        submit.setOnAction(actionEvent -> {
            try {
                client.setPseudo(field.getText());
                client.getOut().writeObject(field.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        contentGroup.getChildren().addAll(field, label, submit);
    }

    /**
     * cache le textField pour le pseudo et affiche en attente d'adversaire si attente il y a
     */

    public static void hidePseudo() {
        contentGroup.getChildren().clear();

        GameText message = new GameText("En attente d'adversaire");
        message.setFontSize(30);
        message.setPosition(sizeX / 3, sizeY / 2);

        contentGroup.getChildren().add(message);
    }

    /**
     * affiche un message d'erreur
     *
     * @param message message d'erreur
     */

    public static void displayErrorMessage(String message) {
        error.setText(message);
    }

    /**
     * cache l'erreur
     */
    public static void hideError() {
        error.setText("");
    }

    /**
     * créée l'affichage de la grille avec les infos en parametres
     *
     * @param g grille
     */
    public static void setGrid(GridGame g) {
        grid = g;
        contentGroup.getChildren().clear();
        hideError();

        double size = 150;
        double xDepart = (sizeX - size * 3) / 2;
        double y = 25 + (sizeY - size * 3) / 2;

        text = new GameText(grid.getNamePlayer().equals(client.getPseudo()) ? "Votre tour choisir case" : "Au tour de votre adversaire, veuillez patienter");
        text.setPosition(xDepart, 25);
        text.setFontSize(25);

        contentGroup.getChildren().add(text);

        double xLine = xDepart + size / 2;
        double yLine = y - 25;
        double lineSize = size * 3 + 50;
        for (int i = 0; i < 3; i++) {
            lineList[i] = new GameLine(xLine, yLine, xLine, yLine + lineSize);
            xLine += size;
        }

        xLine = xDepart - 25;
        yLine = y + size / 2;
        for (int i = 3; i < 6; i++) {
            lineList[i] = new GameLine(xLine, yLine, xLine + lineSize, yLine);
            yLine += size;
        }

        xLine = xDepart - 25;
        yLine = y - 25;

        lineList[6] = new GameLine(xLine, yLine, xLine + lineSize, yLine + lineSize);
        lineList[7] = new GameLine(xLine + lineSize, yLine, xLine, yLine + lineSize);


        group.getChildren().addAll(Arrays.asList(lineList));

        int i = 0, j = 0;
        for (char[] row : grid.getGrid()) {
            double x = xDepart;
            for (char pion : row) {
                Spot spot = new Spot(new Coords(i, j), x, y, size);
                contentGroup.getChildren().add(spot);
                if (pion != ' ') {
                    Pion p = new Pion(pion, x, y, size);
                    contentGroup.getChildren().add(p.getPion());
                }
                x += size;
                j++;
            }
            j = 0;
            y += size;
            i++;
        }
    }

    /**
     * envoie les coordonnées de la case sélectionée
     *
     * @param c coordonnées
     * @throws IOException en case de problème d'E/S
     */
    public static void sendCoords(Coords c) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        if (grid.isValid(c)) {
            if (grid.getNamePlayer().equals(client.getPseudo())) {
                client.getOut().writeObject(SecurityDES.encode(SerializationUtils.serialize(c), client.getKey()));
            }
        } else {
            System.out.println("déjà été joué");
        }
    }

    /**
     * affiche une ligne sur la ligne gagnante
     *
     * @param index num de la ligne gagnante
     */
    public static void setWinner(int index) {
        lineList[index].setVisible(true);
        text.setText(grid.getNamePlayer().equals(client.getPseudo()) ? "Félicitation vous avez gagné" : "Vous avez perdu");
    }

    /**
     * affiche match nul
     */
    public static void setMatchNul() {
        text.setText("Match Nul");
    }


    public static void main(String[] args) {
        launch();
    }
}

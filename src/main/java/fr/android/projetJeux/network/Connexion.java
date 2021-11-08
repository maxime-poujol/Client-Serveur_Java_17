package fr.android.projetJeux.network;

import fr.umontpellier.iut.thread.ServerThread;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connexion implements Runnable{

    private Socket socket;

    private static final HashMap<String, Socket> clients = new HashMap<>();
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Connexion(@NotNull Socket socket) {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            String pseudo;
            boolean exists = true;
            while (exists) {
                pseudo = (String) in.readObject();
                if (!clients.containsKey(pseudo)) {
                    exists = false;
                    clients.put(pseudo,socket);
                    out.writeObject("OK");
                } else {
                    out.writeObject(pseudo + " existe déjà !");
                }
            }

            System.out.println(clients);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //startConnexion();

    }


    private boolean startConnexion(){
        /*ExecutorService es = Executors.newSingleThreadExecutor();
        try {
            es.execute(new ServerThread(socket));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }*/
        return false;
    }


    public static HashMap<String, Socket> getClients() {
        return clients;
    }

}
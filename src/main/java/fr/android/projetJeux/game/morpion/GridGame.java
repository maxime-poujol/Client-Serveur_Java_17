package fr.android.projetJeux.game.morpion;

import java.io.Serializable;
import java.util.Arrays;

public class GridGame implements Serializable {

    private String namePlayer;
    private String[][] grid;

    public GridGame(){
        this.grid = new String[3][3];
    }

    public String getNamePlayer() {
        return namePlayer;
    }

    public String[][] getGrid() {
        return grid;
    }

    /*public void setGrid(String[][] grid) {
        this.grid = grid;
    }*/

    public void setMovement(Coords coord, String pion){
        grid[coord.x][coord.y] = pion;
    }

    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }

    @Override
    public String toString() {
        return      grid[0][0] + " | " + grid[0][1] + " | " + grid[0][2] + "\n"
                +   grid[1][0] + " | " + grid[1][1] + " | " + grid[1][2] + "\n"
                +   grid[2][0] + " | " + grid[2][1] + " | " + grid[2][2] + "\n";
    }

    public static void main(String[] args) {
        GridGame gridGame = new GridGame();

        //gridGame.setGrid(new String[][]{{"x", "x", "x"}, {"x", "x", "x"}, {"x", "x", "x"}});
        System.out.println(gridGame);
    }
}

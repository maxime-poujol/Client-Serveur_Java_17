package fr.umontpellier.iut.FX.Elements;

import javafx.scene.shape.Rectangle;

public class Div extends Rectangle {

    double posX, posY, sizeX, sizeY;

    public Div(double posX, double posY, double sizeX, double sizeY) {
        setPosition(posX,posY);
        setDimensions(sizeX,sizeY);
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getSizeX() {
        return sizeX;
    }

    public double getSizeY() {
        return sizeY;
    }

    public void setPosX(double posX) {
        this.posX = posX;
        setLayoutX(posX);
    }

    public void setPosY(double posY) {
        this.posY = posY;
        setLayoutY(posY);
    }

    public void setSizeX(double sizeX) {
        this.sizeX = sizeX;
        setWidth(sizeX);
    }

    public void setSizeY(double sizeY) {
        this.sizeY = sizeY;
        setHeight(sizeY);
    }

    public void setDimensions(double w, double h) {
        setSizeX(w);
        setSizeY(h);
    }

    public void setPosition(double x, double y) {
        setPosX(x);
        setPosY(y);
    }


}
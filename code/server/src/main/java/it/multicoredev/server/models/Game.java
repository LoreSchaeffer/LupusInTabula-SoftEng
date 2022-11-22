package it.multicoredev.server.models;

import java.awt.*;

import static it.multicoredev.server.utils.Utils.randomCode;

public class Game {
    private final String id;
    private Boolean gameFinished;
    private List listaPlayer;
    private Boolean day;
    private int numDays;

    //TODO controllare che non ci siano due game con lo stesso codice

    Game(){
        this.id = randomCode(6);
    }

    private String getId(){
        return id;
    }

    public void start(){
        this.gameFinished = false;
    }

    public void endGame(){
        this.gameFinished = true;
    }
}

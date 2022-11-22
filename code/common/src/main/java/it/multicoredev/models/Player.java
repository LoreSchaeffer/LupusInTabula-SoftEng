package it.multicoredev.models;

public class Player {
    private String id;
    private Enum role;
    private Boolean status;

    Player(String id){
        this.id = id;
    }

    private String getId(){
        return id;
    }
}

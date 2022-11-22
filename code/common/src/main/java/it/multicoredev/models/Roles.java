package it.multicoredev.models;

public enum Roles {
    VILLAN("villan"),
    WEREWOLF("werewolf"),
    SEER("seer"),
    MEDIUM("medium"),
    BODYGUARD("bodyguard"),
    POSSESSED("possessed"),
    MASON("mason"),
    HOWL("howl"),
    WEREHAMSTER("werehamster"),
    MITHOMANIAC("mithomaniac");

    private final String id;

    Roles(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }
}

package it.multicoredev.models;

public enum Roles {
    VILLAGER("villager"),
    WEREWOLF("werewolf"),
    SEER("seer"),
    MEDIUM("medium"),
    BODYGUARD("bodyguard"),
    POSSESSED("possessed"),
    FREEMASON("freemason"),
    OWN_MAN("owl_man"),
    WEREHAMSTER("werehamster"),
    MYTHOMANIAC("mythomaniac");

    private final String id;
    //TODO Add limits and amounts

    Roles(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

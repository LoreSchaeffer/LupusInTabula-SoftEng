package it.multicoredev.enums;

public enum Role {
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

    Role(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

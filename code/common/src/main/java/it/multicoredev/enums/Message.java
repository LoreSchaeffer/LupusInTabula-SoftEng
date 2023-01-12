package it.multicoredev.enums;

public enum Message {
    GAME_NARRATOR("game.narrator", "Narrator"),
    GAME_TURN_MEDIUM("game.turn.medium", "The Medium wakes up and sees if the lynched player was or was not a werewolf"),
    GAME_TURN_MEDIUM_WEREWOLF("game.turn.medium.werewolf", "{0} WAS a werewolf"),
    GAME_TURN_MEDIUM_VILLAGER("game.turn.medium.villager", "{0} was NOT a werewolf"),
    GAME_TURN_SEER("game.turn.seer", "The seer wakes up pick someone to ask about"),
    GAME_TURN_SEER_WEREWOLF("game.turn.seer.werewolf", "{0} is a werewolf"),
    GAME_TURN_SEER_VILLAGER("game.tur.seer.villager", "{0} is NOT a werewolf"),
    GAME_TURN_OWL_MAN("game.turn.owl_man", "The Owl-man wakes up and choose a player"),

    MODAL_TITLE_INSUFFICIENT_PLAYERS("modal.title.insufficient_players", "Insufficient players"),
    MODAL_BODY_INSUFFICIENT_PLAYERS("modal.body.insufficient_players", "You need at least %d players to start a game");

    private final String path;
    private final String defValue;

    Message(String id, String defValue) {
        this.path = id;
        this.defValue = defValue;
    }

    public String getPath() {
        return path;
    }

    public String getDefValue() {
        return defValue;
    }
}

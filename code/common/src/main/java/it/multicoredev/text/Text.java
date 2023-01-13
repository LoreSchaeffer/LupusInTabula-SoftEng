package it.multicoredev.text;

import org.jetbrains.annotations.Nullable;

public enum Text {
    NARRATOR("narrator", "Narrator"),
    VILLAGER("role.villager", "Villager"),
    WEREWOLF("role.werewolf", "Werewolf"),
    SEER("role.seer", "Seer"),
    MEDIUM("role.medium", "Medium"),
    BODYGUARD("role.bodyguard", "Bodyguard"),
    POSSESSED("role.possessed", "Possessed"),
    FREEMASON("role.freemason", "Freemason"),
    OWL_MAN("role.owl_man", "Owl-man"),
    WEREHAMSTER("role.werehamster", "Werehamster"),
    MYTHOMANIAC("role.mythomaniac", "Mythomaniac"),

    CHANNEL_ALL("channel.all", "All"),
    CHANNEL_SYSTEM("channel.system", "System"),
    CHANNEL_WEREWOLVES("channel.werewolves", "Werewolves"),
    CHANNEL_SEERS("channel.seers", "Seers"),
    CHANNEL_DEAD("channel.dead", "Dead"),

    GAME_TURN_MEDIUM("game.turn.medium", "The Medium wakes up and sees whether or not the lynched person was a werewolf"),
    GAME_TURN_MEDIUM_WEREWOLF("game.turn.medium.werewolf", "{0} WAS a werewolf"),
    GAME_TURN_MEDIUM_VILLAGER("game.turn.medium.villager", "{0} was NOT a werewolf"),
    GAME_TURN_SEER("game.turn.seer", "The seer wakes up picks someone to ask about"),
    GAME_TURN_SEER_WEREWOLF("game.turn.seer.werewolf", "{0} is a werewolf"),
    GAME_TURN_SEER_VILLAGER("game.tur.seer.villager", "{0} is NOT a werewolf"),
    GAME_TURN_OWL_MAN("game.turn.owl_man", "The Owl-man wakes up and picks up someone to send to the lynching"),
    GAME_TURN_MYTHOMANIAC("game.turn.mythomaniac", "The Mythomaniac wakes up and picks up someone to imitate"),
    GAME_TURN_MYTHOMANIAC_ROLE("game.turn.mythomaniac.role", "Your new role is {0}"),
    GAME_TURN_BODYGUARD("game.turn.bodyguard", "The Bodyguard wakes up and picks up someone to protect"),
    GAME_TURN_WEREWOLF("game.turn.werewolf", "The Werewolves wake up and pick up someone to devour"),
    GAME_TURN_WEREWOLF_CHOICE("game.turn.werewolf.choice", "The werewolves choose to devour {0}"),

    MODAL_TITLE_INSUFFICIENT_PLAYERS("modal.title.insufficient_players", "Insufficient players"),
    MODAL_BODY_INSUFFICIENT_PLAYERS("modal.body.insufficient_players", "You need at least %d players to start a game");

    private final String path;
    private final String defValue;

    Text(String id, String defValue) {
        this.path = id;
        this.defValue = defValue;
    }

    public TranslatableText getText(Object... args) {
        return new TranslatableText(path, args);
    }

    public String getPath() {
        return path;
    }

    public String getDefValue() {
        return defValue;
    }

    @Nullable
    public static Text byPath(@Nullable String path) {
        for (Text text : values()) {
            if (text.getPath().equals(path)) return text;
        }

        return null;
    }
}

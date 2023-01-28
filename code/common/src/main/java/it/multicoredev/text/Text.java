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

    TIME_DAY_START("time.day.start", "It's morning and the sun is shining. The villagers wake up, they are all still alive"),
    TIME_DAY_START_DEAD("time.day.start.dead", "It's morning, the sun is shining. The villagers wake up except {0}"),
    TIME_NIGHT_START("time.night.start", "It's night, the moon is full and the villagers go to sleep"),

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
    GAME_TURN_DISCUSSION_1("game.turn.discussion.1", "The villagers argue to choose two suspected to be sent to the lynching"),
    GAME_TURN_DISCUSSION_1_END("game.turn.discussion.1.end", "The time to argue is over"),
    GAME_TURN_DISCUSSION_2("game.turn.discussion.2", "The suspected villagers try to convince the others that they are not werewolves"),
    GAME_TURN_DISCUSSION_2_END("game.turn.discussion.2.end", "The time to argue is over"),
    GAME_TURN_VOTE_1("game.turn.vote.1", "The villagers now vote to choose who to send to the lynching"),
    GAME_TURN_VOTE_1_END("game.turn.vote.1.end", "The time to vote is over. The suspected villagers sent to the lynching are {0}"),
    GAME_TURN_VOTE_2("game.turn.vote.2", "The villagers now vote the person to lynch between {0}"),
    GAME_TURN_VOTE_2_END("game.turn.vote.2.end", "The time to vote is over. The person lynched is {0}"),
    GAME_TURN_LYNCHING_SKIPPED("game.turn.lynching.skipped", "None of the villager voted, the lynching was skipped"),

    GAME_WIN_VILLAGERS("game.win.villagers", "All the werewolves have been killed. The villagers won"),
    GAME_WIN_WEREWOLVES("game.win.werewolves", "The werewolves devoured all the villagers"),

    SELECT_YOURSELF("select.yourself", "You can't select yourself"),
    SELECT_DEAD("select.dead", "You can't select a dead player"),
    SELECT_ALREADY_KNOWN("select.already_known", "You already know the role of this player"),
    SELECT_WEREWOLF("select.werewolf", "You can't select an other werewolf"),

    MODAL_TITLE_INSUFFICIENT_PLAYERS("modal.title.insufficient_players", "Insufficient players"),
    MODAL_BODY_INSUFFICIENT_PLAYERS("modal.body.insufficient_players", "You need at least %d players to start a game"),
    MODAL_TITLE_GAME_STOPPED("modal.title.game_stopped", "Game interrupted"),
    MODAL_BODY_GAME_STOPPED("modal.body.game_stopped", "An error caused the game to stop. Please create a new game to play"),
    MODAL_TITLE_CONNECTION_ERROR("modal.title.connection_error", "Connection error"),
    MODAL_BODY_CONNECTION_ERROR("modal.body.connection_error", "Cannot connect to the server. Please check your internet connection and try again"),
    MODAL_TITLE_CONNECTION_REJECTED("modal.title.connection_rejected", "Connection rejected"),
    MODAL_BODY_CONNECTION_REJECTED("modal.body.connection_rejected", "The server rejected your connection. Maybe the server is full or you have been banned"),
    MODAL_TITLE_GAME_NOT_FOUND("modal.title.game_not_found", "Game not found"),
    MODAL_BODY_GAME_NOT_FOUND("modal.body.game_not_found", "The game you are trying to join does not exist"),
    MODAL_TITLE_SERVER_CLOSING("modal.title.server_closing", "Server closing"),
    MODAL_BODY_SERVER_CLOSING("modal.body.server_closing", "The server is closing"),
    MODAL_TITLE_GENERIC_DISCONNECTION("modal.title.generic_disconnection", "Disconnected"),
    MODAL_BODY_GENERIC_DISCONNECTION("modal.body.generic_disconnection", "You have been disconnected from the server"),
    MODAL_TITLE_NETWORK_EXCEPTION("modal.title.network_exception", "Network error"),
    MODAL_BODY_NETWORK_EXCEPTION("modal.body.network_exception", "An error occurred while communicating with the server.<br>{0}"),
    MODAL_TITLE_USERNAME_SELECTION("modal.title.username_selection", "Chose your name"),


    DOWNLOADING_NATIVES("system.downloading_natives", "Downloading natives...");

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

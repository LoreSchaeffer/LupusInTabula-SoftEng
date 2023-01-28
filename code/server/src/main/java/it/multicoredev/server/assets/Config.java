package it.multicoredev.server.assets;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.mclib.json.JsonConfig;

import java.util.ArrayList;
import java.util.List;

public class Config extends JsonConfig {
    @SerializedName("port")
    public Integer port;

    @SerializedName("game_starting_countdown")
    public Integer gameStartingCountdown;
    @SerializedName("game_turn_duration")
    public Integer gameTurnDuration;
    @SerializedName("first_discussion_duration")
    public Integer firstDiscussionDuration;
    @SerializedName("first_vote_duration")
    public Integer firstVoteDuration;
    @SerializedName("second_discussion_duration")
    public Integer secondDiscussionDuration;
    @SerializedName("second_discussion_duration_3")
    public Integer secondDiscussionDuration3;
    @SerializedName("second_vote_duration")
    public Integer secondVoteDuration;

    @SerializedName("censored_words")
    public List<String> censoredWords;

    @Override
    public Config init() {
        if (port == null) port = 12987;

        if (gameStartingCountdown == null) gameStartingCountdown = 5;
        if (gameTurnDuration == null) gameTurnDuration = 30;
        if (firstDiscussionDuration == null) firstDiscussionDuration = 180;
        if (firstVoteDuration == null) firstVoteDuration = 60;
        if (secondDiscussionDuration == null) secondDiscussionDuration = 60;
        if (secondDiscussionDuration3 == null) secondDiscussionDuration3 = 90;
        if (secondVoteDuration == null) secondVoteDuration = 40;

        if (censoredWords == null) censoredWords = new ArrayList<>();
        else censoredWords.replaceAll(String::toLowerCase);
        return this;
    }
}

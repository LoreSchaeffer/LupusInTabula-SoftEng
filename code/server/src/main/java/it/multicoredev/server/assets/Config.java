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

    @SerializedName("censored_words")
    public List<String> censoredWords;

    @Override
    public Config init() {
        if (port == null) port = 12987;

        if (gameStartingCountdown == null) gameStartingCountdown = 5;

        if (censoredWords == null) censoredWords = new ArrayList<>();
        else censoredWords.replaceAll(String::toLowerCase);
        return this;
    }
}
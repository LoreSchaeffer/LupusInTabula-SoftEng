package it.multicoredev.server.assets;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.mclib.json.JsonConfig;

public class Config extends JsonConfig {
    @SerializedName("port")
    public Integer port;

    @SerializedName("game_starting_countdown")
    public Integer gameStartingCountdown;

    @Override
    public Config init() {
        if (port == null) port = 12987;

        if (gameStartingCountdown == null) gameStartingCountdown = 5;
        return this;
    }
}

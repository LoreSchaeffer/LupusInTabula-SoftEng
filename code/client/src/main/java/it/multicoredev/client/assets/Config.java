package it.multicoredev.client.assets;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.mclib.json.JsonConfig;

public class Config extends JsonConfig {
    @SerializedName("server_address")
    public String serverAddress;
    public String username;
    public String language;

    @Override
    public Config init() {
        if (serverAddress == null) serverAddress = "127.0.0.1";
        if (language == null) language = "en_us";
        return this;
    }
}

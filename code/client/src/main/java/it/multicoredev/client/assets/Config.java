package it.multicoredev.client.assets;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.mclib.json.JsonConfig;

public class Config extends JsonConfig {
    @SerializedName("server_address")
    public String serverAddress;
    public Integer port;

    @Override
    public Config init() {
        if (serverAddress == null) serverAddress = "127.0.0.1";
        if (port == null) port = 12987;
        return this;
    }
}

package it.multicoredev.client.ui;

import java.util.List;

public class InternalMessage {
    private final String type;
    private final List<Object> data;

    public InternalMessage(String type, List<Object> data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public List<Object> getData() {
        return data;
    }
}

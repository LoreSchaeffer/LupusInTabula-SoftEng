package it.multicoredev.text;

import org.jetbrains.annotations.NotNull;

public class StaticText extends BaseText {
    private final String text;

    public StaticText(@NotNull String text) {
        super("static");
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}

package it.multicoredev.text;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticText that = (StaticText) o;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}

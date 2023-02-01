package it.multicoredev.text;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class TranslatableText extends BaseText {
    private final String path;
    private final Object[] args;
    private transient ILocalization localization;

    public TranslatableText(@NotNull String path, @NotNull Object... args) {
        super("translatable");
        this.path = path;
        this.args = args;
    }

    public TranslatableText setLocalization(@NotNull ILocalization localization) {
        this.localization = localization;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String getText() {
        String text = localization.get(path);
        if (text == null) text = "";

        for (int i = 0; i < args.length; i++) {
            String argument;

            if (args[i] != null) {
                if (args[i] instanceof BaseText) argument = ((BaseText) args[i]).getText();
                else argument = args[i].toString();
            } else {
                argument = "null";
            }


            text = text.replaceAll("\\{" + i + "}", argument);
        }

        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslatableText that = (TranslatableText) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}

package it.multicoredev.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LitLogger {
    private static Logger LOGGER = LoggerFactory.getLogger("LIT");

    public static Logger get() {
        return LOGGER;
    }

}

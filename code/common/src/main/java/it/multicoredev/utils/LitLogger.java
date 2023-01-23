package it.multicoredev.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LitLogger {
    private static Logger LOGGER = LoggerFactory.getLogger("LIT");

    public static Logger get() {
        return LOGGER;
    }

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void info(String format, Object arg) {
        LOGGER.info(format, arg);
    }

    public static void info(String format, Object arg1, Object arg2) {
        LOGGER.info(format, arg1, arg2);
    }

    public static void info(String format, Object... arguments) {
        LOGGER.info(format, arguments);
    }

    public static void info(String msg, Throwable t) {
        LOGGER.info(msg, t);
    }

    public static void warn(String msg) {
        LOGGER.warn(msg);
    }

    public static void warn(String format, Object arg) {
        LOGGER.warn(format, arg);
    }

    public static void warn(String format, Object... arguments) {
        LOGGER.warn(format, arguments);
    }

    public static void warn(String format, Object arg1, Object arg2) {
        LOGGER.warn(format, arg1, arg2);
    }

    public static void warn(String msg, Throwable t) {
        LOGGER.warn(msg, t);
    }

    public static void error(String msg) {
        LOGGER.error(msg);
    }

    public static void error(String format, Object arg) {
        LOGGER.error(format, arg);
    }

    public static void error(String format, Object arg1, Object arg2) {
        LOGGER.error(format, arg1, arg2);
    }

    public static void error(String format, Object... arguments) {
        LOGGER.error(format, arguments);
    }

    public static void error(String msg, Throwable t) {
        LOGGER.error(msg, t);
    }
}

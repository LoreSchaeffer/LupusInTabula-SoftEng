package it.multicoredev.server;

import it.multicoredev.utils.Static;

public class Bootstrap {

    public static void main(String[] args) {
        for (String arg : args) {
            Static.DEBUG = arg.equals("--debug") || arg.equals("-d");
        }
        LupusInTabula.get().start();
    }
}

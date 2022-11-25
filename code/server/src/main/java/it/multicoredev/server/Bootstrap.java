package it.multicoredev.server;

import it.multicoredev.utils.Static;

public class Bootstrap {

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("--debug") || arg.equals("-d")) Static.DEBUG = true;
            else Static.DEBUG = false;
        }
        LupusInTabula.get().start();
    }
}

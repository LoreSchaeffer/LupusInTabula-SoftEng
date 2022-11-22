package it.multicoredev.client.utils;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class ServerAddress extends it.multicoredev.mclib.network.client.ServerAddress {

    public ServerAddress(String ip, int port) {
        super(ip, port);
    }

    public static ServerAddress fromString(String address) {
        if (address == null) return null;

        String[] args = address.split(":");
        if (address.startsWith("[")) {
            int i = address.indexOf("]");

            if (i > 0) {
                String ip = address.substring(1, i);
                String port = address.substring(i + 1).trim();

                if (port.startsWith(":")) {
                    port = port.substring(1);
                    args = new String[]{ip, port};
                } else {
                    args = new String[]{ip};
                }
            }
        }

        if (args.length > 2) {
            args = new String[]{address};
        }

        String ip = args[0];
        int port = args.length > 1 ? getInt(args[1], 12987) : 12987;

        if (port == 12987) {
            String[] serverAddress = getServerAddress(ip);
            ip = serverAddress[0];
            port = getInt(serverAddress[1], 12987);
        }

        return new ServerAddress(ip, port);
    }

    private static String[] getServerAddress(String address) {
        try {
            String dnsResolver = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName(dnsResolver);

            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put("java.naming.factory.initial", dnsResolver);
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");

            DirContext dirContext = new InitialDirContext(hashtable);
            Attributes attr = dirContext.getAttributes("_lit._tcp." + address, new String[]{"SRV"});
            String[] serverAddress = attr.get("srv").get().toString().split(" ", 4);
            return new String[]{serverAddress[3], serverAddress[2]};
        } catch (Throwable ignored) {
            return new String[]{address, Integer.toString(12987)};
        }
    }

    private static int getInt(String value, int defValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return defValue;
        }
    }
}

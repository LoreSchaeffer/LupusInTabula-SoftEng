package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.mclib.network.protocol.PacketRegistry;

public enum Packets {
    ;

    private final Class<? extends Packet<?>> packetClass;

    Packets(Class<? extends Packet<?>> packetClass) {
        this.packetClass = packetClass;
    }

    public Class<? extends Packet<?>> getPacketClass() {
        return packetClass;
    }

    public static void registerPackets() {
        PacketRegistry registry = PacketRegistry.getInstance();

        for (Packets packet : values()) {
            registry.registerPacket(packet.getPacketClass());
        }
    }
}

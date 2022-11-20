package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.mclib.network.protocol.PacketRegistry;
import it.multicoredev.network.clientbound.S2CHandshakePacket;
import it.multicoredev.network.serverbound.C2SHandshakePacket;
import it.multicoredev.network.serverbound.C2SMessagePacket;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;

public enum Packets {
    C2S_HANDSHAKE(C2SHandshakePacket.class),
    C2S_MESSAGE(C2SMessagePacket.class),

    S2C_HANDSHAKE(S2CHandshakePacket.class);

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
            if (Static.DEBUG) LitLogger.get().info("Registered packet " + packet.getPacketClass().getSimpleName() + " with id " + registry.getPacketId(packet.getPacketClass()));
        }
    }
}

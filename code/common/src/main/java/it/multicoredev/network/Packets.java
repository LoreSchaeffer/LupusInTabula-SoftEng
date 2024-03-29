package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.mclib.network.protocol.PacketRegistry;
import it.multicoredev.network.clientbound.*;
import it.multicoredev.network.serverbound.*;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;

public enum Packets {
    C2S_CREATE_GAME(C2SCreateGame.class),
    C2S_DISCONNECT(C2SDisconnectPacket.class),
    C2S_HANDSHAKE(C2SHandshakePacket.class),
    C2S_JOIN_GAME(C2SJoinGamePacket.class),
    C2S_MESSAGE(C2SMessagePacket.class),
    C2S_SELECT(C2SSelectPacket.class),
    C2S_START_GAME(C2SStartGamePacket.class),

    S2C_CHANGE_SCENE(S2CChangeScenePacket.class),
    S2C_DISCONNECT(S2CDisconnectPacket.class),
    S2C_END_GAME(S2CEndGamePacket.class),
    S2C_GAME(S2CGamePacket.class),
    S2C_GAME_CREATED(S2CGameCreatedPacket.class),
    S2C_GAME_JOINED(S2CGameJoinedPacket.class),
    S2C_GAME_START_COUNTDOWN(S2CGameStartCountdownPacket.class),
    S2C_HANDSHAKE(S2CHandshakePacket.class),
    S2C_MESSAGE(S2CMessagePacket.class),
    S2C_MODAL(S2CModalPacket.class),
    S2C_PLAYER_JOIN(S2CPlayerJoinPacket.class),
    S2C_PLAYER_LEAVE(S2CPlayerLeavePacket.class),
    S2C_TIMER(S2CTimerPacket.class),
    S2C_TURN_PACKET(S2CTurnPacket.class);

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
            if (Static.DEBUG)
                LitLogger.info("Registered packet " + packet.getPacketClass().getSimpleName() + " with id " + registry.getPacketId(packet.getPacketClass()));
        }
    }
}

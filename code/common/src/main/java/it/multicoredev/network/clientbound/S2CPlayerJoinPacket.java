package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.models.Player;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

public class S2CPlayerJoinPacket implements Packet<IClientPacketListener> {
    private Player player;
    private boolean readyToStart;

    public S2CPlayerJoinPacket(@NotNull Player player, boolean readyToStart) {
        this.player = player;
        this.readyToStart = readyToStart;
    }

    public S2CPlayerJoinPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (player == null) throw new EncoderException("Player is null");

        buf.writeObject(player);
        buf.writeBoolean(readyToStart);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        player = buf.readObject(Player.class);
        readyToStart = buf.readBoolean();

        if (player == null) throw new DecoderException("Player is null");
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handlePlayerJoin(this);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isReadyToStart() {
        return readyToStart;
    }
}

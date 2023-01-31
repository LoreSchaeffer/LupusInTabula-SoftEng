package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.models.Game;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

public class S2CGameJoinedPacket implements Packet<IClientPacketListener> {
    private Game game;

    public S2CGameJoinedPacket(@NotNull Game game) {
        this.game = game;
    }

    public S2CGameJoinedPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (game == null) throw new EncoderException("Game is null");

        buf.writeObject(game);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        game = buf.readObject(Game.class);

        if (game == null) throw new DecoderException("Game is null");
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleGameJoined(this);
    }

    public Game getGame() {
        return game;
    }
}

package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.models.Game;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

public class S2CGamePacket implements Packet<IClientPacketListener> {
    private Game game;

    public S2CGamePacket(@NotNull Game game) {
        this.game = game;
    }

    public S2CGamePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (game == null) throw new EncoderException("Game is null");

        buf.writeObject(game);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        try {
            game = buf.readObject(Game.class);
        } catch (IllegalArgumentException e) {
            throw new DecoderException("Error while reading game", e);
        }
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleGame(this);
    }

    public Game getGame() {
        return game;
    }
}

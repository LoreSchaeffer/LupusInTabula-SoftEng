package it.multicoredev.network.serverbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IServerPacketListener;
import org.jetbrains.annotations.NotNull;

public class C2SJoinGame implements Packet<IServerPacketListener> {
    private String gameCode;

    public C2SJoinGame(@NotNull String gameCode) {
        this.gameCode = gameCode;
    }

    public C2SJoinGame() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (gameCode == null || gameCode.trim().isEmpty()) throw new EncoderException("Game code cannot be null or empty");

        buf.writeString(gameCode);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        gameCode = buf.readString();

        if (gameCode == null || gameCode.trim().isEmpty()) throw new DecoderException("Game code cannot be null or empty");
    }

    @Override
    public void processPacket(IServerPacketListener handler) throws ProcessException {
        handler.handleJoinGame(this);
    }
}

package it.multicoredev.network.clientbound;

import it.multicoredev.enums.Role;
import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

public class S2CEndGamePacket implements Packet<IClientPacketListener> {
    private Role winner;

    public S2CEndGamePacket(@NotNull Role winner) {
        this.winner = winner;
    }

    public S2CEndGamePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (winner == null) throw new EncoderException("Winner is null");

        buf.writeInt(winner.ordinal());
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        winner = Role.values()[buf.readInt()];

        if (winner == null) throw new DecoderException("Winner is null");
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleEndGame(this);
    }

    public Role getWinner() {
        return winner;
    }
}

package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.enums.SceneId;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

public class S2CChangeScenePacket implements Packet<IClientPacketListener> {
    private SceneId scene;

    public S2CChangeScenePacket(@NotNull SceneId scene) {
        this.scene = scene;
    }

    public S2CChangeScenePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (scene == null) throw new EncoderException("Scene is null");

        buf.writeInt(scene.ordinal());
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        scene = SceneId.values()[buf.readInt()];

        if (scene == null) throw new DecoderException("Scene is null");
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleChangeScene(this);
    }

    public SceneId getScene() {
        return scene;
    }
}

package dev.sebastianb.owocraft.platform;

import dev.sebastianb.owocraft.CommonOwocraft;
import dev.sebastianb.owocraft.services.INetworkingHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeNetworkingHelper implements INetworkingHelper {

    private static final String PROTOCOL_VERSION = "1";
    private static final Map<ResourceLocation, SimpleChannel> CHANNELS = new HashMap<>();

    private SimpleChannel getOrCreateChannel(ResourceLocation id) {
        return CHANNELS.computeIfAbsent(id, name -> NetworkRegistry.newSimpleChannel(
                name,
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        ));
    }

    @Override
    public void registerS2CReceiver(ResourceLocation id, Consumer<FriendlyByteBuf> handler) {
        SimpleChannel channel = getOrCreateChannel(id);
        channel.registerMessage(0, FriendlyByteBuf.class, (buf, buffer) -> {
            buffer.writeBytes(buf);
        }, buf -> {
            // Need to return a new buffer or something that the decoder can use
            // Forge's SimpleChannel is a bit different. Usually you define a packet class.
            // Since we want to use FriendlyByteBuf directly:
            return new FriendlyByteBuf(buf.copy());
        }, (buf, ctxSupplier) -> {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> handler.accept(buf));
            ctx.setPacketHandled(true);
        });
    }

    @Override
    public void sendS2CPacket(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        SimpleChannel channel = getOrCreateChannel(id);
        channel.send(PacketDistributor.PLAYER.with(() -> player), buf);
    }
}

package dev.sebastianb.owocraft.platform;

import dev.sebastianb.owocraft.services.INetworkingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class FabricNetworkingHelper implements INetworkingHelper {

    @Override
    public void registerS2CReceiver(ResourceLocation id, Consumer<FriendlyByteBuf> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, handler1, buf, responseSender) -> {
            // Fabric executes networking on the network thread, we should move it to the main thread if needed.
            // But badpackets also seemed to provide the client instance.
            // Most of our activatePythonEvent already uses client.execute.
            
            // We need to copy the buffer because it will be released after this method returns
            FriendlyByteBuf bufCopy = PacketByteBufs.copy(buf);
            handler.accept(bufCopy);
        });
    }

    @Override
    public void sendS2CPacket(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        ServerPlayNetworking.send(player, id, buf);
    }
}

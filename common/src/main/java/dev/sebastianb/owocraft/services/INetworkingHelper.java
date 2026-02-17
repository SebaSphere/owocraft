package dev.sebastianb.owocraft.services;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public interface INetworkingHelper {

    /**
     * Registers a S2C packet receiver.
     */
    void registerS2CReceiver(ResourceLocation id, Consumer<FriendlyByteBuf> handler);

    /**
     * Sends a S2C packet to a specific player.
     */
    void sendS2CPacket(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf);

}

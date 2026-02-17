package dev.sebastianb.owocraft.networking.common;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;

public interface IPacket {

    String packetName();


    default Consumer<FriendlyByteBuf> receiver() {

        return null;
    }

}

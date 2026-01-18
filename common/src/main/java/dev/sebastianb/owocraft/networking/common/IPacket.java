package dev.sebastianb.owocraft.networking.common;

import lol.bai.badpackets.api.PacketReceiver;
import lol.bai.badpackets.api.play.ClientPlayContext;
import net.minecraft.network.FriendlyByteBuf;

public interface IPacket {

    String packetName();


    default PacketReceiver<ClientPlayContext, FriendlyByteBuf> receiver() {

        return null;
    }

}

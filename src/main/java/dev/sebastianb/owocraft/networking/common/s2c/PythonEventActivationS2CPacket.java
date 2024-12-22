package dev.sebastianb.owocraft.networking.common.s2c;

import dev.sebastianb.owocraft.networking.common.IPacket;
import lol.bai.badpackets.api.PacketReceiver;
import lol.bai.badpackets.api.play.ClientPlayContext;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class PythonEventActivationS2CPacket implements IPacket {


    @Override
    public String packetName() {
        return "python_event_activation";
    }

//    @Override
//    public PacketReceiver<ClientPlayContext, FriendlyByteBuf> receiver() {
//        return (ClientPlayContext handler, FriendlyByteBuf buf) -> {
//            createDynamicDimension(
//                    handler.client(), handler, buf
//            );
//        };
//    }
//
//    private static void createDynamicDimension(@NotNull Minecraft client, @NotNull ClientPlayContext handler, @NotNull FriendlyByteBuf buf) {
//
//    }

}

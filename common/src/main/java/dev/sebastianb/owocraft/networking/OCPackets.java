package dev.sebastianb.owocraft.networking;

import dev.sebastianb.owocraft.CommonOwocraft;
import dev.sebastianb.owocraft.networking.common.IPacket;
import dev.sebastianb.owocraft.networking.common.s2c.PythonEventActivationS2CPacket;
import dev.sebastianb.owocraft.services.Services;

import java.util.List;

public class OCPackets {

    // should just handle S2C packets, will need to make S2C packets a future update in these bindings
    private static final List<IPacket> PACKETS = List.of(
            new PythonEventActivationS2CPacket()
    );

    public static void register() {
        PACKETS.forEach(iPacket -> {
            Services.NETWORKING.registerS2CReceiver(CommonOwocraft.id(iPacket.packetName()), iPacket.receiver());
        });
    }


}

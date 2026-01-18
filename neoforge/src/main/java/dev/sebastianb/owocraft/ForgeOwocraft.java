package dev.sebastianb.owocraft;


import lol.bai.badpackets.impl.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("owocraft")
public class ForgeOwocraft {

    public ForgeOwocraft(IEventBus eventBus, ModContainer container) {
        CommonOwocraft.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ForgeOwocraftClient(container);
        }
    }
}
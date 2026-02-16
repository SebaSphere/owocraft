package dev.sebastianb.owocraft;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;


@Mod("owocraft")
public class ForgeOwocraft {

    public ForgeOwocraft() {
        CommonOwocraft.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ForgeOwocraftClient(ModLoadingContext.get().getActiveContainer());
        }
    }
}
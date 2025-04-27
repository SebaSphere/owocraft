package dev.sebastianb.owocraft.mixin.client;

import dev.sebastianb.owocraft.utils.HapticsIconWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {


    @Unique
    private static final ResourceLocation TEXTURE
            = ResourceLocation.fromNamespaceAndPath("owocraft", "textures/yellow_dye.png");
    @Unique
    private static final int FADE_TIME = 5000;

    @Unique
    private float yPosition = 0;

    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void injectWidget(CallbackInfo ci) {
        addRenderableWidget(new HapticsIconWidget(16,16,32,32));
    }
}

package dev.sebastianb.owocraft.mixin.client;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.sebastianb.owocraft.utils.HapticsIconWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(YACLScreen.class)
public abstract class TitleScreenMixin {

    @Unique
    HapticsIconWidget hapticsIconWidget = new HapticsIconWidget(0, 32, 32, 32);

    @Shadow
    @Final
    public YetAnotherConfigLib config;

    @Shadow @Final private Screen parent;

    @Inject(at = @At("TAIL"), method = "init")
    public void thing(CallbackInfo ci) {

    }

    @Inject(at = @At("TAIL"), method = "renderBackground")
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        YACLScreen s = ((YACLScreen) (Object) this);
        if (this.config.title().equals(Component.literal("Owocraft Config"))
            && s.tabManager.getCurrentTab().getTabTitle().equals(Component.literal("Vest Management"))) {
            hapticsIconWidget.render(guiGraphics, mouseX, mouseY, partialTick);
            hapticsIconWidget.setY(Minecraft.getInstance().screen.height - 32);
        }
    }

}

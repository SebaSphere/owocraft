package dev.sebastianb.owocraft.mixin;

import dev.isxander.yacl3.gui.YACLScreen;
import dev.sebastianb.owocraft.utils.HapticsIconWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class YACLScreenMixin {

    @Unique
    HapticsIconWidget hapticsIconWidget = new HapticsIconWidget(0, 32, 32, 32);

    @Inject(at = @At("TAIL"), method = "render")
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Screen s = ((Screen) (Object) this);
        if (s instanceof YACLScreen yaclScreen && yaclScreen.tabManager.getCurrentTab().getTabTitle().equals(Component.literal("Vest Management"))) {
            hapticsIconWidget.render(guiGraphics, mouseX, mouseY, partialTick);
            hapticsIconWidget.setY(Minecraft.getInstance().screen.height - 32);
        }
    }

}

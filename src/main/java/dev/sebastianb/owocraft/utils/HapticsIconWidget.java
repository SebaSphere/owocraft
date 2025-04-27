package dev.sebastianb.owocraft.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sebastianb.owocraft.Owocraft;
import dev.sebastianb.owocraft.client.OwocraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public class HapticsIconWidget extends AbstractWidget {

    public HapticsIconWidget(int i, int j, int k, int l) {
        super(i, j, k, l, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float f) {

        int startX = 16;
        int startY = 16;
        int width = 32;
        int height = 32;

        ResourceLocation location = null;

        switch (OwocraftClient.getConnectionStateManager().getState()) {
            case CONNECTING -> location = Owocraft.id("textures/gui/connection/connecting.png");
            case CONNECTED -> location = Owocraft.id("textures/gui/connection/connected.png");
            case DISCONNECTED -> location = Owocraft.id("textures/gui/connection/disconnected.png");
        }

        if (mouseX >= startX && mouseY >= startY && mouseX < startX + width && mouseY < startY + height) {

            // TODO: make this a translatable
            Component text
                    = Component.literal("Connection status: "
                                        + OwocraftClient.getConnectionStateManager().getState().name().toLowerCase(Locale.ROOT)
            );
            guiGraphics.renderTooltip(Minecraft.getInstance().font, text, mouseX, mouseY);
        }
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0f);
        guiGraphics.blit(location, 16,16, 32, 32, 32, 32, 32, 32 );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected boolean clicked(double d, double e) {
        return false;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return false;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {

    }
}

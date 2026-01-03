package dev.sebastianb.owocraft.utils;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sebastianb.owocraft.Owocraft;
import dev.sebastianb.owocraft.client.OwocraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HapticsIconWidget extends AbstractWidget {

    public HapticsIconWidget(int i, int j, int k, int l) {
        super(i, j, k, l, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float f) {
        ResourceLocation location = null;

        switch (OwocraftClient.getConnectionStateManager().getState()) {
            case CONNECTING -> location = Owocraft.id("textures/gui/connection/connecting.png");
            case CONNECTED -> location = Owocraft.id("textures/gui/connection/connected.png");
            case DISCONNECTED -> location = Owocraft.id("textures/gui/connection/disconnected.png");
        }

        if (mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height) {

            // TODO: make this a translatable
            Component text
                    = Component.literal("Connection status: "
                                        + OwocraftClient.getConnectionStateManager().getState().name().toLowerCase(Locale.ROOT)
            );
            guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, text.toFlatList(), mouseX, mouseY);
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, location, getX(), getY(), 1.0f, 1.0f, getWidth(), getHeight(), 32, 32);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
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

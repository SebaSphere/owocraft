package dev.sebastianb.owocraft.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.sebastianb.owocraft.client.OwocraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.*;
import java.util.List;

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


    @Inject(method = "render", at = @At(value = "TAIL"))
    private void renderCustomTexture(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        renderTexture(guiGraphics, i, j);
    }

    private void renderTexture(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        RenderSystem.enableBlend();


        ItemStack itemToRender;
        switch (OwocraftClient.getConnectionStateManager().getState()) {
            case CONNECTING -> itemToRender = Items.YELLOW_DYE.getDefaultInstance();
            case CONNECTED -> itemToRender = Items.GREEN_DYE.getDefaultInstance();
            case DISCONNECTED -> itemToRender = Items.RED_DYE.getDefaultInstance();
            default -> itemToRender = Items.WHITE_DYE.getDefaultInstance();
        }
        // check if hovering over where the item should be 16 16 32 32 and display tooltip
        int startX = 16;
        int startY = 16;
        int width = 32;
        int height = 32;

        if (mouseX >= startX && mouseY >= startY && mouseX < startX + width && mouseY < startY + height) {

            // TODO: make this a translatable
            Component text
                    = Component.literal("Connection status: "
                    + OwocraftClient.getConnectionStateManager().getState().name().toLowerCase(Locale.ROOT)
            );
            guiGraphics.renderTooltip(Minecraft.getInstance().font, text, mouseX, mouseY);
        }

        guiGraphics.renderFakeItem(itemToRender, 16, 16);
        RenderSystem.disableBlend();

    }
    

}

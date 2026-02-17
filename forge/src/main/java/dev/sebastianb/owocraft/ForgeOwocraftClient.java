package dev.sebastianb.owocraft;

import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.screen.OwocraftConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModContainer;

public class ForgeOwocraftClient {
    public ForgeOwocraftClient(ModContainer modContainer) {
        OwocraftClient.init();
        // This will use Forge's ConfigurationScreen to display this mod's configs
        modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) ->
                        OwocraftConfigScreen.createScreen(screen)));
    }
}

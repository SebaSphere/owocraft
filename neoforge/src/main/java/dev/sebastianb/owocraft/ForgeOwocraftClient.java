package dev.sebastianb.owocraft;

import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.screen.OwocraftConfigScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ForgeOwocraftClient {
    public ForgeOwocraftClient(ModContainer modContainer) {
        OwocraftClient.init();
        // This will use NeoForge's ConfigurationScreen to display this mod's configs
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parent) ->
                OwocraftConfigScreen.createScreen(parent));
    }
}

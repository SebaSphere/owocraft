package dev.sebastianb.owocraft.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.sebastianb.owocraft.client.screen.OwocraftConfigScreen;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return OwocraftConfigScreen::createScreen;
    }

}

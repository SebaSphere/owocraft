package dev.sebastianb.owocraft.client.screen;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.sebastianb.owocraft.config.OwocraftConfig;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PythonScriptInformation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class OwocraftConfigScreen {


    public static Screen createScreen(Screen parent) {
        OwocraftConfig.reload();

        var builder = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Owocraft Config")); // TODO: translatable

        var categoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Events"));

        List<PythonScriptInformation> pythonScripts
                = OwocraftClient.getPythonRunnerManager().getPythonScripts().values().stream().toList();

        for (PythonScriptInformation script : pythonScripts) {
            var groupBuilder = OptionGroup.createBuilder();
            recursiveOptionAdd(script, groupBuilder);
            categoryBuilder.group(groupBuilder.build());
        }

        builder.category(categoryBuilder.build());

        var screen = builder
                .save(() -> {
                    OwocraftConfig.HANDLER.save();
                    OwocraftClient.getPythonRunnerManager()
                            .reloadWithScript(OwocraftConfig.pythonConfigInformation);
                })
                .build()
                .generateScreen(parent);

        return screen;
    }

    public static void recursiveOptionAdd(PythonScriptInformation scriptInformation, OptionGroup.Builder groupBuilder) {
        groupBuilder.name(Component.literal(scriptInformation.eventName))
                .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Priority"))
                        .binding(
                                OwocraftConfig.HANDLER.instance().pythonConfigInformation.values().stream()
                                        .filter(info -> info.eventName.equals(scriptInformation.eventName))
                                        .findFirst().get().priority,

                                () -> OwocraftConfig.HANDLER.instance().pythonConfigInformation.values().stream()
                                        .filter(info -> info.eventName.equals(scriptInformation.eventName))
                                        .findFirst().get().priority, // getter

                                val -> {
                                    OwocraftConfig.HANDLER.instance().pythonConfigInformation.values().stream()
                                            .filter(info -> info.eventName.equals(scriptInformation.eventName))
                                            .findFirst().get().priority
                                            = val;
                                } // setter

                        )
                        .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                .range(0, 1000)
                                .formatValue(value -> Component.literal(String.valueOf(value)))
                        )
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Run Generic Event Instead"))
                        .binding(
                                OwocraftConfig.HANDLER.instance().pythonConfigInformation.values().stream()
                                        .filter(info -> info.eventName.equals(scriptInformation.eventName))
                                        .findFirst().get().runGenericEventInstead,

                                () -> OwocraftConfig.HANDLER.instance().pythonConfigInformation.values().stream()
                                        .filter(info -> info.eventName.equals(scriptInformation.eventName))
                                        .findFirst().get().runGenericEventInstead, // getter

                                val -> {
                                    OwocraftConfig.HANDLER.instance().pythonConfigInformation.values().stream()
                                            .filter(info -> info.eventName.equals(scriptInformation.eventName))
                                            .findFirst().get().runGenericEventInstead
                                            = val;
                                } // setter
                        )
                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).trueFalseFormatter())
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Should finish event"))
                        .binding(
                                OwocraftConfig.HANDLER.instance().pythonConfigInformation.values().stream()
                                        .filter(info -> info.eventName.equals(scriptInformation.eventName))
                                        .findFirst().get().shouldFinishEventFirst,

                                () -> OwocraftConfig.HANDLER.instance().pythonConfigInformation.values().stream()
                                        .filter(info -> info.eventName.equals(scriptInformation.eventName))
                                        .findFirst().get().shouldFinishEventFirst, // getter

                                val -> {
                                    OwocraftConfig.HANDLER.instance().pythonConfigInformation.values().stream()
                                            .filter(info -> info.eventName.equals(scriptInformation.eventName))
                                            .findFirst().get().shouldFinishEventFirst
                                            = val;
                                } // setter
                        )
                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).trueFalseFormatter())
                        .build());
    }

}

package dev.sebastianb.owocraft.client.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.sebastianb.owocraft.config.OwocraftConfig;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PythonScriptInformation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class OwocraftConfigScreen {


    public static Screen createScreen(Screen parent) {
        OwocraftConfig.reload();

        var builder = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Owocraft Config")); // TODO: translatable

        vestManagement(builder);

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

    private static void vestManagement(YetAnotherConfigLib.Builder builder) {
        var vestManagementCategoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Vest Management"));

        var autoConnectGroupBuilder = OptionGroup.createBuilder();

        autoConnectGroupBuilder.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Should Auto-connect"))
                .binding(
                        OwocraftConfig.shouldAutoconnect, // the default value can be changed
                        () -> OwocraftConfig.shouldAutoconnect, // getter
                        newValue -> { // setter
                            OwocraftConfig.shouldAutoconnect = newValue;
                        }
                )
                .controller(BooleanControllerBuilder::create)
                .build());

        vestManagementCategoryBuilder.group(autoConnectGroupBuilder.build());


        var ipList = ListOption.<String>createBuilder();


        ipList
                .name(Component.literal("IP Address"))
                .binding(
                        OwocraftConfig.ipAddresses, // change to mutable list
                        () -> OwocraftConfig.ipAddresses, // getter
                        newValues -> { // setter
                            System.out.println("AAA");
                            OwocraftConfig.ipAddresses = newValues;
                        }
                )
                .description(
                        OptionDescription.createBuilder()
                        .text(Component.literal("IP Addresses useable from OWO\n" + String.join("\n", OwocraftConfig.ipAddresses)))
                        .build()
                )
                .controller(StringControllerBuilder::create) // usual controllers, passed to every entry
                .initial("127.0.0.1") // when adding a new entry to the list, this is the initial value it has
                .build();

        vestManagementCategoryBuilder.group(ipList.build());

        builder.category(vestManagementCategoryBuilder.build());

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

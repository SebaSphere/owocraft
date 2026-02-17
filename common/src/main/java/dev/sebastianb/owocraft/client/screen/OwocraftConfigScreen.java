package dev.sebastianb.owocraft.client.screen;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PairedVariableArgument;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PythonScriptInformation;
import dev.sebastianb.owocraft.config.OwocraftConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OwocraftConfigScreen {

    // Persistent list for the debug variables during the screen session
    private static final ArrayList<PairedVariableArgument> pairedVariableArguments = new ArrayList<>();

    public static Screen createScreen(Screen parent) {
        OwocraftConfig.reload();
        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Owocraft Config"));

        vestManagement(builder);

        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Events"));

        List<PythonScriptInformation> pythonScripts = OwocraftClient.getPythonRunnerManager()
                .getPythonScripts().values().stream().toList();

        for (PythonScriptInformation script : pythonScripts) {
            OptionGroup.Builder groupBuilder = OptionGroup.createBuilder();
            recursiveOptionAdd(script, groupBuilder);
            categoryBuilder.group(groupBuilder.build());
        }

        builder.category(categoryBuilder.build());

        return builder.save(() -> {
            OwocraftConfig.HANDLER.save();
            OwocraftClient.getPythonRunnerManager().reloadWithScript(OwocraftConfig.pythonConfigInformation);
        }).build().generateScreen(parent);
    }

    private static void vestManagement(YetAnotherConfigLib.Builder builder) {
        ConfigCategory.Builder vestManagementCategoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Vest Management"));

        // --- DEBUG PAIRED VARIABLES ---
        OptionGroup.Builder pairedVariableBuilder = OptionGroup.createBuilder();
        pairedVariableBuilder.option(Option.<String>createBuilder()
                .name(Component.literal("Debug paired variables"))
                .description(OptionDescription.createBuilder()
                        .text(Component.literal("Format like (var1=value1;var2=value2)"))
                        .build())
                .binding(
                        // Default value / Current state as String
                        pairedVariableArguments.stream()
                                .map(t -> t.variableName() + "=" + t.object())
                                .collect(Collectors.joining(";")),
                        // Getter
                        () -> pairedVariableArguments.stream()
                                .map(t -> t.variableName() + "=" + t.object())
                                .collect(Collectors.joining(";")),
                        // Setter (Parsing the string back into the list)
                        newValue -> {
                            String[] pairs = newValue.split(";");
                            pairedVariableArguments.clear();
                            for (String pair : pairs) {
                                if (!pair.trim().isEmpty()) {
                                    String[] keyValue = pair.split("=");
                                    if (keyValue.length == 2) {
                                        String key = keyValue[0].trim();
                                        String valStr = keyValue[1].trim();
                                        Object value;
                                        try {
                                            value = Integer.parseInt(valStr);
                                        } catch (NumberFormatException e1) {
                                            try {
                                                value = Float.parseFloat(valStr);
                                            } catch (NumberFormatException e2) {
                                                value = valStr; // Fallback to String
                                            }
                                        }
                                        pairedVariableArguments.add(new PairedVariableArgument(key, value));
                                    }
                                }
                            }
                        }
                )
                .controller(StringControllerBuilder::create)
                .build());
        vestManagementCategoryBuilder.group(pairedVariableBuilder.build());

        // --- AUTO CONNECT ---
        OptionGroup.Builder autoConnectGroupBuilder = OptionGroup.createBuilder();
        autoConnectGroupBuilder.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Should Auto-connect"))
                .description(OptionDescription.createBuilder()
                        .text(Component.literal("If auto-connect is ON, IP addresses will not be connectable"))
                        .build())
                .binding(
                        OwocraftConfig.shouldAutoconnect,
                        () -> OwocraftConfig.shouldAutoconnect,
                        newValue -> OwocraftConfig.shouldAutoconnect = newValue
                )
                .controller(BooleanControllerBuilder::create)
                .build());
        vestManagementCategoryBuilder.group(autoConnectGroupBuilder.build());

        // --- IP ADDRESS LIST ---
        ListOption.Builder<String> ipList = ListOption.createBuilder();
        ipList.name(Component.literal("IP Address"))
                .binding(
                        OwocraftConfig.ipAddresses,
                        () -> OwocraftConfig.ipAddresses,
                        newValues -> {
                            OwocraftConfig.ipAddresses = newValues;
                            // Trigger connection logic on change
                            OwocraftClient.getPanamaBindingManager().getBooleanStateInvokeMultiplePassedStringMethod(
                                    "connectToVest",
                                    OwocraftConfig.ipAddresses.toArray(String[]::new)
                            );
                        }
                )
                .description(OptionDescription.createBuilder()
                        .text(Component.literal("IP Addresses useable from OWO\nScanned: " +
                                OwocraftClient.getPanamaBindingManager().getStringFromMethod("getScannedIPs")))
                        .build())
                .controller(StringControllerBuilder::create)
                .initial("127.0.0.1");

        vestManagementCategoryBuilder.group(ipList.build());

        // --- PYTHON SCRIPT MANAGEMENT ---
        OptionGroup.Builder pythonManagementGroupBuilder = OptionGroup.createBuilder()
                .name(Component.literal("Python Script Management"));

        pythonManagementGroupBuilder.option(ButtonOption.createBuilder()
                .name(Component.literal("Refresh all scripts from filesystem"))
                .description(OptionDescription.createBuilder()
                        .text(Component.literal("Reloads all python scripts from the config folder. Use this to apply changes to .py files without restarting."))
                        .build())
                .action((yaclScreen, option) -> {
                    OwocraftClient.getPythonRunnerManager().refreshAllScriptsFromFiles();
                    // Optionally save the config to sync the refreshed content to the json5 file
                    OwocraftConfig.HANDLER.save();
                })
                .build());

        vestManagementCategoryBuilder.group(pythonManagementGroupBuilder.build());

        builder.category(vestManagementCategoryBuilder.build());
    }

    public static void recursiveOptionAdd(PythonScriptInformation scriptInformation, OptionGroup.Builder groupBuilder) {
        String eventName = scriptInformation.eventName != null ? scriptInformation.eventName : "unknown";
        groupBuilder.name(Component.literal(eventName));

        // --- TEST SENSATION BUTTON ---
        groupBuilder.option(ButtonOption.createBuilder()
                .name(Component.literal("Test sensation"))
                .action((yaclScreen, option) -> {
                    // Uses the arguments parsed from the "Debug paired variables" field
                    OwocraftClient.getPythonRunnerManager().runPythonScript(
                            scriptInformation.modID,
                            scriptInformation.eventName,
                            pairedVariableArguments.toArray(PairedVariableArgument[]::new)
                    );
                })
                .build());

        // --- PRIORITY ---
        groupBuilder.option(Option.<Integer>createBuilder()
                .name(Component.literal("Priority"))
                .binding(
                        scriptInformation.priority,
                        () -> OwocraftConfig.pythonConfigInformation.get(scriptInformation.modID + ":" + scriptInformation.eventName).priority,
                        val -> OwocraftConfig.pythonConfigInformation.get(scriptInformation.modID + ":" + scriptInformation.eventName).priority = val
                )
                .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                        .range(0, 1000)
                        .formatValue(v -> Component.literal(String.valueOf(v))))
                .build());

        // --- RUN GENERIC EVENT ---
        groupBuilder.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Run Generic Event Instead"))
                .binding(
                        scriptInformation.runGenericEventInstead,
                        () -> OwocraftConfig.pythonConfigInformation.get(scriptInformation.modID + ":" + scriptInformation.eventName).runGenericEventInstead,
                        val -> OwocraftConfig.pythonConfigInformation.get(scriptInformation.modID + ":" + scriptInformation.eventName).runGenericEventInstead = val
                )
                .controller(opt -> BooleanControllerBuilder.create(opt).trueFalseFormatter())
                .build());

        // --- SHOULD FINISH EVENT ---
        groupBuilder.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Should finish event"))
                .binding(
                        scriptInformation.shouldFinishEventFirst,
                        () -> OwocraftConfig.pythonConfigInformation.get(scriptInformation.modID + ":" + scriptInformation.eventName).shouldFinishEventFirst,
                        val -> OwocraftConfig.pythonConfigInformation.get(scriptInformation.modID + ":" + scriptInformation.eventName).shouldFinishEventFirst = val
                )
                .controller(opt -> BooleanControllerBuilder.create(opt).trueFalseFormatter())
                .build());
    }
}
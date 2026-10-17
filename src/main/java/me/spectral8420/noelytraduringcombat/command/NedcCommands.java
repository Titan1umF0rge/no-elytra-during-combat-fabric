package me.spectral8420.noelytraduringcombat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.spectral8420.noelytraduringcombat.config.ModConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

public class NedcCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nedcreload")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(NedcCommands::executeReload));

        dispatcher.register(Commands.literal("nedcsave")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(NedcCommands::executeSave));

        dispatcher.register(Commands.literal("nedc")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("reload").executes(NedcCommands::executeReload))
                .then(Commands.literal("save").executes(NedcCommands::executeSave)));
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        ModConfig.get().load();
        String message = ModConfig.get().getMessage("reload", true);
        context.getSource().sendSuccess(() -> Component.literal(message), true);
        return 1;
    }

    private static int executeSave(CommandContext<CommandSourceStack> context) {
        ModConfig.get().save();
        String message = ModConfig.get().getMessage("save", true);
        context.getSource().sendSuccess(() -> Component.literal(message), true);
        return 1;
    }
}

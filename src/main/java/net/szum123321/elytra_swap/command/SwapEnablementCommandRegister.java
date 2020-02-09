package net.szum123321.elytra_swap.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.szum123321.elytra_swap.PlayerSwapDataHandler;

public class SwapEnablementCommandRegister {
    public static void register(){
        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register(CommandManager.literal("swap")
                .then(CommandManager.argument("Operation", SwapEnableArgumentType.Int())
                        .executes(SwapEnablementCommandRegister::execute)
                ).executes(ctx -> {
                    ctx.getSource().getPlayer().sendMessage(new TranslatableText("Available options are: enable, disable. Now Elytra Swap is: %s", PlayerSwapDataHandler.get(ctx.getSource().getPlayer()) ? "Enabled" : "Disabled"));
                    return 1;
                })
        ));
    }

    private static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        switch(SwapEnableArgumentType.getInteger(ctx, "Operation")){
            case 1:
                PlayerSwapDataHandler.set(ctx.getSource().getPlayer(), true);
                break;

            case 2:
                PlayerSwapDataHandler.set(ctx.getSource().getPlayer(), false);
                break;
        }

        ctx.getSource().getPlayer().sendMessage(new TranslatableText("Elytra Swap in %s", PlayerSwapDataHandler.get(ctx.getSource().getPlayer()) ? "Enabled" : "Disabled"));

        return 1;
    }
}

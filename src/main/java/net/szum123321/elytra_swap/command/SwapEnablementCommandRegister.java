/*
    Automatic elytra replacement with chestplace
    Copyright (C) 2020 Szum123321

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package net.szum123321.elytra_swap.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.szum123321.elytra_swap.core.PlayerSwapDataHandler;

public class SwapEnablementCommandRegister {
    public static void register(){
        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register(CommandManager.literal("swap")
                .then(CommandManager.argument("Operation", BoolArgumentType.bool())//SwapEnableArgumentType.Int())
                        .executes(SwapEnablementCommandRegister::execute)
                ).executes(ctx -> {
                    ctx.getSource().getPlayer().sendMessage(new TranslatableText("Available options are: true(enable), false(disable). Now Elytra Swap is: %s", PlayerSwapDataHandler.get(ctx.getSource().getPlayer()) ? "Enabled" : "Disabled"));
                    return 1;
                })
        ));
    }

    private static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerSwapDataHandler.set(ctx.getSource().getPlayer(), BoolArgumentType.getBool(ctx, "Operation"));

        ctx.getSource().getPlayer().sendMessage(new TranslatableText("Elytra Swap in %s", PlayerSwapDataHandler.get(ctx.getSource().getPlayer()) ? "Enabled" : "Disabled"));

        return 1;
    }
}

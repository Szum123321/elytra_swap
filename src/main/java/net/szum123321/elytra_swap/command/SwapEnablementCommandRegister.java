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
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.szum123321.elytra_swap.ElytraSwap;

public class SwapEnablementCommandRegister {
	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("swap")
			.then(CommandManager.argument("Operation", BoolArgumentType.bool())
					.executes(SwapEnablementCommandRegister::execute)
			).executes(ctx -> {
				ctx.getSource().getPlayer().sendMessage(new TranslatableText("Available options are: true(enable), false(disable). Now Elytra Swap is: %s", ElytraSwap.serverSwapStateHandler.getSwapState(ctx.getSource().getPlayer()) ? "Enabled" : "Disabled"),false);
				return 1;
			})
	));
	}

	private static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ElytraSwap.serverSwapStateHandler.setSwapState(ctx.getSource().getPlayer(), BoolArgumentType.getBool(ctx, "Operation"), true);

		ctx.getSource().getPlayer().sendMessage(new TranslatableText("Elytra Swap is %s", ElytraSwap.serverSwapStateHandler.getSwapState(ctx.getSource().getPlayer()) ? "Enabled" : "Disabled"), false);

		return 1;
	}
}
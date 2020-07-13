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

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.emi.trinkets.api.Trinket;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.LoreHelper;

public class SwapEnablementCommandRegister {
	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
				(LiteralArgumentBuilder)(CommandManager.literal("swap")
				.then(CommandManager.literal("apply").then(
						(RequiredArgumentBuilder)CommandManager.argument("value", IntegerArgumentType.integer(0, 10))
								.executes(SwapEnablementCommandRegister::executeApply)
				))
				).then(CommandManager.literal("toggle")
						.then(CommandManager.literal("enable").executes(ctx -> executeToggle(ctx, true)))
						.then(CommandManager.literal("disable").executes(ctx -> executeToggle(ctx, false)))
				)
			)
		);
	}


	private static int executeToggle(CommandContext<ServerCommandSource> ctx, boolean state) throws CommandSyntaxException {
		ElytraSwap.serverSwapStateHandler.setSwapState(ctx.getSource().getPlayer(), state, true);

		ctx.getSource().sendFeedback(new LiteralText("Elytra Swap is: " + (ElytraSwap.serverSwapStateHandler.getSwapState(ctx.getSource().getPlayer()) ? "enabled" : "disabled")), false);

		return 1;
	}

	private static int executeApply(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		int a = IntegerArgumentType.getInteger(ctx, "value");
		ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();

		ItemStack stack = playerEntity.getMainHandStack();

		if(stack.isEmpty())
			stack = playerEntity.getOffHandStack();

		if(!stack.isEmpty()) {
			if(stack.getItem() instanceof Trinket || stack.getItem() instanceof ElytraItem) {
				LoreHelper.apply(stack, a);
			}
		}

		return 1;
	}

	private static int executeApplyHelp(CommandContext<ServerCommandSource> ctx) {
		return 1;
	}

	private static int executeToggleHelp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		//ctx.getSource().sendFeedback(new LiteralText("Available options are: true(enable), false(disable). Now Elytra Swap is: " + (ElytraSwap.serverSwapStateHandler.getSwapState(ctx.getSource().getPlayer()) ? "enabled" : "disabled")),false);
		return 1;
	}

	private static int executeHelp(CommandContext<ServerCommandSource> ctx) {
		//ctx.getSource().sendFeedback(new LiteralText("Available commands are: toggle, apply"), false);
		return 1;
	}
}
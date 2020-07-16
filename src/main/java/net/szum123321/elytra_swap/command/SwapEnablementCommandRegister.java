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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.emi.trinkets.api.Trinket;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.szum123321.elytra_swap.ElytraSwap;


public class SwapEnablementCommandRegister {
	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
				CommandManager.literal("swap")
				.then(CommandManager.literal("apply").then(
						CommandManager.argument("value", IntegerArgumentType.integer(0, 10))
								.executes(SwapEnablementCommandRegister::executeApply))
						.executes(SwapEnablementCommandRegister::executeApplyHelp)
				).then(CommandManager.literal("toggle")
						.then(CommandManager.literal("enable").executes(ctx -> executeToggle(ctx, true)))
						.then(CommandManager.literal("disable").executes(ctx -> executeToggle(ctx, false)))
						.executes(SwapEnablementCommandRegister::executeToggleHelp)
				).executes(SwapEnablementCommandRegister::executeHelp)
			)
		);
	}


	private static int executeToggle(CommandContext<ServerCommandSource> ctx, boolean state) throws CommandSyntaxException {
		ElytraSwap.serverSwapStateHandler.setSwapState(ctx.getSource().getPlayer(), state, true);

		ctx.getSource().sendFeedback(new LiteralText("Elytra Swap is: " + (ElytraSwap.serverSwapStateHandler.getSwapState(ctx.getSource().getPlayer()) ? "enabled" : "disabled")), false);

		return 1;
	}

	private static int executeApply(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		int value = IntegerArgumentType.getInteger(ctx, "value");

		ItemStack stack = ctx.getSource().getPlayer().getMainHandStack();

		if(!stack.isEmpty()) {
			if(stack.getItem() instanceof ElytraItem ||
					(stack.getItem() instanceof ArmorItem && ((ArmorItem)stack.getItem()).getSlotType() == EquipmentSlot.CHEST) ||
					(ElytraSwap.hasTrinkets && stack.getItem() instanceof Trinket && ((Trinket)stack.getItem()).canWearInSlot("chest", "cape"))) {
				LoreHelper.apply(stack, value);
			} else {
				ctx.getSource().sendFeedback(new LiteralText("Sorry, but you cannot set priority on this item!").formatted(Formatting.RED), false);
			}
		} else {
			ctx.getSource().sendFeedback(new LiteralText("There is no item in your hand right now."), false);
		}

		return 1;
	}

	private static int executeApplyHelp(CommandContext<ServerCommandSource> ctx) {
		ctx.getSource().sendFeedback(new LiteralText("With this command you can set swap priority for an item in your hand."), false);
		ctx.getSource().sendFeedback(new LiteralText("Values range from 0 (removes priority), to 10 (max)"), false);

		return 1;
	}

	private static int executeToggleHelp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ctx.getSource().sendFeedback(new LiteralText("This command enables or disabled swapping and firework takeoff feature."), false);
		ctx.getSource().sendFeedback(new LiteralText("Available options are: enable, disable. Now, Elytra Swap is: " + (ElytraSwap.serverSwapStateHandler.getSwapState(ctx.getSource().getPlayer()) ? "enabled" : "disabled") + "."),false);

		return 1;
	}

	private static int executeHelp(CommandContext<ServerCommandSource> ctx) {
		ctx.getSource().sendFeedback(new LiteralText("Available commands are: toggle, apply."), false);

		return 1;
	}
}
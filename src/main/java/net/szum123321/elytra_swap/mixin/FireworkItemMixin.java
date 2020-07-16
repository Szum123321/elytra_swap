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

package net.szum123321.elytra_swap.mixin;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.inventory.FlatInventory;
import net.szum123321.elytra_swap.handlers.TakeoffHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkItem.class)
public abstract class FireworkItemMixin extends Item {
	public FireworkItemMixin(Settings settings) {
		super(settings);
	}

	// function below gets invoked only when player uses firework while standing on ground and clicks in the air
	@Inject(method = "use", at = @At(value = "RETURN", ordinal = 1))
	private void fireworkUsageHandler(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci) {
		if (ElytraSwap.CONFIG.useFireworks.getState() &&
				player instanceof ServerPlayerEntity &&
				player.isOnGround() &&
				TakeoffHandler.checkSpaceOverPlayer(player, ElytraSwap.CONFIG.requiredHeightAbovePlayer) &&
				ElytraSwap.serverSwapStateHandler.getSwapState(player) &&
				(ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE) || ElytraSwap.CONFIG.noModPlayersHandlingMethod == 1) &&
				new FlatInventory(player).hasItem(Items.ELYTRA)) {

			TakeoffHandler.sendUpdate(world, player, hand);
		}
	}
}
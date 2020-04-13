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
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.core.FlatInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkItem.class)
public abstract class TakeoffHandler extends Item {
	public TakeoffHandler(Settings settings) {
		super(settings);
	}

	@Inject(method = "use", at = @At(value = "RETURN", ordinal = 1))
	private void fireworkUsageHandler(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci) {
		if (player instanceof ServerPlayerEntity &&
				player.onGround &&
				checkSpaceOverPlayer(player, ElytraSwap.config.requiredHeightAbovePlayer) &&
				ElytraSwap.playerSwapDataHandler.get(player) &&
				(ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE) || ElytraSwap.config.noModPlayersHandlingMethod == 1) &&
				ElytraSwap.inventoryController.doesPlayerHasElytra(new FlatInventory(player))) {
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,
					new EntityVelocityUpdateS2CPacket(player.getEntityId(),
							new Vec3d(-Math.sin(Math.toRadians(player.yaw)) * ElytraSwap.config.kickSpeed,
									ElytraSwap.config.kickSpeed,
									Math.cos(Math.toRadians(player.yaw)) * ElytraSwap.config.kickSpeed
							)
					)
			);

			FireworkEntity firework = new FireworkEntity(world, player.getMainHandStack(), player);

			world.spawnEntity(firework);
/*
			world.getEntities(player, new Box(player.getBlockPos().down(16).north(16).east(16), player.getBlockPos().up(16).south(16).west(16))).stream().forEach(
					(e)->{
						ServerSidePacketRegistry.INSTANCE.sendToPlayer((PlayerEntity)e, new EntitySpawnS2CPacket(
										firework.getEntityId(),
										firework.getUuid(),
										firework.getX(),
										firework.getY(),
										firework.getZ(),
										firework.pitch,
										firework.yaw,
										firework.getType(),
										firework.getEntityId(),
										firework.getVelocity()
								)
						);
					}
			);
*/
			if (!player.isCreative())
				player.getMainHandStack().decrement(1);
		}
	}


	private boolean checkSpaceOverPlayer(PlayerEntity player, int requiredHeight) {
		for (int i = 0; i <= requiredHeight; i++) {
			if (player.world.getBlockState(player.getBlockPos().up(i)).getMaterial().isSolid())
				return false;
		}

		return true;
	}
}
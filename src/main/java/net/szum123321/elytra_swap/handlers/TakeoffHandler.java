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

package net.szum123321.elytra_swap.handlers;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.inventory.FlatInventory;
import net.szum123321.elytra_swap.inventory.InventoryHelper;

public class TakeoffHandler {
	public static void sendUpdate(World world, PlayerEntity player, Hand hand) {
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,
				new EntityVelocityUpdateS2CPacket(player.getEntityId(),
						new Vec3d(-Math.sin(Math.toRadians(player.yaw)) * ElytraSwap.CONFIG.kickSpeed,
								ElytraSwap.CONFIG.kickSpeed * (ElytraSwap.CONFIG.horizontalMode.getState() ? -Math.sin(Math.toRadians(player.pitch)) : 1),
								Math.cos(Math.toRadians(player.yaw)) * ElytraSwap.CONFIG.kickSpeed
						)
				)
		);

		world.spawnEntity(new FireworkRocketEntity(world, player.getStackInHand(hand), player));

		if(ElytraSwap.CONFIG.horizontalMode.getState()) {
			if(ElytraSwap.CONFIG.globalSwapEnable.getState()) {
				InventoryHelper.replaceChestplateWithElytra(new FlatInventory(player));
				player.startFallFlying();
			}

			player.jump();
		}

		if (!player.isCreative())
			player.getStackInHand(hand).decrement(1);
	}

	private static boolean checkSpaceOverPlayer(PlayerEntity player, int requiredHeight) {
		BlockPos.Mutable blockPos = player.getBlockPos().mutableCopy();

		for (int i = 2; i <= requiredHeight; i++) {
			if (player.world.getBlockState(blockPos.offset(Direction.UP, i)).getMaterial().isSolid())
				return false;
		}

		return true;
	}
}

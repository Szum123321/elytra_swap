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
import net.szum123321.elytra_swap.mixin.EntitySetFlagInvoker;

public class TakeoffHandler {
	static float takeoffSpeed = ElytraSwap.CONFIG.kickSpeed * (ElytraSwap.CONFIG.horizontalMode.getState() ? 0.5F : 1);//performed once instead of every time.
	public static void sendUpdate(World world, PlayerEntity player, Hand hand) {
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,
				new EntityVelocityUpdateS2CPacket(player.getEntityId(),
						new Vec3d(takeoffSpeed * -Math.sin(Math.toRadians(player.yaw)),
								takeoffSpeed * -Math.sin(Math.toRadians(player.pitch)),
								takeoffSpeed * Math.cos(Math.toRadians(player.yaw))
						)
				)
		);

		FireworkRocketEntity firework = new FireworkRocketEntity(player.world, player.getStackInHand(hand), player);
		world.spawnEntity(firework);

		if(ElytraSwap.CONFIG.globalSwapEnable.getState() && ElytraSwap.CONFIG.horizontalMode.getState()) {
			InventoryHelper.replaceChestplateWithElytra(new FlatInventory(player));
			((EntitySetFlagInvoker)player).invokeSetFlag(7, false);
		}

		if (!player.isCreative())
			player.getStackInHand(hand).decrement(1);
		
		if(ElytraSwap.CONFIG.horizontalMode.getState() )//this might be too fast, the player might not be in the air yet. might have to send in packet.
			player.jump();//I believe this would work as opposed to .setJumping(true), could be wrong. 		
	}

	public static boolean checkSpaceOverPlayer(PlayerEntity player, int requiredHeight) {
		BlockPos.Mutable blockPos = player.getBlockPos().mutableCopy();

		for (int i = 2; i <= requiredHeight; i++) {
			if (player.world.getBlockState(blockPos.offset(Direction.UP, i)).getMaterial().isSolid())
				return false;
		}

		return true;
	}
}

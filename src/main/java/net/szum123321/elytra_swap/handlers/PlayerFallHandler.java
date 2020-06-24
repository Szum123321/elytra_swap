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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.szum123321.elytra_swap.inventory.FlatInventory;
import net.szum123321.elytra_swap.inventory.InventoryController;
import net.szum123321.elytra_swap.mixin.EntitySetFlagInvoker;

/*
	This class takes care of checking if we should swap elytra or not.
*/
public class PlayerFallHandler {
	public static void handleFalling(ServerPlayerEntity playerEntity, double heightDifference) {
		FlatInventory flatInventory = new FlatInventory(playerEntity);

		if (InventoryController.doesPlayerHaveElytra(flatInventory)) {  // this line checks if player has elytra
			if (!playerEntity.isOnGround() && !playerEntity.isClimbing() && !playerEntity.isTouchingWater()) {  //while this line makes sure that player is in the air, is not climbing and is not swimming
				if (heightDifference < 0 && getFallHeight(playerEntity) > 5) { // and here we check i player is falling down and there are at least 5 blocks of empty space below him
					InventoryController.replaceChestPlateWithElytra(flatInventory);
					((EntitySetFlagInvoker)playerEntity).invokeSetFlag(7, true);    // thanks to this line you do not have to press space in order to start gliding
				}
			} else {
				InventoryController.replaceElytraWithChestPlate(flatInventory);

				((EntitySetFlagInvoker)playerEntity).invokeSetFlag(7, false);
			}
		}
	}

	//  Thx magneticflux!
	private static float getFallHeight(PlayerEntity player) {
		BlockPos.Mutable temp = new BlockPos.Mutable(player.getX(), Math.min(player.getY(), player.world.getHeight()), player.getZ());

		while (true) {
			if (temp.getY() < 0)
				return Float.POSITIVE_INFINITY; // You can fall "forever"
			else if (player.world.getBlockState(temp).getMaterial().isSolid())
				return (float) (player.getY() - temp.getY()); // There's some solid block beneath you
			else
				temp.setY(temp.getY() - 1); // We're still looking, go down again
		}
	}
}

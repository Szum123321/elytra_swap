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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.core.FlatInventory;
import net.szum123321.elytra_swap.core.InventoryController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
	This class takes care of checking if we should swap elytra or not.
*/

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerFallCapture extends LivingEntity {
	protected PlayerFallCapture(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

	@Inject(method = "handleFall", at = @At("RETURN"))  //No more nasty overrides!
	private void fallingHandler(double heightDifference, boolean onGround, CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

		if (ElytraSwap.serverSwapStateHandler.getSwapState(player) &&
				(ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE) ||
						ElytraSwap.config.noModPlayersHandlingMethod > 0)) {

			FlatInventory flatInventory = new FlatInventory(player);

			if (InventoryController.doesPlayerHaveElytra(flatInventory)) {  // this line checks if player has elytra
				if (!onGround && !player.isClimbing() && !player.isSwimming()) {  //while this line makes sure that player is in the air, is not climbing and is not swimming
					if (heightDifference < 0 && getFallHeight(player.getBlockPos()) > 4) { // and here we check i player is falling down and there are at least 5 blocks of empty space below him
						InventoryController.replaceChestPlateWithElytra(flatInventory);
						setFlag(7, true);    // thanks to this line you do not have to press space in order to start gliding
					}
				} else {
					InventoryController.replaceElytraWithChestPlate(flatInventory);
					setFlag(7, false);
				}
			}
		}
	}

	//  Thx magneticflux!
	private float getFallHeight(BlockPos currentPosition) {
		try (BlockPos.PooledMutable temp = BlockPos.PooledMutable.get(currentPosition.getX(), Math.min(currentPosition.getY(), world.getHeight()), currentPosition.getZ())) {
			while (true) {
				if (temp.getY() < 0)
					return Float.POSITIVE_INFINITY; // You can fall "forever"
				else if (world.getBlockState(temp).getMaterial().isSolid())
					return currentPosition.getY() - temp.getY(); // There's some solid block beneath you
				else
					temp.setY(temp.getY() - 1); // We're still looking, go down again
			}
		}
	}
}

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
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.core.FlatInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class PlayerFallCapture extends LivingEntity {
    protected PlayerFallCapture(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        fallingHandler(heightDifference, onGround, landedPosition);
        super.fall(heightDifference, onGround, landedState, landedPosition);
    }

    private void fallingHandler(double heightDifference, boolean onGround, BlockPos landedPosition) {
        if ((Object) this instanceof ServerPlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;

            if (ElytraSwap.playerSwapDataHandler.get(player) &&
                    (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE) ||
                            ElytraSwap.config.noModPlayersHandlingMethod > 0)) {

                FlatInventory flatInventory = new FlatInventory(player);

                if(ElytraSwap.inventoryController.doesPlayerHasElytra(flatInventory)) {
                    if (!onGround && !player.isClimbing() && !player.isSwimming()) {
                        if (heightDifference < 0 && getFallHeight(landedPosition) > 5) {
                            ElytraSwap.inventoryController.replaceChestPlateWithElytra(flatInventory);
                            setFlag(7, true);    // thanks to this line you do not have to press space in order to start gliding
                        }
                    } else {
                        ElytraSwap.inventoryController.replaceElytraWithChestPlate(flatInventory);
                        setFlag(7, false);
                    }
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

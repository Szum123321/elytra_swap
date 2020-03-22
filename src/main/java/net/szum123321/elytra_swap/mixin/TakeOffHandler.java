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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.core.PlayerSwapDataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkItem.class)
public abstract class TakeOffHandler extends Item {
    public TakeOffHandler(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void fireworkUsageHandler(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci){
        if(player instanceof ServerPlayerEntity && ElytraSwap.inventoryController.doesPlayerHaveElytra(player) && checkSpaceOverPlayer(player, 15) &&
                player.onGround && PlayerSwapDataHandler.get(player) && !player.isFallFlying()){

            if(ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE) || ElytraSwap.config.noModPlayersHandlingMethod == 1){
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new EntityVelocityUpdateS2CPacket(player.getEntityId(), new Vec3d(-Math.sin(Math.toRadians(player.yaw)) * ElytraSwap.config.kickSpeed, ElytraSwap.config.kickSpeed, Math.cos(Math.toRadians(player.yaw)) * ElytraSwap.config.kickSpeed)));

                world.spawnEntity(new FireworkEntity(world, player.getMainHandStack(), player));

                if(!player.isCreative())
                    player.getMainHandStack().decrement(1);
            }
        }
    }

    private static boolean checkSpaceOverPlayer(PlayerEntity player, int requiredHeight){
        for(int i = (int)player.getY(); i <= (int)player.getY() + requiredHeight; i++){
            if(player.world.getBlockState(new BlockPos(player.getX(), i, player.getZ())).getMaterial().isSolid())
                return false;
        }

        return true;
    }
}

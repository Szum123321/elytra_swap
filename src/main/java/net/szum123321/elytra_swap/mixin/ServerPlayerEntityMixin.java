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

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.handlers.PlayerFallHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
	public ServerPlayerEntityMixin(World world, BlockPos blockPos, GameProfile gameProfile) {
		super(world, blockPos, gameProfile);
	}

	@Inject(method = "handleFall", at = @At("RETURN"))  //No more nasty overrides!
	private void fallHandler(double heightDifference, boolean onGround, CallbackInfo ci) {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;

		if (ElytraSwap.serverSwapStateHandler.getSwapState(serverPlayerEntity) &&
				(ServerSidePacketRegistry.INSTANCE.canPlayerReceive(serverPlayerEntity, ElytraSwap.DUMMY_PACKAGE) ||
						ElytraSwap.CONFIG.noModPlayersHandlingMethod > 0) &&
				ElytraSwap.CONFIG.globalSwapEnable.getState()) {

			PlayerFallHandler.handleFalling(serverPlayerEntity, heightDifference, onGround);
		}
	}
}

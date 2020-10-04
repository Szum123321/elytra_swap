package net.szum123321.elytra_swap.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerHelper {
    public static boolean isOnSurfaceOfWater(World world, PlayerEntity playerEntity) {
        BlockPos playerPos = playerEntity.getBlockPos();

        return !world.getFluidState(playerPos).isEmpty() && world.getFluidState(playerPos.up()).isEmpty();
    }
}

package net.szum123321.elytra_swap;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TakeOffHandler {
    public static void onItemUseRegister(){
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if(player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET  && checkIfPlayerHasElytra(player)){
                if(checkSpaceOverPlayer(player, 15) && player.onGround){
                    if(!player.isCreative())
                        player.getMainHandStack().decrement(1);

                    player.addVelocity(0.0, 1.5, 0.0);

                    world.spawnEntity(new FireworkEntity(world, player.getMainHandStack(), player));
                }
            }
            return ActionResult.PASS;
        });
    }

    private static boolean checkSpaceOverPlayer(PlayerEntity player, int requiredHeight){
        World world = player.world;

        for(int i = (int)player.y; i <= (int)player.y + requiredHeight; i++){
            if(world.getBlockState(new BlockPos(player.x, i ,player.z)).getBlock() != Blocks.AIR){
                return false;
            }
        }

        return true;
    }

    private static boolean checkIfPlayerHasElytra(PlayerEntity player){
        for(int i = 0; i < player.inventory.main.size(); i++){
            if(player.inventory.main.get(i).getItem() == Items.ELYTRA)
                return true;
        }

        for(int i = 0; i < player.inventory.armor.size(); i++){
            if(player.inventory.main.get(i).getItem() == Items.ELYTRA)
                return true;
        }

        return false;
    }
}

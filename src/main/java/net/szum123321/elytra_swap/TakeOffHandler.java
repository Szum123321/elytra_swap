package net.szum123321.elytra_swap;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class TakeOffHandler {
    public static void onItemUseRegister(){
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if(player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET  && checkIfPlayerHasElytra(player) && player instanceof ServerPlayerEntity){
                if(checkSpaceOverPlayer(player, 15) && player.onGround){
                    boolean canExecute = true;

                    if(world.isClient){  //When server is local (a.k.a singleplayer)
                        player.addVelocity(-Math.sin(Math.toRadians(player.yaw)) * 1.2, 1.5, Math.cos(Math.toRadians(player.yaw)) * 1.2);
                    }else { //Server is remote
                        if (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.KICK_PLAYER_INTO_AIR)) {  //player has mod installed
                            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ElytraSwap.KICK_PLAYER_INTO_AIR, passedData);
                        }else{
                            canExecute = false;
                        }
                    }

                    if(canExecute){
                        world.spawnEntity(new FireworkEntity(world, player.getMainHandStack(), player));

                        if(!player.isCreative())
                            player.getMainHandStack().decrement(1);
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

    private static boolean checkSpaceOverPlayer(PlayerEntity player, int requiredHeight){
        for(int i = (int)player.y; i <= (int)player.y + requiredHeight; i++){
            if(player.world.getBlockState(new BlockPos(player.x, i, player.z)).getBlock() != Blocks.AIR){
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
            if(player.inventory.armor.get(i).getItem() == Items.ELYTRA)
                return true;
        }

        return false;
    }
}

package net.szum123321.elytra_swap;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;

public class TakeOffHandler {
    public static void onItemUseRegister(){
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if(player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET  && checkIfPlayerHasElytra(player) && player instanceof ServerPlayerEntity &&
               checkSpaceOverPlayer(player, 15) && player.onGround && PlayerSwapDataHandler.get(player)){

                boolean canExecute = true;

                if(world.isClient){  //When server is local (a.k.a singleplayer)
                    player.addVelocity(-Math.sin(Math.toRadians(player.yaw)) * ElytraSwap.config.kickSpeed, ElytraSwap.config.kickSpeed, Math.cos(Math.toRadians(player.yaw)) * ElytraSwap.config.kickSpeed);
                }else { //Server is remote
                    if (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE)) {  //player has mod installed
                        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                        passedData.writeFloat(ElytraSwap.config.kickSpeed);
                        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ElytraSwap.KICK_PLAYER_INTO_AIR, passedData);
                    }else{
                        if(ElytraSwap.config.noModPlayersHandlingMethode == 1){
                            player.teleport(player.getX(), player.getY() + (ElytraSwap.config.kickSpeed * 10), player.getZ());
                        }else{
                            canExecute = false;
                        }
                    }
                }

                if(canExecute){
                    world.spawnEntity(new FireworkEntity(world, player.getMainHandStack(), player));

                    if(!player.isCreative())
                        player.getMainHandStack().decrement(1);
                }

            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
    }

    private static boolean checkSpaceOverPlayer(PlayerEntity player, int requiredHeight){
        for(int i = (int)player.getY(); i <= (int)player.getY() + requiredHeight; i++){
            if(player.world.getBlockState(new BlockPos(player.getX(), i, player.getZ())).getBlock() != Blocks.AIR){
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

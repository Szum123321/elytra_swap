package net.szum123321.elytra_swap.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.PlayerSwapDataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkItem.class)
public abstract class TakeOffHandler extends Item {
    public TakeOffHandler(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At(value = "HEAD"))
    private void fireworkUsageHandler(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ItemStack> ci){
        if(player instanceof ServerPlayerEntity && checkIfPlayerHasElytra(player) && checkSpaceOverPlayer(player, 15) &&
                player.onGround && PlayerSwapDataHandler.get(player)){

            boolean canExecute = true;

            if(world.isClient){  //When server is local (a.k.a singleplayer)
                player.addVelocity(-Math.sin(Math.toRadians(player.yaw)) * ElytraSwap.config.kickSpeed, ElytraSwap.config.kickSpeed, Math.cos(Math.toRadians(player.yaw)) * ElytraSwap.config.kickSpeed);
            }else { //Server is remote
                if (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE)) {  //player has mod installed
                    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                    passedData.writeFloat(ElytraSwap.config.kickSpeed);
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ElytraSwap.KICK_PLAYER_INTO_AIR, passedData);
                }else{
                    if(ElytraSwap.config.noModPlayersHandlingMethod == 1){
                        player.teleport(player.x, player.y + (ElytraSwap.config.kickSpeed * 10), player.z);
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
    }

    private static boolean checkSpaceOverPlayer(PlayerEntity player, int requiredHeight){
        for(int i = (int)player.y; i <= (int)player.y + requiredHeight; i++){
            if(player.world.getBlockState(new BlockPos(player.x, i, player.z)).getMaterial().isSolid())
                return false;
        }

        return true;
    }

    private static boolean checkIfPlayerHasElytra(PlayerEntity player){
        return player.inventory.contains(new ItemStack(Items.ELYTRA));
    }
}

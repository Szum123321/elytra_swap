package net.szum123321.elytra_swap.mixin;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.PlayerSwapDataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class PlayerFallCapture {
    @Shadow
    public World world;

    @Shadow
    protected abstract void setFlag(int index, boolean value);

    @Shadow
    protected abstract boolean getFlag(int index);


    @Inject(at = @At("HEAD"), method = "fall")
    private void fallingHanlder(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo info){
        if((Object)this instanceof ServerPlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;

            if(!PlayerSwapDataHandler.get(player))
                return;

            if(!onGround){
                if (heightDifference < 0 && getFallHeight(landedPosition) > 5 && (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE) || ElytraSwap.config.noModPlayersHandlingMethode > 0)) {
                    replaceArmorWithElytra(player);
                    setSevenFlagState(true);    // thanks to this line you do not have to press space in order to start gliding
                }
            }else if(getFlag(7) && (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE) || ElytraSwap.config.noModPlayersHandlingMethode > 0)){
                replaceElytraWithArmor(player);
                setSevenFlagState(false);
            }
        }
    }

    private void setSevenFlagState(boolean val){
        this.setFlag(7, val);
    }

    private void replaceElytraWithArmor(PlayerEntity player){
        if(player.inventory.armor.get(2).getItem() == Items.ELYTRA){
            for(int i = 0; i < player.inventory.main.size(); i++){
                if(player.inventory.main.get(i).getItem().toString().toLowerCase().contains("chestplate")){  //kinda sketchy but should make this compatible with modded armor
                    ItemStack elytra = player.inventory.armor.get(2);
                    player.inventory.armor.set(2, player.inventory.main.get(i));
                    player.inventory.main.set(i, elytra);
                }
            }
        }
    }

    private void replaceArmorWithElytra(PlayerEntity player){
        for (int i = 0; i < player.inventory.main.size(); i++){
            if(player.inventory.main.get(i).getItem() == Items.ELYTRA){
                ItemStack chestplate = player.inventory.armor.get(2);

                player.inventory.armor.set(2, player.inventory.main.get(i));
                player.inventory.main.set(i, chestplate);

                return;
            }
        }
    }

    private int getFallHeight(BlockPos currentPosition){
        int height = currentPosition.getY();

        while(world.getBlockState(new BlockPos(currentPosition.getX(), height, currentPosition.getZ())).getBlock() == Blocks.AIR && height > -1){
            height--;
        }

        if(height <= -1){
            System.out.println("WTF! Why are you trying to glide below bedrock?");
            return -Math.abs(currentPosition.getY()) * 2; //even if you would fly below bedrock it should work :)
        }
        return currentPosition.getY() - height;
    }

}

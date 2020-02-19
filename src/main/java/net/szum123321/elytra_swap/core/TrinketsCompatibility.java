package net.szum123321.elytra_swap.core;

import dev.emi.trinkets.api.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class TrinketsCompatibility {
    private boolean isTrinketsInstalled;

    public TrinketsCompatibility(){
        isTrinketsInstalled = false;
    }

    public void enableTrinkets(){
        isTrinketsInstalled = true;
    }

    public void replaceElytraWithChestPlate(PlayerEntity player){
        if(!isTrinketsInstalled){
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
    }

    public void replaceChestPlateWithElytra(PlayerEntity player){
        if(isTrinketsInstalled){
            Inventory inv = TrinketsApi.getTrinketsInventory(player);

            if(!inv.getInvStack(getTrinketElytraSlotId()).getItem().equals(Items.ELYTRA)){
                for(int i = 0; i < player.inventory.main.size(); i++){
                    if(player.inventory.main.get(i).getItem() == Items.ELYTRA){
                        inv.setInvStack(getTrinketElytraSlotId(), player.inventory.main.get(i).copy());
                        player.inventory.main.get(i).setCount(0);
                    }
                }
            }
        }else{
            for (int i = 0; i < player.inventory.main.size(); i++){
                if(player.inventory.main.get(i).getItem() == Items.ELYTRA){
                    ItemStack chestplate = player.inventory.armor.get(2);

                    player.inventory.armor.set(2, player.inventory.main.get(i));
                    player.inventory.main.set(i, chestplate);
                }
            }
        }
    }

    public boolean doesPlayerHaveElytra(PlayerEntity player){
        boolean val = false;

        if(isTrinketsInstalled){
            val = TrinketsApi.getTrinketsInventory(player).getInvStack(getTrinketElytraSlotId()).getItem() == Items.ELYTRA;
        }

        val |= player.inventory.contains(new ItemStack(Items.ELYTRA));

        return val;
    }

    private int getTrinketElytraSlotId(){
        for(int i = 0; i < TrinketSlots.getSlotCount(); i++){
            if(TrinketSlots.getAllSlots().get(i).getName().equals(Slots.CAPE))
                return i;
        }

        return -1;
    }
}

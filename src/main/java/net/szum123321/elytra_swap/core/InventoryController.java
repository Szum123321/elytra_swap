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

package net.szum123321.elytra_swap.core;

import dev.emi.trinkets.api.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class InventoryController {
    private boolean isTrinketsInstalled;

    public InventoryController(){
        isTrinketsInstalled = false;
    }

    public void enableTrinkets(){
        isTrinketsInstalled = true;
    }

    public void replaceElytraWithChestPlate(PlayerEntity player){
        if(!isTrinketsInstalled && player.inventory.armor.get(2).getItem() == Items.ELYTRA){
            for(int i = 0; i < player.inventory.main.size(); i++){
                if(player.inventory.main.get(i).getItem().toString().toLowerCase().contains("chestplate")){  //kinda sketchy but should make this compatible with modded armor
                    ItemStack elytra = player.inventory.armor.get(2).copy();
                    player.inventory.armor.set(2, player.inventory.main.get(i).copy());
                    player.inventory.main.set(i, elytra);
                }
            }
        }
    }

    public void replaceChestPlateWithElytra(PlayerEntity player){
        if(isTrinketsInstalled){
            Inventory inv = TrinketsApi.getTrinketsInventory(player);

            if(inv.getInvStack(getTrinketElytraSlotId()).getItem() != Items.ELYTRA){
                for(int i = 0; i < player.inventory.main.size(); i++){
                    if(player.inventory.main.get(i).getItem() == Items.ELYTRA){
                        inv.setInvStack(getTrinketElytraSlotId(), player.inventory.main.get(i).copy());
                        player.inventory.main.get(i).setCount(0);
                    }
                }
            }
        }else if(player.inventory.armor.get(2).getItem() != Items.ELYTRA){
            for (int i = 0; i < player.inventory.main.size(); i++){
                if(player.inventory.main.get(i).getItem() == Items.ELYTRA){
                    ItemStack chestplate = player.inventory.armor.get(2).copy();

                    player.inventory.armor.set(2, player.inventory.main.get(i)).copy();
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

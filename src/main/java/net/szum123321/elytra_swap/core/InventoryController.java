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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class InventoryController {
	private boolean isTrinketsInstalled;

	public InventoryController() {
		isTrinketsInstalled = FabricLoader.getInstance().isModLoaded("trinkets");
	}

	public void replaceElytraWithChestPlate(PlayerEntity player) {
		if (!isTrinketsInstalled && player.inventory.armor.get(2).getItem() == Items.ELYTRA) {
			for (int i = 0; i < player.inventory.getInvSize(); i++) {
				if (player.inventory.getInvStack(i).getItem() instanceof ArmorItem &&
						((ArmorItem) player.inventory.getInvStack(i).getItem()).getSlotType() == EquipmentSlot.CHEST) {  //kinda sketchy but should make this compatible with modded armor
					ItemStack elytra = player.inventory.armor.get(2).copy();

					player.inventory.armor.set(2, player.inventory.getInvStack(i).copy());
					player.inventory.setInvStack(i, elytra);

					break;
				}
			}
		}
	}

	public void replaceChestPlateWithElytra(PlayerEntity player) {
		if (isTrinketsInstalled) {
			Inventory inv = TrinketsApi.getTrinketsInventory(player);

			if (inv.getInvStack(getTrinketElytraSlotId()).getItem() != Items.ELYTRA) {
				for (int i = 0; i < player.inventory.getInvSize(); i++) {
					if (player.inventory.getInvStack(i).getItem() == Items.ELYTRA) {
						ItemStack elytra = player.inventory.getInvStack(i).copy();

						player.inventory.setInvStack(i, inv.getInvStack(getTrinketElytraSlotId()).copy());
						inv.setInvStack(getTrinketElytraSlotId(), elytra);

						break;
					}
				}
			}
		} else if (player.inventory.armor.get(2).getItem() != Items.ELYTRA) {
			for (int i = 0; i < player.inventory.getInvSize(); i++) {
				if (player.inventory.getInvStack(i).getItem() == Items.ELYTRA) {
					ItemStack elytra = player.inventory.getInvStack(i).copy();

					player.inventory.setInvStack(i, player.inventory.armor.get(2).copy());
					player.inventory.armor.set(2, elytra);

					break;
				}
			}
		}
	}

	public boolean doesPlayerHasElytra(PlayerEntity player) {
		boolean val = false;

		if (isTrinketsInstalled)
			val = TrinketsApi.getTrinketsInventory(player).getInvStack(getTrinketElytraSlotId()).getItem() == Items.ELYTRA;


		val |= player.inventory.contains(new ItemStack(Items.ELYTRA));

		return val;
	}

	private int getTrinketElytraSlotId() {
		for (int i = 0; i < TrinketSlots.getSlotCount(); i++) {
			if (TrinketSlots.getAllSlots().get(i).getName().equals(Slots.CAPE))
				return i;
		}

		return -1;
	}
}

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

import net.minecraft.item.Items;

/*
	Small utility class for actually swapping elytra with chestplate and back.
*/

public class InventoryController {
	public static void replaceElytraWithChestPlate(FlatInventory inv) {

		if (!inv.getItemStack(inv.getChestplateSlotId()).getItem().toString().toLowerCase().contains("chestplate")) {
			int chestplateSlot;

			for(chestplateSlot = 0; chestplateSlot < inv.getSize(); chestplateSlot++) {
				if (inv.getItemStack(chestplateSlot).getItem().toString().toLowerCase().contains("chestplate"))
					break;
			}

			if (chestplateSlot >= inv.getSize() - 1)
				return;

			inv.switchItemStacks(chestplateSlot, inv.getChestplateSlotId());
		}
	}

	public static void replaceChestPlateWithElytra(FlatInventory inv) {
		if (inv.getItemStack(inv.getElytraSlotId()).getItem() != Items.ELYTRA) {
			int elytraSlot;

			for (elytraSlot = 0; elytraSlot < inv.getSize(); elytraSlot++) {
				if (inv.getItemStack(elytraSlot).getItem() == Items.ELYTRA)
					break;
			}

			if (elytraSlot >= inv.getSize() - 1)
				return;

			inv.switchItemStacks(elytraSlot, inv.getElytraSlotId());
		}
	}

	public static boolean doesPlayerHaveElytra(FlatInventory inv) {
		return inv.hasElytra();
	}
}
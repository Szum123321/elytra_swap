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

package net.szum123321.elytra_swap.inventory;

import dev.emi.trinkets.api.TrinketSlots;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ByteTag;
import net.szum123321.elytra_swap.data.MutablePair;
import net.szum123321.elytra_swap.enchantment.ModEnchantments;

/*
	Utility class for actually swapping elytra with chestplate and back.
*/
public class InventoryHelper {
	private static final String swapTag = "swapped_item";

	public static void replaceElytraWithChestplate(FlatInventory inv) {
		if(inv.getItemStack(inv.getElytraSlotId()).getItem() == Items.ELYTRA) {
			int var1 = -1;

			if(!inv.hasTrinkets()) { // isVanilla => ElytraSlotId == ChestplateSlotId
				 var1 = getBestChestplate(inv);
			} else {
				var1 = getBestTrinket(inv);
			}

			if(var1 >= 0 && var1 != inv.getElytraSlotId()) {
				inv.putSubTag(inv.getElytraSlotId(), swapTag, ByteTag.ONE);
				inv.removeSubTag(var1, swapTag);
				inv.switchItemStacks(var1, inv.getElytraSlotId());
			}
		}
	}

	public static void replaceChestplateWithElytra(FlatInventory inv) {
		if (inv.getItemStack(inv.getElytraSlotId()).getItem() != Items.ELYTRA) {
			int var1 = getBestElytra(inv);

			if(var1 >= 0 && var1 != inv.getElytraSlotId()) {
				inv.removeSubTag(var1, swapTag);
				inv.putSubTag(inv.getElytraSlotId(), swapTag, ByteTag.ONE);
				inv.switchItemStacks(var1, inv.getElytraSlotId());
			}
		}
	}

	private static int getBestChestplate(FlatInventory inv) {
		MutablePair<Integer, Integer> result = new MutablePair<>(-1, -1);

		for(int i = 0; i < inv.getSize(); i++) {
			if(inv.getItemStack(i).getItem() instanceof ArmorItem) {
				ArmorItem armorItem = (ArmorItem)inv.getItemStack(i).getItem();

				if(armorItem.getSlotType() == EquipmentSlot.CHEST) {
					int var1 = getPriority(inv.getItemStack(i));

					if(var1 > result.getLast()) {
						result.setFirst(i);
						result.setLast(var1);
					}
				}
			}
		}

		return result.getFirst();
	}

	private static int getBestElytra(FlatInventory inv) {
		MutablePair<Integer, Integer> result = new MutablePair<>(-1, -1);

		for(int i = 0; i < inv.getSize(); i++) {
			if(inv.getItemStack(i).getItem() == Items.ELYTRA) {
				int var1 = getPriority(inv.getItemStack(i));

				if(var1 > result.getLast()) {
					result.setFirst(i);
					result.setLast(var1);
				}
			}
		}

		return result.getFirst();
	}

	private static int getBestTrinket(FlatInventory inv) {
		final TrinketSlots.Slot trinketSlot = TrinketSlots.getSlotFromName("chest", "cape");
		MutablePair<Integer, Integer> result = new MutablePair<>(-1, -1);

		for(int i = 0; i < inv.getSize(); i++) {
			if(inv.getItemStack(i).getItem() != Items.ELYTRA &&
					trinketSlot.canEquip.apply(trinketSlot, inv.getItemStack(i))) {

				int var1 = getPriority(inv.getItemStack(i));

				if(var1 > result.getFirst()) {
					result.setFirst(i);
					result.setLast(var1);
				}
			}
		}

		return result.getFirst();
	}

	private static int getPriority(ItemStack stack) {
		int p = 0;

		if (stack.getTag() != null && stack.getTag().getByte("swapped_item") == 1)
			p = 1;

		p = Math.max(p, EnchantmentHelper.getLevel(ModEnchantments.SWAPPINESS, stack));

		return p;
	}
}
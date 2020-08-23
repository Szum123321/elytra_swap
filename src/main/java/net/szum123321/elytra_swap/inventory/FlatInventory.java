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

import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketSlots;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.collection.DefaultedList;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.handlers.ServerSwapStateHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/*
	This class turns players' inventory into a flat list (including shulkers and trinkets).
	Basically an inventory abstraction layer. :)
	Compatibility with other mods can be easily added
*/
public class FlatInventory {
	private final boolean trinketsSupport;
	private final List<Slot> slots = new ArrayList<>();
	private final Map<SpecialSlots, Integer> specialSlots = new HashMap<>();
	private final PlayerEntity player;

	public FlatInventory(PlayerEntity player) {
		this.player = player;
		this.trinketsSupport = ElytraSwap.hasTrinkets &&
								(ElytraSwap.serverSwapStateHandler.getTrinketsSupport(player) == ServerSwapStateHandler.Tristate.TRUE);

		for (int i = 0; i < player.inventory.size(); i++) {
			if (i == 38) { //Chestplate slot
				specialSlots.put(SpecialSlots.CHESTPLATE, slots.size());

				if(player.inventory.getStack(i).isEmpty())  // so that this slot gets added if it is empty
					slots.add(new Slot(InventoryType.VANILLA, i));
			}

			if(!player.inventory.getStack(i).isEmpty())
				slots.add(new Slot(InventoryType.VANILLA, i));

			if (player.inventory.getStack(i).getItem() instanceof BlockItem &&
					((BlockItem) player.inventory.getStack(i).getItem()).getBlock() instanceof ShulkerBoxBlock &&
					ElytraSwap.CONFIG.lookThroughShulkers) {
				for (int j = 0; j < 27; j++)
					slots.add(new Slot(InventoryType.VANILLA, i, j));
			}
		}

		if (trinketsSupport) {
			Inventory tInv = TrinketsApi.getTrinketsInventory(this.player);

			for (int i = 0; i < TrinketSlots.getSlotCount(); i++) {
				if (TrinketSlots.getAllSlots().get(i).getName().equals(Slots.CAPE)) {
					specialSlots.put(SpecialSlots.CAPE, slots.size());

					if(TrinketsApi.getTrinketsInventory(player).getStack(i).isEmpty())
						slots.add(new Slot(InventoryType.TRINKETS, i));
				}

				if(!TrinketsApi.getTrinketsInventory(player).getStack(i).isEmpty())
					slots.add(new Slot(InventoryType.TRINKETS, i));

				if (tInv.getStack(i).getItem() instanceof BlockItem &&
						((BlockItem) tInv.getStack(i).getItem()).getBlock() instanceof ShulkerBoxBlock &&
						ElytraSwap.CONFIG.lookThroughShulkers) {
					for (int j = 0; j < 27; j++)
						slots.add(new Slot(InventoryType.TRINKETS, i, j));
				}
			}
		}
	}

	public int getSize() {
		return slots.size();
	}

	public void setItemStack(int index, ItemStack stack) throws IndexOutOfBoundsException {
		if (index >= slots.size() || index < 0)
			throw new IndexOutOfBoundsException("Called setItemStack with index = " + index +
					", which exceeds allowed bounds of: 0 to: " + (slots.size() - 1));

		Slot slot = slots.get(index);

		if (slot.invType == InventoryType.VANILLA) { // standard inventory
			if (slot.containerIndex == -1) {  // is not a shulker
				player.inventory.setStack(slot.slotIndex, stack);
			} else {  // it is a shulker so get stack form it
				ItemStack shulker = player.inventory.getStack(slot.slotIndex);

				if (shulker.getTag() != null) {
					DefaultedList<ItemStack> shulkerInventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
					Inventories.fromTag(shulker.getTag().getCompound("BlockEntityTag"), shulkerInventory);

					shulkerInventory.set(slot.containerIndex, stack);

					CompoundTag tag = new CompoundTag();
					Inventories.toTag(tag, shulkerInventory, false);
					shulker.putSubTag("BlockEntityTag", tag);
				}
			}
		} else if (slot.invType == InventoryType.TRINKETS && trinketsSupport) { // trinkets inventory
			if (slot.containerIndex == -1) {
				TrinketsApi.getTrinketsInventory(player).setStack(slot.slotIndex, stack);
			} else {
				ItemStack shulker = TrinketsApi.getTrinketsInventory(player).getStack(slot.slotIndex);

				if (shulker.getTag() != null) {
					DefaultedList<ItemStack> shulkerInventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
					Inventories.fromTag(shulker.getTag().getCompound("BlockEntityTag"), shulkerInventory);

					shulkerInventory.set(slot.containerIndex, stack);

					CompoundTag tag = new CompoundTag();
					Inventories.toTag(tag, shulkerInventory, false);
					shulker.putSubTag("BlockEntityTag", tag);
				}
			}
		}
	}

	public ItemStack getItemStack(int index) throws IndexOutOfBoundsException {
		if (index >= slots.size() || index < 0)
			throw new IndexOutOfBoundsException("Called getItemStack with index = " + index +
					", which exceeds allowed bounds of: 0 to: " + (slots.size() - 1));

		Slot slot = slots.get(index);

		if (slot.invType == InventoryType.VANILLA) { // standard inventory
			if (slot.containerIndex == -1) {  // is not a shulker
				return player.inventory.getStack(slot.slotIndex);
			} else {  // it is a shulker so get the stack form it
				ItemStack shulker = player.inventory.getStack(slot.slotIndex);

				if (shulker.getTag() != null) {
					DefaultedList<ItemStack> shulkerInventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
					Inventories.fromTag(shulker.getTag().getCompound("BlockEntityTag"), shulkerInventory);

					return shulkerInventory.get(slot.containerIndex);
				}
			}
		} else if (slot.invType == InventoryType.TRINKETS && trinketsSupport) { // trinkets inventory
			if (slot.containerIndex == -1) {
				return TrinketsApi.getTrinketsInventory(player).getStack(slot.slotIndex);
			} else {
				ItemStack shulker = TrinketsApi.getTrinketsInventory(player).getStack(slot.slotIndex);

				if (shulker.getTag() != null) {
					DefaultedList<ItemStack> shulkerInventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
					Inventories.fromTag(shulker.getTag().getCompound("BlockEntityTag"), shulkerInventory);

					return shulkerInventory.get(slot.containerIndex);
				}
			}
		}

		return ItemStack.EMPTY; // something went horribly wrong!
	}

	public void removeSubTag(int index, String tagName) { //We have to do so because item may come from shulker box
		ItemStack stack = getItemStack(index);
		stack.removeSubTag(tagName);
		setItemStack(index, stack);
	}

	public void putSubTag(int index, String tagName, Tag tag) {
		ItemStack stack = getItemStack(index);
		stack.putSubTag(tagName, tag);
		setItemStack(index, stack);
	}

	public void switchItemStacks(int indexA, int indexB) {
		ItemStack temp = getItemStack(indexA).copy();

		setItemStack(indexA, getItemStack(indexB));
		setItemStack(indexB, temp);
	}

	public boolean hasOne(Predicate<Item> predicate) {
		for (int i = 0; i < slots.size(); i++) {
			if(predicate.test(getItemStack(i).getItem()))
				return true;
		}

		return false;
	}

	public int getElytraSlotId() {
		if (trinketsSupport)
			return specialSlots.get(SpecialSlots.CAPE);
		else
			return specialSlots.get(SpecialSlots.CHESTPLATE);
	}

	public int getChestplateSlotId() {
		return specialSlots.get(SpecialSlots.CHESTPLATE);
	}

	public boolean isVanillaSlot(int index) {
		if (index >= slots.size() || index < 0)
			throw new IndexOutOfBoundsException("Called getItemStack with index = " + index +
					", which exceeds allowed bounds of: 0 to: " + (slots.size() - 1));

		return slots.get(index).invType == InventoryType.VANILLA;
	}

	public boolean hasTrinkets() {
		return trinketsSupport;
	}

	private enum SpecialSlots {
		CAPE,
		CHESTPLATE
	}

	private enum InventoryType {
		VANILLA,
		TRINKETS
	}

	private static class Slot {
		public final InventoryType invType;
		public final int containerIndex; // id of item in shulker
		public final int slotIndex; // id of slot in given inventory

		public Slot(InventoryType invType, int slotIndex, int containerIndex) {
			this.invType = invType;
			this.containerIndex = containerIndex;
			this.slotIndex = slotIndex;
		}

		public Slot(InventoryType invType, int slotIndex) {
			this.invType = invType;
			this.containerIndex = -1;
			this.slotIndex = slotIndex;
		}
	}
}
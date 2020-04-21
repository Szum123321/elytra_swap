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

import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketSlots;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.szum123321.elytra_swap.ElytraSwap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
	This class turns players' inventory into flat list (including shulkers and trinkets).
	Basically a inventory abstraction layer.
*/

public class FlatInventory {
	// in case someone would like to make this into library, please keep those fields protected and just add getter/setter.
	private final boolean trinketsSupport;
	protected final List<Slot> slots = new ArrayList<>();
	protected final Map<SpecialSlots, Integer> specialSlots = new HashMap<>();
	protected final PlayerEntity player;

	public FlatInventory(PlayerEntity player) {
		this.player = player;
		this.trinketsSupport = FabricLoader.getInstance().isModLoaded("trinkets") &&
								!(ElytraSwap.serverSwapStateHandler.getTrinketsSupport(player) == ServerSwapStateHandler.Tristate.FALSE);

		for (int i = 0; i < player.inventory.getInvSize(); i++) {
			if (i == 38) //Chestplate slot
				specialSlots.put(SpecialSlots.CHESTPLATE, slots.size());

			slots.add(new Slot(InventoryType.NORMAL, i));

			if (player.inventory.getInvStack(i).getItem() instanceof BlockItem &&
					((BlockItem) player.inventory.getInvStack(i).getItem()).getBlock() instanceof ShulkerBoxBlock &&
					ElytraSwap.config.lookThroughShulkers) {
				for (int j = 0; j < 27; j++)
					slots.add(new Slot(InventoryType.NORMAL, i, j));
			}
		}

		if (trinketsSupport) {
			Inventory tInv = TrinketsApi.getTrinketsInventory(this.player);

			for (int i = 0; i < TrinketSlots.getSlotCount(); i++) {
				if (TrinketSlots.getAllSlots().get(i).getName().equals(Slots.CAPE))
					specialSlots.put(SpecialSlots.CAPE, slots.size());

				slots.add(new Slot(InventoryType.TRINKETS, i));

				if (tInv.getInvStack(i).getItem() instanceof BlockItem &&
						((BlockItem) tInv.getInvStack(i).getItem()).getBlock() instanceof ShulkerBoxBlock &&
						ElytraSwap.config.lookThroughShulkers) {
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

		if (slot.invType == InventoryType.NORMAL) { // standard inventory
			if (slot.shulkerIndex == -1) {  // is not a shulker
				player.inventory.setInvStack(slot.slotIndex, stack);
			} else {  // it is a shulker so get stack form it
				ItemStack shulker = player.inventory.getInvStack(slot.slotIndex);

				if (shulker.getTag() != null) {
					DefaultedList<ItemStack> shulkerInventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
					Inventories.fromTag(shulker.getTag().getCompound("BlockEntityTag"), shulkerInventory);

					shulkerInventory.set(slot.shulkerIndex, stack);

					CompoundTag tag = new CompoundTag();
					Inventories.toTag(tag, shulkerInventory, false);
					shulker.putSubTag("BlockEntityTag", tag);
				}
			}
		} else if (slot.invType == InventoryType.TRINKETS && trinketsSupport) { // trinkets inventory
			if (slot.shulkerIndex == -1) {
				TrinketsApi.getTrinketsInventory(player).setInvStack(slot.slotIndex, stack);
			} else {
				ItemStack shulker = TrinketsApi.getTrinketsInventory(player).getInvStack(slot.slotIndex);

				if (shulker.getTag() != null) {
					DefaultedList<ItemStack> shulkerInventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
					Inventories.fromTag(shulker.getTag().getCompound("BlockEntityTag"), shulkerInventory);

					shulkerInventory.set(slot.shulkerIndex, stack);

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

		if (slot.invType == InventoryType.NORMAL) { // standard inventory
			if (slot.shulkerIndex == -1) {  // is not a shulker
				return player.inventory.getInvStack(slot.slotIndex);
			} else {  // it is a shulker so get the stack form it
				ItemStack shulker = player.inventory.getInvStack(slot.slotIndex);

				if (shulker.getTag() != null) {
					DefaultedList<ItemStack> shulkerInventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
					Inventories.fromTag(shulker.getTag().getCompound("BlockEntityTag"), shulkerInventory);

					return shulkerInventory.get(slot.shulkerIndex);
				}
			}
		} else if (slot.invType == InventoryType.TRINKETS && trinketsSupport) { // trinkets inventory
			if (slot.shulkerIndex == -1) {
				return TrinketsApi.getTrinketsInventory(player).getInvStack(slot.slotIndex);
			} else {
				ItemStack shulker = TrinketsApi.getTrinketsInventory(player).getInvStack(slot.slotIndex);

				if (shulker.getTag() != null) {
					DefaultedList<ItemStack> shulkerInventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
					Inventories.fromTag(shulker.getTag().getCompound("BlockEntityTag"), shulkerInventory);

					return shulkerInventory.get(slot.shulkerIndex);
				}
			}
		}

		return ItemStack.EMPTY; // something went horribly wrong!
	}

	public void switchItemStacks(int a, int b) {
		ItemStack temp = getItemStack(a).copy();

		setItemStack(a, getItemStack(b));
		setItemStack(b, temp);
	}

	public boolean hasElytra() {
		for (int i = 0; i < slots.size(); i++) {
			if (getItemStack(i).getItem() == Items.ELYTRA)
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

	private enum SpecialSlots {
		CAPE, CHESTPLATE
	}

	private enum InventoryType {
		NORMAL,
		TRINKETS;
	}

	private class Slot {
		public final InventoryType invType;  // 1 - normal, 2 - trinket
		public final int shulkerIndex; // id of item in shulker
		public final int slotIndex; // id of slot in given inventory

		public Slot(InventoryType invType, int slotIndex, int shulkerIndex) {
			this.invType = invType;
			this.shulkerIndex = shulkerIndex;
			this.slotIndex = slotIndex;
		}

		public Slot(InventoryType invType, int slotIndex) {
			this.invType = invType;
			this.shulkerIndex = -1;
			this.slotIndex = slotIndex;
		}
	}
}
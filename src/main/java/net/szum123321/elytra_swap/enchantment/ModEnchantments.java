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
package net.szum123321.elytra_swap.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.szum123321.elytra_swap.ElytraSwap;

public class ModEnchantments {
	public final static Enchantment SWAPPINESS = Registry.register(Registry.ENCHANTMENT, new Identifier(ElytraSwap.MOD_ID, "swappiness"), new SwappinessEnchantment(
			Enchantment.Rarity.COMMON,
			EnchantmentTarget.WEARABLE,
			new EquipmentSlot[] {
					EquipmentSlot.CHEST
			}
	));
}

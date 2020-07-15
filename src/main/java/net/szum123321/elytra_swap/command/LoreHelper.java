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

package net.szum123321.elytra_swap.command;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Objects;

/*
    This class was inspired by https://github.com/QsPD-Minecraft/Fabric-Server-Enchantments/blob/master/src/main/java/io/github/qspdmc/util/LoreUtil.java
    written by Devan-Kerman and licensed under LGPL-3.
*/
public class LoreHelper {
    private final static String loreItemName = "Swap Priority: ";

    public static void apply(ItemStack stack, int level) {
        CompoundTag display = stack.getOrCreateSubTag("display");
        ListTag lore = display.getList("Lore", 8);

        lore.removeIf(tag -> {
            Text text = Text.Serializer.fromJson(tag.asString());

            if(text != null)
                return text.getString().startsWith(loreItemName);

            return false;
        });

        if(level > 0)
            lore.add(0, StringTag.of(Text.Serializer.toJson(new LiteralText(loreItemName).append(new TranslatableText("enchantment.level." + level)))));

        display.put("Lore", lore);
    }

    public static int get(ItemStack stack) {
        return stack.getOrCreateSubTag("display")
                .getList("Lore", 8).stream()
                .filter(tag -> tag instanceof StringTag)
                .map(Tag::asString)
                .map(Text.Serializer::fromJson)
                .filter(Objects::nonNull)
                .filter(text -> text.asString().startsWith(loreItemName))
                .flatMap(text -> text.getSiblings().stream())
                .filter(text -> text instanceof TranslatableText)
                .map(text -> ((TranslatableText) text).getKey())
                .filter(s -> s.startsWith("enchantment.level."))
                .map(s -> s.split("enchantment.level.")[1])
                .map(Integer::parseInt)
                .findFirst()
                .orElse(-1);
    }
}

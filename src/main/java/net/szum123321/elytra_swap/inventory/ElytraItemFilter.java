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

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.szum123321.elytra_swap.ElytraSwap;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ElytraItemFilter {
    private Set<Item> items;

    public ElytraItemFilter () {
        File idListFile = new File(getClass().getClassLoader().getResource("assets/elytra_swap/elytra_like_item_set.json").getFile());

        if(!idListFile.exists()) {
            ElytraSwap.LOGGER.error("File: {} does not exists! Mod integration will not be available!", getClass().getClassLoader().getResource("assets/elytra_swap/elytra_like_item_set.json").getFile());
        }

        Type type = new TypeToken<HashSet<Identifier>>(){}.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Identifier.class, (JsonDeserializer<Identifier>) (json, typeOfT, context) -> new Identifier(json.getAsString().split(":")[0], json.getAsString().split(":")[1]))
                .create();

        try (FileReader reader = new FileReader(idListFile)) {
            final Item defaultItem = Registry.ITEM.get(Registry.ITEM.getDefaultId());

            items = ((Set<Identifier>)gson.fromJson(reader, type))
                    .stream()
                    .map(Registry.ITEM::get)
                    .filter(item -> item != defaultItem)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            ElytraSwap.LOGGER.error("Something went wrong while trying to deserialize elytra ids list!", e);
        }
    }

    public boolean isElytraLike(Item item) {
        return items.contains(item);
    }
}
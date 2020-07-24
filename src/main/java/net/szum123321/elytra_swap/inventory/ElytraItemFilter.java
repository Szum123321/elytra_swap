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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.szum123321.elytra_swap.ElytraSwap;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ElytraItemFilter {
    private Set<Item> items;

    public ElytraItemFilter () {
        Path path = FabricLoader.getInstance().getModContainer(ElytraSwap.MOD_ID).get().getRootPath().resolve("/elytra_like_item_set.json");

        Type type = new TypeToken<HashSet<Identifier>>(){}.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Identifier.class, (JsonDeserializer<Identifier>) (json, typeOfT, context) -> new Identifier(json.getAsString().split(":")[0], json.getAsString().split(":")[1]))
                .create();

        try {
            final Item defaultItem = Registry.ITEM.get(Registry.ITEM.getDefaultId());

            items = ((Set<Identifier>)gson.fromJson(new String(Files.readAllBytes(path)), type))
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
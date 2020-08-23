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

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.szum123321.elytra_swap.ElytraSwap;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ElytraItemFilter {
    private ImmutableSet<Item> items;

    public ElytraItemFilter () {
        Path path;

        if(FabricLoader.getInstance().isDevelopmentEnvironment())
            path = Paths.get(getClass().getResource("/elytra_like_item_set.json").getPath());
        else
            path = FabricLoader.getInstance().getModContainer(ElytraSwap.MOD_ID).get().getRootPath().resolve("/elytra_like_item_set.json");

        Type type = new TypeToken<ArrayList<ElytraLikeItemEntry>>(){}.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Identifier.class, (JsonDeserializer<Identifier>) (json, typeOfT, context) -> new Identifier(json.getAsString().split(":")[0], json.getAsString().split(":")[1]))
                .create();

        try (InputStream inputStream = Files.newInputStream(path)) {
            final Item defaultItem = Registry.ITEM.get(Registry.ITEM.getDefaultId());
            final boolean isTrinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");

            //noinspection unchecked
            items = ((List<ElytraLikeItemEntry>)gson.fromJson(new InputStreamReader(inputStream), type))
                    .stream()
                    .filter(entry -> entry.isTrinketCompatible() || !isTrinketsLoaded)
                    .map(ElytraLikeItemEntry::getIdentifier)
                    .map(Registry.ITEM::get)
                    .filter(item -> item != defaultItem)
                    .collect(ImmutableSet.toImmutableSet());
            
            ElytraSwap.LOGGER.info("Found %d compatible items: %s", items.size(), items.stream().map(Registry.ITEM::getId).map(Identifier::toString).collect(Collectors.joining(", ")));
        } catch (IOException e) {
            ElytraSwap.LOGGER.error("Something went wrong while trying to read elytra ids list!", e);
            items = ImmutableSet.of(Items.ELYTRA);
        }
    }

    public boolean isElytraLike(Item item) {
        return items.contains(item);
    }

    private static class ElytraLikeItemEntry {
        private final Identifier identifier;
        private final boolean trinketCompatible;

        public ElytraLikeItemEntry(Identifier identifier, boolean trinketCompatible) {
            this.identifier = identifier;
            this.trinketCompatible = trinketCompatible;
        }

        public Identifier getIdentifier() {
            return identifier;
        }

        public boolean isTrinketCompatible() {
            return trinketCompatible;
        }
    }
}
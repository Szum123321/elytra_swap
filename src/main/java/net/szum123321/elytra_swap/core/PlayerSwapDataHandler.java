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

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class PlayerSwapDataHandler {
    private static Map<String, Boolean> data = new HashMap<>();

    public static boolean isMapped(PlayerEntity player){
        return data.containsKey(player.getName().asString());
    }

    public static void addPlayer(PlayerEntity player, boolean val){
        data.put(player.getName().asString(), val);
    }

    public static void set(PlayerEntity player, boolean val){
        data.replace(player.getName().asString(), val);
    }

    public static boolean get(PlayerEntity player){
        if(isMapped(player)){
            return data.get(player.getName().asString());
        }else{
            data.put(player.getName().asString(), true);
            return true;
        }
    }
}

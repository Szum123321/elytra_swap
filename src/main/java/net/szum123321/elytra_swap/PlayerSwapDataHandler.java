package net.szum123321.elytra_swap;

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

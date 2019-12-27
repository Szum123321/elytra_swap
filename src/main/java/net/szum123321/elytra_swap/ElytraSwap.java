package net.szum123321.elytra_swap;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ElytraSwap implements ModInitializer {
    public static final String MOD_ID = "elytra_swap";
    public static final Identifier KICK_PLAYER_INTO_AIR = new Identifier(MOD_ID, "kick");

    @Override
    public void onInitialize() {
        System.out.println("Loading Elytra Swap by Szum123321");

        TakeOffHandler.onItemUseRegister();
    }
}


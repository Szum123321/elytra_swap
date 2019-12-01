package net.szum123321.elytra_swap;

import net.fabricmc.api.ModInitializer;

public class ElytraSwap implements ModInitializer {
    public final String MOD_ID = "elytra_swap";

    @Override
    public void onInitialize() {
        System.out.println("Loading Elytra Swap by Szum123321");
        TakeOffHandler.onItemUseRegister();
    }

}

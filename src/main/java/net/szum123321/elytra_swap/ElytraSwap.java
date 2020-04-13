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

package net.szum123321.elytra_swap;

import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.cotton.logging.ModLogger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.util.Identifier;
import net.szum123321.elytra_swap.command.SwapEnablementCommandRegister;
import net.szum123321.elytra_swap.core.ConfigHandler;
import net.szum123321.elytra_swap.core.PlayerSwapDataHandler;
import net.szum123321.elytra_swap.core.InventoryController;

public class ElytraSwap implements ModInitializer {
    public static final String MOD_ID = "elytra_swap";

    public static final Identifier SET_SWAP_ENABLE = new Identifier(MOD_ID, "set_swap");
    public static final Identifier DUMMY_PACKAGE = new Identifier(MOD_ID, "dummy");

    public static ConfigHandler config;
    public static final ModLogger LOGGER = new ModLogger(MOD_ID);

    public static InventoryController inventoryController;
    public static PlayerSwapDataHandler playerSwapDataHandler;

    @Override
    public void onInitialize() {
        LOGGER.info("Loading Elytra Swap by Szum123321");

        config = ConfigManager.loadConfig(ConfigHandler.class);

        registerSwapToggle();

        SwapEnablementCommandRegister.register();

        inventoryController = new InventoryController();
        playerSwapDataHandler = new PlayerSwapDataHandler();
    }

    private void registerSwapToggle() {
        ServerSidePacketRegistry.INSTANCE.register(SET_SWAP_ENABLE, (packetContext, attachedData) -> {
            boolean val = attachedData.readBoolean();

            packetContext.getTaskQueue().execute(() -> {
                if (playerSwapDataHandler.isMapped(packetContext.getPlayer())) {
                    playerSwapDataHandler.set(packetContext.getPlayer(), val);
                } else {
                    playerSwapDataHandler.addPlayer(packetContext.getPlayer(), val);
                }
            });
        });

        ServerSidePacketRegistry.INSTANCE.register(ElytraSwap.DUMMY_PACKAGE, ((packetContext, packetByteBuf) -> {}));
    }
}
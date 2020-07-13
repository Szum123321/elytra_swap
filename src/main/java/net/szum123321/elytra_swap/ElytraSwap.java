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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.szum123321.elytra_swap.command.SwapEnablementCommandRegister;
import net.szum123321.elytra_swap.handlers.ServerSwapStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
    Main class. Mostly static variables and initialization stuff.
    If you are looking for client init, head to client/ElytraSwapClientInit.
*/

public class ElytraSwap implements ModInitializer {
    public static final String MOD_ID = "elytra_swap";

    public static final Identifier CLIENT_JOIN_PACKET = new Identifier(MOD_ID, "client_join");
    public static final Identifier SET_SWAP_STATE = new Identifier(MOD_ID, "set_state");
    public static final Identifier DUMMY_PACKAGE = new Identifier(MOD_ID, "dummy");

    public static ConfigHandler CONFIG;
    public static final Logger LOGGER = LogManager.getFormatterLogger("Elytra Swap");

    public static ServerSwapStateHandler serverSwapStateHandler;
    public static boolean hasTrinkets;

    @Override
    public void onInitialize() {
        LOGGER.info("Loading Elytra Swap by Szum123321");

        CONFIG = ConfigManager.loadConfig(ConfigHandler.class);

        registerSwapToggle();

        SwapEnablementCommandRegister.register();

        serverSwapStateHandler = new ServerSwapStateHandler();

        hasTrinkets = FabricLoader.getInstance().isModLoaded("trinkets");
    }

    private void registerSwapToggle() {
        ServerSidePacketRegistry.INSTANCE.register(CLIENT_JOIN_PACKET, (packetContext, packetByteBuf) -> {
            boolean val = packetByteBuf.readBoolean();
            boolean hasTrinkets = packetByteBuf.readBoolean();

            packetContext.getTaskQueue().execute(() -> {
                if (!serverSwapStateHandler.isMapped(packetContext.getPlayer()))
                    serverSwapStateHandler.addPlayer(packetContext.getPlayer(), val, ServerSwapStateHandler.Tristate.get(hasTrinkets));
            });
        });

        ServerSidePacketRegistry.INSTANCE.register(SET_SWAP_STATE, (packetContext, packetByteBuf) -> {
            boolean val = packetByteBuf.readBoolean();

            packetContext.getTaskQueue().execute(() -> serverSwapStateHandler.setSwapState(packetContext.getPlayer(), val, false));
        });

        ServerSidePacketRegistry.INSTANCE.register(ElytraSwap.DUMMY_PACKAGE, ((packetContext, packetByteBuf) -> {}));
    }
}
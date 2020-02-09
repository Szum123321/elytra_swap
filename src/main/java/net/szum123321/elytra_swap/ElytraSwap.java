package net.szum123321.elytra_swap;

import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.cotton.logging.ModLogger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.szum123321.elytra_swap.command.SwapEnableArgumentType;
import net.szum123321.elytra_swap.command.SwapEnablementCommandRegister;

public class ElytraSwap implements ModInitializer {
    public static final String MOD_ID = "elytra_swap";

    public static final Identifier SET_SWAP_ENABLE = new Identifier(MOD_ID, "set_swap");
    public static final Identifier DUMMY_PACKAGE = new Identifier(MOD_ID, "dummy");

    public static ConfigHandler config;
    public static final ModLogger LOGGER = new ModLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Loading Elytra Swap by Szum123321");

        config = ConfigManager.loadConfig(ConfigHandler.class);

        registerSwapToggle();

        SwapEnablementCommandRegister.register();
    }

    private void registerSwapToggle(){
        ServerSidePacketRegistry.INSTANCE.register(SET_SWAP_ENABLE, (packetContext, attachedData) -> {
            boolean val = attachedData.readBoolean();

            packetContext.getTaskQueue().execute(() -> {
                if(PlayerSwapDataHandler.isMapped(packetContext.getPlayer())){
                    PlayerSwapDataHandler.set(packetContext.getPlayer(), val);
                }else{
                    PlayerSwapDataHandler.addPlayer(packetContext.getPlayer(), val);
                }
            });
        });

        ServerSidePacketRegistry.INSTANCE.register(ElytraSwap.DUMMY_PACKAGE, ((packetContext, packetByteBuf) -> {}));
    }
}


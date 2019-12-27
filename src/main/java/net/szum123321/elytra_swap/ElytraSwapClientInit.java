package net.szum123321.elytra_swap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;

public class ElytraSwapClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(ElytraSwap.KICK_PLAYER_INTO_AIR,
                (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
                    PlayerEntity player = packetContext.getPlayer();
                    player.addVelocity(-Math.sin(Math.toRadians(player.yaw)) * 1.2, 1.5, Math.cos(Math.toRadians(player.yaw)) * 1.2);
                })
                );
    }
}

package net.szum123321.elytra_swap.mixin;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.class_5404;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.DedicatedPlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.szum123321.elytra_swap.ElytraSwap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    public void displaySwapHelp(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if(!ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, ElytraSwap.DUMMY_PACKAGE) &&
                ElytraSwap.CONFIG.sendInfoOnClientJoin &&
                (Object)this instanceof DedicatedPlayerManager) {
            player.sendMessage(
                    new LiteralText("Hi! This server uses Elytra Swap mod, which partially alters elytra mechanics.\n")
                    .append("For more info see: ")
                    .append(new LiteralText("Elytra Swap wiki.")
                            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Szum123321/elytra_swap/wiki")))
                            .formatted(Formatting.UNDERLINE, Formatting.AQUA)
                    )
                    , false
            );
        }
    }
}

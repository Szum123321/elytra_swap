package net.szum123321.elytra_swap.mixin.dedicated;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.szum123321.elytra_swap.event.GameReadyCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin {
    @Inject(method = "setupServer", at = @At("HEAD"))
    private void atDedicatedServerStart(CallbackInfoReturnable<Boolean> ci) {
        GameReadyCallback.EVENT.invoker().ready(this);
    }
}

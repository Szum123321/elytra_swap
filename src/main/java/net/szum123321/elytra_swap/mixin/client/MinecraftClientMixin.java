package net.szum123321.elytra_swap.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.szum123321.elytra_swap.event.GameReadyCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void atClientStart(RunArgs args, CallbackInfo ci) {
        GameReadyCallback.EVENT.invoker().ready(this);
    }
}
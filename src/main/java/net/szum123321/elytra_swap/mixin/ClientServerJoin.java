package net.szum123321.elytra_swap.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.PacketByteBuf;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.client.ElytraSwapClientInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientServerJoin {
    @Inject(method = "onGameJoin", at= @At("RETURN"))
    private void join(GameJoinS2CPacket packet, CallbackInfo ci){
        if(ClientSidePacketRegistry.INSTANCE.canServerReceive(ElytraSwap.DUMMY_PACKAGE)) {
            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
            passedData.writeBoolean(ElytraSwapClientInit.swapStateHandler.get());

            ClientSidePacketRegistry.INSTANCE.sendToServer(ElytraSwap.SET_SWAP_ENABLE, passedData);

            ElytraSwapClientInit.serverHasMod = true;
        }else{
            ElytraSwapClientInit.serverHasMod = false;
        }
    }
}

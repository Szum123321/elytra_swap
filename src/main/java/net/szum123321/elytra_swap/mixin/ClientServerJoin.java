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

package net.szum123321.elytra_swap.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.szum123321.elytra_swap.ElytraSwap;
import net.szum123321.elytra_swap.client.ElytraSwapClientInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientServerJoin {
    /*
        This method get called every time player joins a server in order to find out if server
        has Elytra Swap installed, and if so, to send packet informing if player has Elytra Swap enabled
        and has Trinkets installed.
     */
    @Inject(method = "onGameJoin", at= @At("RETURN"))
    private void join(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (ClientSidePacketRegistry.INSTANCE.canServerReceive(ElytraSwap.DUMMY_PACKAGE)) {
            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
            passedData.writeBoolean(ElytraSwapClientInit.swapStateHandler.get());
            passedData.writeBoolean(FabricLoader.getInstance().isModLoaded("trinkets"));

            ClientSidePacketRegistry.INSTANCE.sendToServer(ElytraSwap.CLIENT_JOIN_PACKET, passedData);

            ElytraSwapClientInit.serverHasMod = true;
        } else {
            ElytraSwapClientInit.serverHasMod = false;
        }
    }
}
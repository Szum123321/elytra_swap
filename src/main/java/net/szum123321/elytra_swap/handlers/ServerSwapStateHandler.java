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

package net.szum123321.elytra_swap.handlers;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.szum123321.elytra_swap.ElytraSwap;

import java.util.HashMap;
import java.util.Map;

public class ServerSwapStateHandler {
    private final Map<String, PlayerEntry> data = new HashMap<>();

    public boolean isMapped(PlayerEntity player){
        return data.containsKey(player.getName().asString());
    }

    public void addPlayer(PlayerEntity player, boolean val, Tristate trinkets){
        data.put(player.getName().asString(), new PlayerEntry(trinkets, val));
    }

    public void addDefaultPlayer(PlayerEntity player) {
        addPlayer(player, ElytraSwap.CONFIG.noModPlayersDefaultState.getState(), Tristate.UNKNOWN);
    }

    public Tristate getTrinketsSupport(PlayerEntity player) {
        if(isMapped(player)) {
            return data.get(player.getName().asString()).getHasTrinkets();
        } else {
            addDefaultPlayer(player);
            return Tristate.UNKNOWN;
        }
    }

    public void setSwapState(PlayerEntity player, boolean val, boolean internal){
        if(data.get(player.getName().asString()).getSwapState() != val) {
          data.get(player.getName().asString()).setSwapState(val);

            if(internal) {
                PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
                packetByteBuf.writeBoolean(val);

                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ElytraSwap.SET_SWAP_STATE, packetByteBuf);
            }
        }
    }

    public boolean getSwapState(PlayerEntity player) {
        if(isMapped(player)) {
            return data.get(player.getName().asString()).getSwapState();
        } else {
            addDefaultPlayer(player);
            return ElytraSwap.CONFIG.noModPlayersDefaultState.getState();
        }
    }

    private static class PlayerEntry {
        private final Tristate hasTrinkets;
        private boolean swapState;

        public PlayerEntry(Tristate hasTrinkets, boolean swapState) {
            this.hasTrinkets = hasTrinkets;
            this.swapState = swapState;
        }

        public Tristate getHasTrinkets() {
            return hasTrinkets;
        }

        public boolean getSwapState() {
            return swapState;
        }

        public void setSwapState(boolean swapState) {
            this.swapState = swapState;
        }
    }

    public enum Tristate {  //Idontknowinwhatstateitisinator!
        TRUE,
        FALSE,
        UNKNOWN;

        public static Tristate get(int val) {
            switch (val) {
                case 1:
                    return TRUE;

                case -1:
                    return FALSE;

                default:
                    return UNKNOWN;
            }
        }

        public static Tristate get(boolean val) {
            return val ? TRUE : FALSE;
        }
    }
}

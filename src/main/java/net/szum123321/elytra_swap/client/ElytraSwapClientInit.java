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

package net.szum123321.elytra_swap.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.szum123321.elytra_swap.ElytraSwap;
import org.lwjgl.glfw.GLFW;

/*
	Client side entry point. All it does is it registers keybind and adds packet listener.
*/
public class ElytraSwapClientInit implements ClientModInitializer {
	public static ClientSwapStateHandler swapStateHandler;

	private static KeyBinding enableSwap;

	public static boolean serverHasMod = false;
	private boolean lastState = false;

	@Override
	public void onInitializeClient() {
		swapStateHandler = new ClientSwapStateHandler();
		swapStateHandler.load();

		registerKeyBind();
		registerPackets();
	}

	private void registerKeyBind() {
		//KeyBindingRegistry.INSTANCE.addCategory("Elytra Swap");

		enableSwap = new KeyBinding(
				"key.elytra_swap.swap",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_P,
				"category.elytra_swap.swap"
		);

		KeyBindingHelper.registerKeyBinding(enableSwap);
		//KeyBindingRegistry.INSTANCE.register(enableSwap);

		ClientTickCallback.EVENT.register(e -> {
			if (enableSwap.isPressed() && serverHasMod) {
				if (!lastState) {
					swapStateHandler.set(!swapStateHandler.get());
					lastState = true;

					if (e.player != null)
						e.player.sendMessage(new LiteralText("Elytra Swap in now: " + (swapStateHandler.get() ? "Enabled" : "Disabled")), false);

					PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
					passedData.writeBoolean(swapStateHandler.get());

					ClientSidePacketRegistry.INSTANCE.sendToServer(ElytraSwap.SET_SWAP_STATE, passedData);
				}
			} else {
				lastState = false;
			}
		});
	}

	private void registerPackets() {
		ClientSidePacketRegistry.INSTANCE.register(ElytraSwap.SET_SWAP_STATE, (packetContext, packetByteBuf) -> {
			boolean state = packetByteBuf.readBoolean();

			packetContext.getTaskQueue().execute(() -> {
				if(state != swapStateHandler.get()) {
					swapStateHandler.set(state);
				}
			});
		});

		ClientSidePacketRegistry.INSTANCE.register(ElytraSwap.DUMMY_PACKAGE, ((packetContext, packetByteBuf) -> {}));
	}
}
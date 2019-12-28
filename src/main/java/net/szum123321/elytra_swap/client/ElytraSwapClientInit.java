package net.szum123321.elytra_swap.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.szum123321.elytra_swap.ElytraSwap;
import org.lwjgl.glfw.GLFW;

public class ElytraSwapClientInit implements ClientModInitializer {
    private static FabricKeyBinding enableSwap;
    private Boolean lastState = false;

    public static ClientSwapStateHandler swapStateHandler;

    @Override
    public void onInitializeClient() {
        swapStateHandler = new ClientSwapStateHandler();
        swapStateHandler.load();

        registerKeyBind();
        registerPackets();
    }

    private void registerKeyBind(){
        KeyBindingRegistry.INSTANCE.addCategory("Elytra Swap");

        enableSwap = FabricKeyBinding.Builder.create(
                new Identifier(ElytraSwap.MOD_ID, "swap"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "Elytra Swap"
        ).build();

        KeyBindingRegistry.INSTANCE.register(enableSwap);

        ClientTickCallback.EVENT.register(e -> {
            if(enableSwap.isPressed()){
                if(!lastState){
                    swapStateHandler.set(!swapStateHandler.get());
                    e.player.sendMessage(new TranslatableText("Elytra Swap in now %s", swapStateHandler.get() ? "Enabled" : "Disabled"));
                    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                    passedData.writeBoolean(swapStateHandler.get());
                    ClientSidePacketRegistry.INSTANCE.sendToServer(ElytraSwap.SET_SWAP_ENABLE, passedData);
                    lastState = true;
                }
            }else{
                lastState = false;
            }
        });
    }

    private void registerPackets(){
        ClientSidePacketRegistry.INSTANCE.register(ElytraSwap.KICK_PLAYER_INTO_AIR, (packetContext, attachedData) -> {
            float speed = attachedData.readFloat();
            packetContext.getTaskQueue().execute(() -> {
                PlayerEntity player = packetContext.getPlayer();
                player.addVelocity(-Math.sin(Math.toRadians(player.yaw)) * speed, speed, Math.cos(Math.toRadians(player.yaw)) * speed);
            });
        });

        ClientSidePacketRegistry.INSTANCE.register(ElytraSwap.DUMMY_PACKAGE, ((packetContext, packetByteBuf) -> {
            System.out.println("PING!");
        }));
    }
}

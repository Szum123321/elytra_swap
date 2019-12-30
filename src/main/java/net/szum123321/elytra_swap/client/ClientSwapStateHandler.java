package net.szum123321.elytra_swap.client;

import net.fabricmc.loader.api.FabricLoader;
import net.szum123321.elytra_swap.ElytraSwap;

import java.io.*;
import java.util.Scanner;

public class ClientSwapStateHandler {
    private boolean state;

    public boolean get(){
        return state;
    }

    public void set(boolean val){
        state = val;
        save();
    }

    public void load(){
        try{
            File file = FabricLoader.getInstance().getGameDirectory().toPath().resolve("data/" + ElytraSwap.MOD_ID + ".txt").toFile();

            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            Scanner reader = new Scanner(file);

            if(reader.hasNextBoolean()) {
                state = reader.nextBoolean();
            }else{
                state = true;
            }

            reader.close();

        }catch (Exception e){
            ElytraSwap.LOGGER.error("Error while loading datafile: %s", e.toString());
            state = true;
        }
    }

    private void save(){
        try{
            File file = FabricLoader.getInstance().getGameDirectory().toPath().resolve("data/" + ElytraSwap.MOD_ID + ".txt").toFile();

            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write(state ? "true" : "false");

            writer.close();
        }catch (Exception e){
            ElytraSwap.LOGGER.error("Error while saving datafile: %s", e.toString());
            state = true;
        }
    }

}

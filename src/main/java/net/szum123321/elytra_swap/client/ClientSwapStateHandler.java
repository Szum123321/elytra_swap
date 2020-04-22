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

import net.fabricmc.loader.api.FabricLoader;
import net.szum123321.elytra_swap.ElytraSwap;

import java.io.*;
import java.util.Scanner;

/*
    This class allows for saving and loading last state in which elytra swap was (enabled or disabled).
*/

public class ClientSwapStateHandler {
    private boolean state;

    public boolean get(){
        return state;
    }

    public void set (boolean val) {
        state = val;
        save();
    }

    public void load() {
        try {
            File file = FabricLoader.getInstance().getGameDirectory().toPath().resolve("data/" + ElytraSwap.MOD_ID + ".txt").toFile();

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            Scanner reader = new Scanner(file);

            state = (reader.hasNextBoolean() ? reader.nextBoolean() : true);

            reader.close();
        } catch (Exception e) {
            ElytraSwap.LOGGER.error("Error while loading datafile: %s", e.toString());
            state = true;
        }
    }

    private void save() {
        try {
            File file = FabricLoader.getInstance().getGameDirectory().toPath().resolve("data/" + ElytraSwap.MOD_ID + ".txt").toFile();

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write(state ? "true" : "false");

            writer.close();
        } catch (Exception e) {
            ElytraSwap.LOGGER.error("Error while saving datafile: %s", e.toString());
            state = true;
        }
    }

}

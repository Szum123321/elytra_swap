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

package net.szum123321.elytra_swap.core;

import blue.endless.jankson.Comment;
import io.github.cottonmc.cotton.config.annotations.ConfigFile;
import net.szum123321.elytra_swap.ElytraSwap;

@ConfigFile(name = ElytraSwap.MOD_ID)
public class ConfigHandler {
    @Comment(value = "Changes how server treats players without mod installed\n" +
            "0: players without Elytra Swap installed are just ignored\n"+
            "1 (default): players without Elytra Swap installed will be kicked into air armor change will be forced and can only be enabled or disabled by command\n"+
            "2: for players without Elytra Swap installed armor still will be changed when falling but firework usage will be unavailable"
    )
    public int noModPlayersHandlingMethod = 1;

    @Comment(value = "This is how much vertical speed player will gain as a result of using firework rocket")
    public float kickSpeed = 1.7F;
}

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

package net.szum123321.elytra_swap;

import blue.endless.jankson.Comment;
import io.github.cottonmc.cotton.config.annotations.ConfigFile;

@SuppressWarnings("CanBeFinal")
@ConfigFile(name = ElytraSwap.MOD_ID)
public class ConfigHandler {
	@Comment(value = "\nChanges how server treats players without mod installed\n" +
			"0: players without Elytra Swap installed are just ignored\n" +
			"1 (default): players without Elytra Swap installed will be kicked into air, armor change will be forced and can only be enabled or disabled by command\n" +
			"2: for players without Elytra Swap installed, armor still will be changed when falling but firework usage will be unavailable.\n"
	)
	public int noModPlayersHandlingMethod = 1;

	@Comment(value = "\nThis is how much vertical speed player will gain as a result of using firework rocket.\n" +
						"Default: 1.7\n")
	public float kickSpeed = 1.7F;

	@Comment(value = "\nMinimal height that player has to have above him in order to use firework.\n" +
						"Default: 15\n")
	public int requiredHeightAbovePlayer = 15;

	@Comment(value = "\nIf you set this to false Elytra Swap won't look for chestplate or elytra inside shulker boxes.\n" +
						"Default: true\n")
	public boolean lookThroughShulkers = true;

	@Comment(value = "\nThis is the default state, that would be given to the players which do not have Elytra Swap installed.\n" +
					"Available: ENABLE, DISABLE\n" +
					"Default: ENABLE\n")
	public EnableDisableEnum noModPlayersDefaultState = EnableDisableEnum.ENABLE;

	@Comment(value = "\nBy setting this to DISABLE, you can completely disable firework usage.\n" +
					"Default: ENABLE\n")
	public EnableDisableEnum useFireworks = EnableDisableEnum.ENABLE;

	@Comment(value = "\nIf this potion is set to true, than every time client, without Elytra Swap mod installed joins a server, will receive short note informing about this mod.\n")
	public boolean sendInfoOnClientJoin = true;

	public enum EnableDisableEnum {
		ENABLE(true),
		DISABLE(false);

		public boolean getState() { return state; }

		private final boolean state;

		private EnableDisableEnum(boolean state) {
			this.state = state;
		}
	}
}

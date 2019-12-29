package net.szum123321.elytra_swap;

import blue.endless.jankson.Comment;
import io.github.cottonmc.cotton.config.annotations.ConfigFile;

@ConfigFile(name = ElytraSwap.MOD_ID)
public class ConfigHandler {
    @Comment(value = "Changes how server treats players without mod installed\n" +
            "0 (default): players without Elytra Swap installed are just ignored\n"+
            "1: players without Elytra Swap installed will be teleported into air and armour change will be forced\n"+
            "2: for players without Elytra Swap installed armour still will be changed when falling but firework usage will be unavailable"
    )
    public int noModPlayersHandlingMethod = 0;

    @Comment(value = "This is how much vertical speed player will gain as a result of using firework rocket")
    public float kickSpeed = 1.5F;
}

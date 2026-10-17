package me.spectral8420.noelytraduringcombat;

import me.spectral8420.noelytraduringcombat.command.NedcCommands;
import me.spectral8420.noelytraduringcombat.config.ModConfig;
import me.spectral8420.noelytraduringcombat.event.CombatEventHandler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoElytraDuringCombat implements ModInitializer {
    public static final String MOD_ID = "noelytraduringcombat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.get().load();
        CombatEventHandler.register();
        NedcCommands.register();
        LOGGER.info("NoElytraDuringCombat has been initialized!");
    }
}

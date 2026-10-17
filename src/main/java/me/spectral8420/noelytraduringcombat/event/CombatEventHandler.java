package me.spectral8420.noelytraduringcombat.event;

import me.spectral8420.noelytraduringcombat.combat.CombatTracker;
import me.spectral8420.noelytraduringcombat.config.ModConfig;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class CombatEventHandler {
    private static int tickCounter = 0;

    public static void register() {
        // PvP Combat trigger
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof ServerPlayer victim)) {
                return;
            }

            Entity attackerEntity = source.getEntity();
            if (!(attackerEntity instanceof ServerPlayer attacker)) {
                return;
            }

            if (victim == attacker) {
                return;
            }

            int combatTime = ModConfig.get().getCombatTimeInSeconds();
            CombatTracker.setCombat(victim.getUUID(), combatTime);
            CombatTracker.setCombat(attacker.getUUID(), combatTime);
        });

        // Player death clears combat timer
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayer victim) {
                CombatTracker.removeFromCombat(victim.getUUID());
            }
        });

        // Player disconnect clears combat tracking
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (handler.getPlayer() != null) {
                CombatTracker.removeFromCombat(handler.getPlayer().getUUID());
            }
        });

        // Server tick tracker and active elytra flight blocker
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;

            // Every 5 ticks (0.25 seconds)
            if (tickCounter % 5 == 0) {
                CombatTracker.tickQuarterSecond();
            }

            // Every 20 ticks (1.0 second)
            if (tickCounter % 20 == 0) {
                CombatTracker.tickSecond();
            }

            if (tickCounter >= 1000) {
                tickCounter = 0;
            }

            // Check all online players in flight
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.isFallFlying() && CombatTracker.isFlightBlocked(player)) {
                    player.stopFallFlying();
                    CombatTracker.notifyPlayer(player);
                }
            }
        });
    }
}

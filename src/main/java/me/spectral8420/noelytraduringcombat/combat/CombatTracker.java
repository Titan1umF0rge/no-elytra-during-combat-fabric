package me.spectral8420.noelytraduringcombat.combat;

import me.spectral8420.noelytraduringcombat.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatTracker {
    private static final Map<UUID, Integer> timeLeft = new ConcurrentHashMap<>();
    private static final Map<UUID, Double> messageTimeLeft = new ConcurrentHashMap<>();

    public static void setCombat(UUID uuid, int seconds) {
        timeLeft.put(uuid, seconds);
    }

    public static boolean isInCombat(UUID uuid) {
        return timeLeft.containsKey(uuid);
    }

    public static int getTimeLeft(UUID uuid) {
        return timeLeft.getOrDefault(uuid, 0);
    }

    public static void removeFromCombat(UUID uuid) {
        timeLeft.remove(uuid);
        messageTimeLeft.remove(uuid);
    }

    public static boolean containsMessageCooldown(UUID uuid) {
        return messageTimeLeft.containsKey(uuid);
    }

    public static void setMessageCooldown(UUID uuid, double seconds) {
        messageTimeLeft.put(uuid, seconds);
    }

    public static void tickSecond() {
        List<UUID> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : timeLeft.entrySet()) {
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                toRemove.add(entry.getKey());
            } else {
                timeLeft.put(entry.getKey(), remaining);
            }
        }
        for (UUID uuid : toRemove) {
            timeLeft.remove(uuid);
        }
    }

    public static void tickQuarterSecond() {
        List<UUID> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, Double> entry : messageTimeLeft.entrySet()) {
            double remaining = entry.getValue() - 0.25;
            if (remaining <= 0) {
                toRemove.add(entry.getKey());
            } else {
                messageTimeLeft.put(entry.getKey(), remaining);
            }
        }
        for (UUID uuid : toRemove) {
            messageTimeLeft.remove(uuid);
        }
    }

    public static boolean isFlightBlocked(Player player) {
        if (!isInCombat(player.getUUID())) {
            return false;
        }

        // Whitelist check by username
        String username = player.getGameProfile().name();
        if (ModConfig.get().isWhitelisted(username)) {
            return false;
        }

        // Operator bypass check (similar to permission check in Paper)
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                return false;
            }
        }

        return true;
    }

    public static void notifyPlayer(Player player) {
        UUID uuid = player.getUUID();
        if (!containsMessageCooldown(uuid)) {
            int remainingSeconds = getTimeLeft(uuid);
            String msg = ModConfig.get().getMessage("glideCancel", true);
            if (msg.contains("{time}")) {
                msg = msg.replace("{time}", String.valueOf(remainingSeconds));
            }

            Component component = Component.literal(msg);

            boolean sendActionBar = ModConfig.get().isSendToActionBar();
            boolean sendChat = ModConfig.get().isSendToChat();

            // Default to action bar if neither is set
            if (!sendActionBar && !sendChat) {
                sendActionBar = true;
            }

            // Display text feedback above hotbar (action bar)
            if (sendActionBar) {
                player.displayClientMessage(component, true);
            }

            // Display chat message if enabled
            if (sendChat) {
                player.displayClientMessage(component, false);
            }

            setMessageCooldown(uuid, ModConfig.get().getNotifyInterval());
        }
    }

    public static void clear() {
        timeLeft.clear();
        messageTimeLeft.clear();
    }
}

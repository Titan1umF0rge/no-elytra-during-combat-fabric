package me.spectral8420.noelytraduringcombat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("noelytraduringcombat");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig INSTANCE = new ModConfig();

    private int combatTimeInSeconds = 30;
    private double notifyInterval = 5.0;
    private boolean sendToActionBar = true;
    private boolean sendToChat = false;
    private List<String> flightWhitelist = new ArrayList<>();
    private Messages messages = new Messages();

    public static class Messages {
        private String prefix = "§6§lN.E.D.C §r§7| §r";
        private String glideCancel = "§cElytras are disabled during combat.";
        private String reload = "§aAll configs were reloaded!";
        private String save = "§aAll configs were saved!";

        public String getPrefix() {
            return prefix;
        }

        public String getGlideCancel() {
            return glideCancel;
        }

        public String getReload() {
            return reload;
        }

        public String getSave() {
            return save;
        }
    }

    public static ModConfig get() {
        return INSTANCE;
    }

    public static void setInstance(ModConfig instance) {
        INSTANCE = instance;
    }

    public synchronized void load() {
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            if (flightWhitelist.isEmpty()) {
                flightWhitelist.add("ExamplePlayerName");
            }
            save();
            return;
        }

        try (FileReader reader = new FileReader(configFile, StandardCharsets.UTF_8)) {
            ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
            if (loaded != null) {
                if (loaded.flightWhitelist == null) {
                    loaded.flightWhitelist = new ArrayList<>();
                }
                if (loaded.messages == null) {
                    loaded.messages = new Messages();
                }
                INSTANCE = loaded;
                LOGGER.info("Configuration loaded successfully from {}", configFile.getName());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load configuration file", e);
        }
    }

    public synchronized void save() {
        File configFile = getConfigFile();
        try {
            File parent = configFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(configFile, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
                LOGGER.info("Configuration saved successfully to {}", configFile.getName());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save configuration file", e);
        }
    }

    private File getConfigFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("noelytraduringcombat.json").toFile();
    }

    public boolean isWhitelisted(String username) {
        if (username == null || flightWhitelist == null) {
            return false;
        }
        for (String name : flightWhitelist) {
            if (name.equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public String getMessage(String key, boolean includePrefix) {
        if (messages == null) {
            return key;
        }
        String msg = switch (key) {
            case "glideCancel" -> messages.getGlideCancel();
            case "reload" -> messages.getReload();
            case "save" -> messages.getSave();
            default -> key;
        };
        if (includePrefix && messages.getPrefix() != null) {
            return messages.getPrefix() + msg;
        }
        return msg;
    }

    public int getCombatTimeInSeconds() {
        return combatTimeInSeconds;
    }

    public void setCombatTimeInSeconds(int combatTimeInSeconds) {
        this.combatTimeInSeconds = combatTimeInSeconds;
    }

    public double getNotifyInterval() {
        return notifyInterval;
    }

    public void setNotifyInterval(double notifyInterval) {
        this.notifyInterval = notifyInterval;
    }

    public boolean isSendToActionBar() {
        return sendToActionBar;
    }

    public void setSendToActionBar(boolean sendToActionBar) {
        this.sendToActionBar = sendToActionBar;
    }

    public boolean isSendToChat() {
        return sendToChat;
    }

    public void setSendToChat(boolean sendToChat) {
        this.sendToChat = sendToChat;
    }

    public List<String> getFlightWhitelist() {
        return flightWhitelist;
    }

    public void setFlightWhitelist(List<String> flightWhitelist) {
        this.flightWhitelist = flightWhitelist;
    }

    public Messages getMessages() {
        return messages;
    }
}

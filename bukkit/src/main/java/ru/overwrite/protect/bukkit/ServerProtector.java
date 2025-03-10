package ru.overwrite.protect.bukkit;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;
import ru.overwrite.protect.bukkit.utils.logging.Logger;

import java.time.LocalDateTime;

public final class ServerProtector extends ServerProtectorManager {

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        if (!isPaper()) {
            return;
        }
        saveDefaultConfig();
        final FileConfiguration config = getConfig();
        final ConfigurationSection mainSettings = config.getConfigurationSection("main-settings");
        setupLogger(config);
        setupProxy(config);
        loadConfigs(config);
        PluginManager pluginManager = server.getPluginManager();
        checkSafe(pluginManager);
        checkPaper();
        registerListeners(pluginManager);
        registerCommands(pluginManager, mainSettings);
        startTasks(config);
        logEnableDisable(getPluginConfig().getLogMessages().enabled(), LocalDateTime.now());
        if (mainSettings.getBoolean("enable-metrics", true)) {
            new Metrics(this, 13347);
        }
        checkForUpdates(mainSettings);
        long endTime = System.currentTimeMillis();
        getPluginLogger().info("Plugin started in " + (endTime - startTime) + " ms");
    }

    public boolean isPaper() {
        if (getServer().getName().equals("CraftBukkit")) {
            Logger pluginLogger = getPluginLogger();
            pluginLogger.info(" ");
            pluginLogger.info("§6============= §c! WARNING ! §6=============");
            pluginLogger.info("§eЭтот плагин работает только на Paper и его форках!");
            pluginLogger.info("§eАвтор плагина §cкатегорически §eвыступает за отказ от использования устаревшего и уязвимого софта!");
            pluginLogger.info("§eСкачать Paper: §ahttps://papermc.io/downloads/all");
            pluginLogger.info("§6============= §c! WARNING ! §6=============");
            pluginLogger.info(" ");
            this.setEnabled(false);
            return false;
        }
        return true;
    }

    @Override
    public void onDisable() {
        if (getMessageFile() != null) {
            logEnableDisable(getPluginConfig().getLogMessages().disabled(), LocalDateTime.now());
        }
        if (getPluginConfig().getMessageSettings().enableBroadcasts()) {
            for (Player onlinePlayer : server.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("serverprotector.admin") && getMessageFile() != null) {
                    onlinePlayer.sendMessage(getPluginConfig().getBroadcasts().disabled());
                }
            }
        }
        getRunner().cancelTasks();
        if (getPluginMessage() != null) {
            Messenger messenger = server.getMessenger();
            messenger.unregisterOutgoingPluginChannel(this);
            messenger.unregisterIncomingPluginChannel(this);
        }
        if (getConfig().getBoolean("secure-settings.shutdown-on-disable")) {
            server.shutdown();
        }
    }
}

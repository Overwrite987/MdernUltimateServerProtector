package ru.overwrite.protect.bukkit.listeners;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import ru.overwrite.protect.bukkit.ServerProtectorManager;
import ru.overwrite.protect.bukkit.api.ServerProtectorAPI;
import ru.overwrite.protect.bukkit.api.events.ServerProtectorPasswordSuccessEvent;
import ru.overwrite.protect.bukkit.configuration.Config;

import java.util.Objects;

public class TabCompleteListener implements Listener {

    private final ServerProtectorAPI api;
    private final Config pluginConfig;

    public TabCompleteListener(ServerProtectorManager plugin) {
        this.api = plugin.getApi();
        this.pluginConfig = plugin.getPluginConfig();
    }

    @EventHandler(ignoreCancelled = true)
    public void onTabComplete(AsyncTabCompleteEvent e) {
        if (!(e.getSender() instanceof Player player))
            return;
        if (pluginConfig.getBlockingSettings().blockTabComplete()) {
            api.handleInteraction(player, e);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent e) {
        if (pluginConfig.getBlockingSettings().blockTabComplete()) {
            if (api.isCaptured(e.getPlayer())) {
                e.getCommands().removeIf(command -> !Objects.equals(command, pluginConfig.getMainSettings().pasCommand()));
            }
        }
    }

    @EventHandler
    public void onSucsessPassword(ServerProtectorPasswordSuccessEvent e) {
        e.getPlayer().updateCommands();
    }
}

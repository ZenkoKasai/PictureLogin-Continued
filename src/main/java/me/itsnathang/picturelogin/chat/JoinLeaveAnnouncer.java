package me.itsnathang.picturelogin.chat;

import me.itsnathang.picturelogin.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public final class JoinLeaveAnnouncer implements Listener {

    private final ConfigManager config;

    public JoinLeaveAnnouncer(ConfigManager config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        if (!config.getBoolean("join-leave.enabled", true)) return;

        if (config.getBoolean("join-leave.suppress-vanilla", true)) {
            event.setJoinMessage(null);
        }

        if (!config.getBoolean("join-leave.join.enabled", true)) return;

        Player actor = event.getPlayer();
        List<String> lines = config.getConfig().getStringList("join-leave.join.message");
        broadcastLines(lines, actor, config.getBoolean("join-leave.include-actor", true));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        if (!config.getBoolean("join-leave.enabled", true)) return;

        if (config.getBoolean("join-leave.suppress-vanilla", true)) {
            event.setQuitMessage(null);
        }

        if (!config.getBoolean("join-leave.leave.enabled", true)) return;

        Player actor = event.getPlayer();
        List<String> lines = config.getConfig().getStringList("join-leave.leave.message");
        broadcastLines(lines, actor, config.getBoolean("join-leave.include-actor", true));
    }

    private void broadcastLines(List<String> lines, Player actor, boolean includeActor) {
        if (lines == null || lines.isEmpty()) return;

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!includeActor && viewer.getUniqueId().equals(actor.getUniqueId())) continue;

            for (String raw : lines) {
                if (raw == null) continue;
                viewer.sendMessage(color(applyPlaceholders(raw, actor)));
            }
        }
    }

    private String applyPlaceholders(String s, Player p) {
        return s
                .replace("%player%", p.getName())
                .replace("%displayname%", p.getDisplayName())
                .replace("%world%", p.getWorld().getName());
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}

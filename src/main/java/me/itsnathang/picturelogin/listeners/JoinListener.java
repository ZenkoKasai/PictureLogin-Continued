package me.itsnathang.picturelogin.listeners;

import fr.xephi.authme.api.v3.AuthMeApi;
import me.itsnathang.picturelogin.PictureLogin;
import me.itsnathang.picturelogin.config.ConfigManager;
import me.itsnathang.picturelogin.util.Hooks;
import me.itsnathang.picturelogin.util.PictureUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {
    private final PictureLogin plugin;
    private final ConfigManager config;
    private final PictureUtil utils;

    public JoinListener(PictureLogin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.utils = plugin.getPictureUtil();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // block the default join message
        if (config.getBoolean("join-leave.suppress-vanilla", true)) {
            event.setJoinMessage(null);
        } else if (config.getBoolean("block-join-message", false)) {
            event.setJoinMessage(null);
        }

        if (Hooks.AUTHME) {
            authMeLogin(player);
        } else {
            utils.sendImage(player);
        }
    }

    private void authMeLogin(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player == null || !player.isOnline()) {
                    cancel();
                    return;
                }

                if (AuthMeApi.getInstance().isAuthenticated(player)) {
                    utils.sendImage(player);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}

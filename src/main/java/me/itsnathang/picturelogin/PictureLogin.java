package me.itsnathang.picturelogin;

import me.itsnathang.picturelogin.chat.JoinLeaveAnnouncer;
import me.itsnathang.picturelogin.commands.BaseCommand;
import me.itsnathang.picturelogin.config.ConfigManager;
import me.itsnathang.picturelogin.listeners.JoinListener;
import me.itsnathang.picturelogin.listeners.QuitListener;
import me.itsnathang.picturelogin.util.Hooks;
import me.itsnathang.picturelogin.util.PictureUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

/*
Updated to work with Minecraft 1.18.2 by _NickV on 4/7/2022
 */
public class PictureLogin extends JavaPlugin {
    private ConfigManager configManager;
    private PictureUtil pictureUtil;

    @Override
    public void onEnable() {
        // load config & languages file
        configManager = new ConfigManager(this);

        // Config-driven multi-line join/leave announcer
        getServer().getPluginManager().registerEvents(
                new JoinLeaveAnnouncer(configManager),
                this
        );

        // Register Plugin Hooks
        new Hooks(getServer().getPluginManager(), configManager, getLogger());

        // Initialize Picture Utility
        pictureUtil = new PictureUtil(this);

        // register Listeners (picture login image)
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        // register leave picture (separate from normal leave chat messages)
        if (configManager.getBoolean("leave-picture.enabled", true)) {
            getServer().getPluginManager().registerEvents(new QuitListener(this), this);
        }

        // register /picturelogin command
        getCommand("picturelogin").setExecutor(new BaseCommand(this));

        // bStats integration
        if (configManager.getBoolean("metrics", true)) {
            new Metrics(this, 14892);
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PictureUtil getPictureUtil() {
        return pictureUtil;
    }
}

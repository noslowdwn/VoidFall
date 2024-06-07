package noslowdwn.voidfall;

import noslowdwn.voidfall.events.OnHeightReach;
import noslowdwn.voidfall.utils.ColorsParser;
import noslowdwn.voidfall.utils.Config;
import noslowdwn.voidfall.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class VoidFall extends JavaPlugin {

    public static VoidFall instance;

    @Override
    public void onEnable() {
        instance = this;
        Config.load();
        Config.checkVersion();

        this.getCommand("voidfall").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player && !sender.hasPermission("voidfall.reload")) {
                sender.sendMessage(ColorsParser.of(getConfig().getString("messages.no-permission")));
                return true;
            }

            reloadConfig();
            Config.checkVersion();
            sender.sendMessage(ColorsParser.of(getConfig().getString("messages.reload-message")));

            return true;
        });

        this.getServer().getPluginManager().registerEvents(new OnHeightReach(), this);

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, UpdateChecker::checkVersion, 60L);
    }

    public static void debug(String error) {
        if (instance.getConfig().getBoolean("debug-mode", false))
            Bukkit.getConsoleSender().sendMessage(ColorsParser.of(error));
    }
}

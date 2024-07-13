package noslowdwn.voidfall;

import noslowdwn.voidfall.events.OnHeightReach;
import noslowdwn.voidfall.events.VoidDamage;
import noslowdwn.voidfall.utils.Parser;
import noslowdwn.voidfall.utils.Config;
import noslowdwn.voidfall.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoidFall extends JavaPlugin {

    public static VoidFall instance;

    @Override
    public void onEnable() {
        instance = this;
        Config.load();
        Config.checkVersion();

        this.getCommand("voidfall").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player && !sender.hasPermission("voidfall.reload")) {
                sender.sendMessage(Parser.hex(Parser.applyPlaceholders((Player) sender, (getConfig().getString("messages.no-permission")
                                                                            .replace("%player%", sender.getName()
                                                                            .replace("%world%", ((Player) sender).getWorld().getName()))))));
                return true;
            }

            Config.load();
            reloadConfig();
            Config.checkVersion();
            if (sender instanceof Player)
                sender.sendMessage(Parser.hex(Parser.applyPlaceholders((Player) sender, (getConfig().getString("messages.reload-message")
                                                                                                .replace("%player%", sender.getName()
                                                                                                .replace("%world%", ((Player) sender).getWorld().getName()))))));
            else
                sender.sendMessage(Parser.hex((getConfig().getString("messages.reload-message"))));

            return true;
        });

        this.getServer().getPluginManager().registerEvents(new OnHeightReach(), this);
        this.getServer().getPluginManager().registerEvents(new VoidDamage(), this);

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, UpdateChecker::checkVersion, 60L);
    }

    public static void debug(String error) {
        if (instance.getConfig().getBoolean("debug-mode", false))
            Bukkit.getConsoleSender().sendMessage(Parser.hex(error));
    }
}

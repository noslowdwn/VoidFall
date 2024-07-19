package noslowdwn.voidfall;

import noslowdwn.voidfall.handlers.PlayerEvents;
import noslowdwn.voidfall.handlers.Region;
import noslowdwn.voidfall.handlers.YCords;
import noslowdwn.voidfall.utils.ColorsParser;
import noslowdwn.voidfall.utils.Config;
import noslowdwn.voidfall.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VoidFall extends JavaPlugin
{

    private static VoidFall instance;

    @Override
    public void onEnable()
    {
        setInstance(this);
        Config.load();
        Config.checkVersion();

        Plugin wgEvents = Bukkit.getPluginManager().getPlugin("WorldGuardEvents");

        this.getCommand("voidfall").setExecutor((sender, command, label, args) ->
        {
            if (sender instanceof Player && !sender.hasPermission("voidfall.reload"))
            {
                sender.sendMessage(ColorsParser.of(sender, getConfig().getString("messages.no-permission")));
                return true;
            }

            Config.load();
            Config.checkVersion();

            reloadConfig();

            sender.sendMessage(ColorsParser.of(sender, getConfig().getString("messages.reload-message")));


            if (!wgEvents.isEnabled())
            {
                debug("[VoidFall] Actions on region enter/leave will be disabled!", null, "info");
                debug("[VoidFall] Please download WorldGuardEvents to enable them.", null, "info");
                debug("[WorldGuardEvents] https://www.spigotmc.org/resources/worldguard-events.65176/", null, "info");
            }

            return true;
        });

        this.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        this.getServer().getPluginManager().registerEvents(new YCords(), this);

        if (wgEvents.isEnabled())
        {
            this.getServer().getPluginManager().registerEvents(new Region(), this);
        }
        else
        {
            debug("[VoidFall] Actions on region enter/leave will be disabled!", null, "info");
            debug("[VoidFall] Please download WorldGuardEvents to enable them.", null, "info");
            debug("[WorldGuardEvents] https://www.spigotmc.org/resources/worldguard-events.65176/", null, "info");
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, UpdateChecker::checkVersion, 60L);
    }

    public static VoidFall getInstance()
    {
        return instance;
    }

    public static void setInstance(VoidFall instance)
    {
        Objects.requireNonNull(instance);
        if (VoidFall.instance != null)
        {
            throw new UnsupportedOperationException();
        }
        VoidFall.instance = instance;
    }

    public static void debug(String msg, Player p, String type)
    {
        if (instance.getConfig().getBoolean("debug-mode", false))
        {
            switch (type)
            {
                case "info":
                    Bukkit.getLogger().info(ColorsParser.of(p, msg));
                    break;
                case "warn":
                    Bukkit.getLogger().warning(ColorsParser.of(p, msg));
                    break;
            }
        }
    }
}
package noslowdwn.voidfall;

import noslowdwn.voidfall.handlers.PlayerEvents;
import noslowdwn.voidfall.handlers.Region;
import noslowdwn.voidfall.handlers.YCords;
import noslowdwn.voidfall.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VoidFall extends JavaPlugin
{

    private static VoidFall instance;
    private static int subVersion;

    @Override
    public void onEnable()
    {
        setInstance(this);
        subVersion = extractMainVersion(Bukkit.getVersion());

        Config.load();
        Config.checkVersion();

        ConfigValues.initializeAll();

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

            ConfigValues.initializeAll();
            sender.sendMessage(ColorsParser.of(sender, getConfig().getString("messages.reload-message")));

            if (wgEvents != null && !wgEvents.isEnabled())
            {
                debug("[VoidFall] Actions on region enter/leave will be disabled!", null, "info");
                debug("[VoidFall] Please download WorldGuardEvents to enable them.", null, "info");
                debug("[WorldGuardEvents] https://www.spigotmc.org/resources/worldguard-events.65176/", null, "info");
            }

            return true;
        });

        this.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        this.getServer().getPluginManager().registerEvents(new YCords(), this);

        if (wgEvents != null && wgEvents.isEnabled())
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

    private int extractMainVersion(String versionString) {
        Pattern p = Pattern.compile("(?<=MC: |^)(1\\.\\d+)(?=\\D|$)");
        Matcher m = p.matcher(versionString);
        if (m.find()) {
            return Integer.parseInt(m.group().replace("1.", ""));
        } else {
            return 0;
        }

    }

    public static int getSubVersion()
    {
        return subVersion;
    }

}
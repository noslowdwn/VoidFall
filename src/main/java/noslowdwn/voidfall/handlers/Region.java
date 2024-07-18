package noslowdwn.voidfall.handlers;

import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import noslowdwn.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static noslowdwn.voidfall.VoidFall.getInstance;

public class Region implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionEntered(RegionEnteredEvent e) {
        final Player player = Bukkit.getPlayer(e.getUUID()); if (player == null) return;

        final boolean hasKeys = getInstance().getConfig().getConfigurationSection("regions").getKeys(false).isEmpty();
        if (hasKeys) return;

        final String regionName = e.getRegionName();

        final String worldName = player.getWorld().getName();
        final boolean containsRegion = getInstance().getConfig().contains("regions." + regionName);
        final boolean containsAction = getInstance().getConfig().contains("regions." + regionName + ".on-enter");
        final boolean containsWorld = getInstance().getConfig().getStringList("regions." + regionName + ".worlds").contains(worldName);

        if (!containsRegion || !containsAction || !containsWorld) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final boolean isRandom = getInstance().getConfig().getBoolean("regions." + regionName + ".on-enter.random", false);
                final List<String> commands = getInstance().getConfig().getStringList("regions." + regionName + ".on-enter.execute-commands");

                if (commands.isEmpty())
                {
                    VoidFall.debug("Nothing to execute because commands list are empty!", player, "warn");
                    VoidFall.debug("Path to: regions." + regionName + ".on-enter.execute-commands", player, "warn");
                    return;
                }

                if (isRandom)
                {
                    Actions.executeRandom(player, commands, worldName);
                }
                else
                {
                    for (String cmd : commands)
                    {
                        Actions.execute(player, cmd, worldName);
                    }
                }
            }
        }.runTaskAsynchronously(getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionLeave(RegionLeftEvent e) {
        final Player player = Bukkit.getPlayer(e.getUUID()); if (player == null) return;

        final boolean hasKeys = getInstance().getConfig().getConfigurationSection("regions").getKeys(false).isEmpty();
        if (hasKeys) return;

        final String regionName = e.getRegionName();

        final String worldName = player.getWorld().getName();
        final boolean containsRegion = getInstance().getConfig().contains("regions." + regionName);
        final boolean containsAction = getInstance().getConfig().contains("regions." + regionName + ".on-leave");
        final boolean containsWorld = getInstance().getConfig().getStringList("regions." + regionName + ".worlds").contains(worldName);

        if (!containsRegion || !containsAction || !containsWorld) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final boolean isRandom = getInstance().getConfig().getBoolean("regions." + regionName + ".on-leave.random");
                final List<String> commands = getInstance().getConfig().getStringList("regions." + regionName + ".on-leave.execute-commands");

                if (!commands.isEmpty())
                {
                    if (isRandom)
                    {
                        Actions.executeRandom(player, commands, worldName);
                    }
                    else
                    {
                        for (String cmd : commands)
                        {
                            Actions.execute(player, cmd, worldName);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(getInstance());
    }
}

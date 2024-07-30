package noslowdwn.voidfall.handlers;

import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static noslowdwn.voidfall.VoidFall.getInstance;

public class Region implements Listener
{

    private static final ConfigValues values = new ConfigValues();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionEntered(RegionEnteredEvent e)
    {
        if (values.isRegionsEmpty()) return;

        final Player player = Bukkit.getPlayer(e.getUUID());
        if (player == null) return;

        final String regionName = e.getRegionName();
        final String worldName = player.getWorld().getName();

        if (!values.containsEnterRegionWorld(regionName, worldName)) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final List<String> commands = values.getEntryRegionsCommands(regionName);

                if (commands.isEmpty())
                {
                    VoidFall.debug("Nothing to execute because commands list are empty!", player, "warn");
                    VoidFall.debug("Path to: regions." + regionName + ".on-enter.execute-commands", player, "warn");
                    return;
                }

                if (values.entryRegionsAreUsingRandom(regionName))
                {
                    Actions.executeRandom(player, commands, worldName, "regions");
                }
                else
                {
                    for (String cmd : commands)
                    {
                        Actions.execute(player, cmd, worldName, "regions");
                    }
                }
            }
        }.runTaskAsynchronously(getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionLeave(RegionLeftEvent e)
    {
        if (values.isRegionsEmpty()) return;

        final Player player = Bukkit.getPlayer(e.getUUID());
        if (player == null) return;

        final String regionName = e.getRegionName();
        final String worldName = player.getWorld().getName();

        if (!values.containsLeaveRegionWorld(regionName, worldName)) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final List<String> commands = values.getLeaveRegionsCommands(regionName);

                if (!commands.isEmpty())
                {
                    if (values.leaveRegionsAreUsingRandom(regionName))
                    {
                        Actions.executeRandom(player, commands, worldName, "regions");
                    }
                    else
                    {
                        for (String cmd : commands)
                        {
                            Actions.execute(player, cmd, worldName, "regions");
                        }
                    }
                }
            }
        }.runTaskAsynchronously(getInstance());
    }
}
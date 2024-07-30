package noslowdwn.voidfall.handlers;

import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static noslowdwn.voidfall.VoidFall.getInstance;

public class YCords implements Listener
{

    private final Set<Player> executing = new HashSet<>();
    private static final ConfigValues values = new ConfigValues();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        final Player p = e.getPlayer();
        if (executing.contains(p)) return;

        final String world = p.getWorld().getName();
        if (!values.containsWorld(world)) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final double pHeight = p.getLocation().getY();

                if (executing.contains(p)) return;
                if (values.worldHasFloorMode(world) && pHeight <= values.worldFloorHeight(world))
                {
                    remove(p, world);
                    run(p, world, "floor");
                }
                else if (values.worldHasRoofMode(world) && pHeight >= values.worldRoofHeight(world))
                {
                    remove(p, world);
                    run(p, world, "roof");
                }
            }

            private void remove(Player p, String world)
            {
                Bukkit.getScheduler().runTaskLater(
                        getInstance(),
                        () -> executing.remove(p),
                        values.getWorldRepeatFix(world) * 20L
                );
            }

            private void run(Player p, String world, String mode)
            {
                final List<String> commands = values.getWorldCommands(world, mode);

                if (commands.isEmpty())
                {
                    VoidFall.debug("Nothing to execute because commands list are empty!", p, "warn");
                    VoidFall.debug("Path to: worlds." + world + "." + mode + ".execute-commands", p, "warn");
                    return;
                }

                executing.add(p);

                if (values.isWorldRunModeRandom(world, mode))
                {
                    Actions.executeRandom(p, commands, world, "worlds");
                }
                else
                {
                    for (String str : commands)
                    {
                        Actions.execute(p, str, world, "worlds");
                    }
                }
            }
        }.runTaskAsynchronously(getInstance());
    }
}
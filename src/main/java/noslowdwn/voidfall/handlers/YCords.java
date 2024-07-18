package noslowdwn.voidfall.handlers;

import noslowdwn.voidfall.VoidFall;
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        if (getInstance().getConfig().getConfigurationSection("worlds").getKeys(false).isEmpty()) return;

        final Player p = e.getPlayer();
        if (executing.contains(p)) return;

        final String world = p.getWorld().getName();
        if (!getInstance().getConfig().contains("worlds." + world)) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {

                final double pHeight = p.getLocation().getY();

                for (String mode : getInstance().getConfig().getConfigurationSection("worlds." + world).getKeys(false))
                {
                    if (pHeight <= getInstance().getConfig().getInt("worlds." + world + ".floor.executing-height") && mode.equalsIgnoreCase("floor"))
                    {
                        run(p, world, mode);
                    }
                    if (pHeight >= getInstance().getConfig().getInt("worlds." + world + ".roof.executing-height") && mode.equalsIgnoreCase("roof"))
                    {
                        run(p, world, mode);
                    }
                }
            }

            private void run(Player p, String world, String mode)
            {
                final List<String> commands = getInstance().getConfig().getStringList("worlds." + world + "." + mode + ".execute-commands");

                if (commands.isEmpty())
                {
                    VoidFall.debug("Nothing to execute because commands list are empty!", p, "warn");
                    VoidFall.debug("Path to: worlds." + world + "." + mode + ".execute-commands", p, "warn");
                    return;
                }

                executing.add(p);

                if (getInstance().getConfig().getBoolean("worlds." + world + "." + mode + ".random", false))
                {
                    Actions.executeRandom(p, commands, world);
                }
                else
                {
                    for (String str : commands)
                    {
                        Actions.execute(p, str, world);
                    }
                }

                Bukkit.getScheduler().runTaskLater(
                        getInstance(),
                        () -> executing.remove(p),
                        getInstance().getConfig().getInt("worlds." + world + ".floor.repeat-fix", 3) * 20L
                );
            }
        }.runTaskAsynchronously(getInstance());
    }
}
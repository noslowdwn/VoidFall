package noslowdwn.voidfall.handlers;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static noslowdwn.voidfall.VoidFall.getInstance;

public class PlayerEvents implements Listener
{

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        if (p == null) return;
        if (!getInstance().getConfig().contains("player.on-server-join")) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final boolean isRandom = getInstance().getConfig().getBoolean("player.on-server-join.random", false);
                final List<String> commands = getInstance().getConfig().getStringList("player.on-server-join.execute-commands");

                if (commands.isEmpty())
                {
                    VoidFall.debug("Nothing to execute because commands list are empty!", e.getPlayer(), "warn");
                    VoidFall.debug("Path to: player.on-server-join.execute-commands", e.getPlayer(), "warn");
                    return;
                }

                if (isRandom)
                {
                    Actions.executeRandom(p, commands, p.getWorld().toString());
                }
                else
                {
                    for (String cmd : commands)
                    {
                        Actions.execute(p, cmd, p.getWorld().toString());
                    }
                }
            }
        }.runTaskAsynchronously(getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        final Player p = e.getPlayer();

        if (p == null) return;
        if (!getInstance().getConfig().contains("player.on-server-leave")) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final boolean isRandom = getInstance().getConfig().getBoolean("player.on-server-leave.random", false);
                final List<String> commands = getInstance().getConfig().getStringList("player.on-server-leave.execute-commands");

                if (commands.isEmpty())
                {
                    VoidFall.debug("Nothing to execute because commands list are empty!", e.getPlayer(), "warn");
                    VoidFall.debug("Path to: player.on-server-leave.execute-commands", e.getPlayer(), "warn");
                    return;
                }

                if (isRandom)
                {
                    Actions.executeRandom(p, commands, p.getWorld().toString());
                }
                else
                {
                    for (String cmd : commands)
                    {
                        Actions.execute(p, cmd, p.getWorld().toString());
                    }
                }
            }
        }.runTaskAsynchronously(getInstance());
    }
}

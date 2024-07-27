package noslowdwn.voidfall.handlers;

import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static noslowdwn.voidfall.VoidFall.getInstance;

public class PlayerEvents implements Listener
{

    private static final ConfigValues values = new ConfigValues();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        if (p == null) return;
        if (!values.isPlayerServerJoinTriggerEnabled()) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final List<String> commands = values.getPlayerServerJoinCommands();

                if (commands.isEmpty())
                {
                    VoidFall.debug("Nothing to execute because commands list are empty!", p, "warn");
                    VoidFall.debug("Path to: player.on-server-join.execute-commands", p, "warn");
                    return;
                }

                if (values.isPlayerServerJoinTriggerRandom())
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
        if (!values.isPlayerServerQuitTriggerEnabled()) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final List<String> commands = values.getPlayerServerQuitCommands();

                if (commands.isEmpty())
                {
                    VoidFall.debug("Nothing to execute because commands list are empty!", p, "warn");
                    VoidFall.debug("Path to: player.on-server-leave.execute-commands", p, "warn");
                    return;
                }

                if (values.isPlayerServerQuitTriggerRandom())
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
    public void playerDeathEvent(PlayerDeathEvent e) {
        final Player p = e.getEntity().getPlayer();

        if (p == null) return;
        if (!values.isPlayerDeathTriggerEnabled()) return;
        if (values.isInstantlyRespawnEnabled())
        {
            new BukkitRunnable() {
                public void run() {
                    p.spigot().respawn();
                }
            }.runTaskLater(getInstance(), 1L);
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final List<String> commands = values.getPlayerDeathCommands();

                if (commands.isEmpty())
                {
                    VoidFall.debug("Nothing to execute because commands list are empty!", p, "warn");
                    VoidFall.debug("Path to: player.on-server-leave.execute-commands", p, "warn");
                    return;
                }

                if (values.isPlayerDeathTriggerRandom())
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

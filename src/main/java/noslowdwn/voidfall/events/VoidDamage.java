package noslowdwn.voidfall.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static noslowdwn.voidfall.VoidFall.instance;
import static noslowdwn.voidfall.utils.Parser.applyBoolean;

public class VoidDamage implements Listener {

    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.VOID) ||
                !(e.getEntity() instanceof Player) ||
                !instance.getConfig().getConfigurationSection("worlds").getKeys(false).isEmpty()) return;

        Player p = (Player) e.getEntity();
        String world = p.getWorld().getName();

        if (instance.getConfig().contains("worlds." + world + ".floor.disable-void-damage")
                && applyBoolean(p, "worlds." + world + ".floor.disable-void-damage")) {
            e.setDamage(0);
        }
    }
}

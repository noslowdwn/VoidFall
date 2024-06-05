package noslowdwn.voidfall;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Set;
import java.util.HashSet;

import static noslowdwn.voidfall.VoidFall.instance;
import static org.bukkit.Sound.ENTITY_SHULKER_HURT_CLOSED;

public class FallEvent implements Listener {

    private final Set<Player> executing = new HashSet<>();

    @EventHandler
    public void onVoidFall(PlayerMoveEvent e) {
        if (instance.getConfig().getConfigurationSection("worlds").getKeys(false).isEmpty()) return;

        Player p = e.getPlayer();
        String world = p.getWorld().getName();

        if (executing.contains(p)) return;

        if (instance.getConfig().contains("worlds." + world)) {
            //if (p.getLocation().getY() <= instance.getConfig().getInt("worlds." + world + ".execute-on-height")) {
            if (checkMode(p, instance.getConfig().getString("worlds." + world + ".mode"), world)) {
                executing.add(p);
                for (String str : instance.getConfig().getStringList("worlds." + world + ".execute-commands")) {
                    str = str.replace("%player%", p.getName())
                            .replace("%world%", world);

                    if (str.startsWith("[CONSOLE] ")) {
                        str = str.replace("[CONSOLE] ", "");
                        if (str.equals("")) {
                            VoidFall.debug("There are not enough arguments to run the command in console");
                            VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                        } else {
                            instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), str.replace("[CONSOLE] ", ""));
                        }
                    }

                    else if (str.startsWith("[MESSAGE] ")) {
                        str = str.replace("[MESSAGE] ", "");
                        if (str.equals("")) {
                            VoidFall.debug("There are not enough arguments to send the message to a player");
                            VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                        } else {
                            p.sendMessage(ColorsUtil.of(str));
                        }
                    }

                    else if (str.startsWith("[PLAYER] ")) {
                        str = str.replace("[PLAYER] ", "");
                        if (str.equals("")) {
                            VoidFall.debug("There are not enough arguments to execute the command from a player");
                            VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                        } else {
                            p.chat("/" + str);
                        }
                    }

                    else if (str.startsWith("[TITLE] ")) {
                        str = str.replace("[TITLE] ", "");
                        if (str.equals("")) {
                            VoidFall.debug("There are not enough arguments to display the title message");
                            VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                        } else {
                            String[] title = str.split(";", 2);
                            String main = title[0];
                            String sub = title[1];
                            p.sendTitle(ColorsUtil.of(main), ColorsUtil.of(sub), 10, 40, 10);
                        }
                    }

                    else if (str.startsWith("[ACTIONBAR] ")) {
                        str = str.replace("[ACTIONBAR] ", "");
                        if (str.equals("")) {
                            VoidFall.debug("There are not enough arguments to display the message in actionbar");
                            VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                        } else
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                    ColorsUtil.of(str.replace("[ACTIONBAR] ", ""))));
                    }

                    else if (str.startsWith("[PLAY_SOUND] ")) {
                        String[] params = str.replace("[PLAY_SOUND] ", "").split(";", 3);
                        if (params.length == 0) {
                            VoidFall.debug("There are not enough arguments to play the sound");
                            VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                            return;
                        }

                        Sound sound = ENTITY_SHULKER_HURT_CLOSED;
                        try {
                            sound = Sound.valueOf(params[0].toUpperCase());
                        } catch (final IllegalArgumentException exception) {
                            VoidFall.debug("Sound name given for sound action: " + params[0] + ", is not a valid sound!");
                        }

                        float volume = 1, pitch = 1;
                        if (params.length == 2) {
                            try {
                                volume = Float.parseFloat(params[1]);
                            } catch (final NumberFormatException exception) {
                                VoidFall.debug("Volume given for sound action: " + params[1] + ", is not a valid number!");
                            }
                        }

                        if (params.length == 3) {
                            try {
                                pitch = Float.parseFloat(params[2]);
                            } catch (final NumberFormatException exception) {
                                VoidFall.debug("Pitch given for sound action: " + params[2] + ", is not a valid number!");
                            }
                        }

                        p.playSound(p.getLocation(), sound, volume, pitch);
                    }

                    else {
                        VoidFall.debug("&cYou're trying to cause an action that doesn't exist.");
                        VoidFall.debug("&cPath to: worlds." + world + ".execute-commands");
                        VoidFall.debug("&cAction: " + str);
                    }
                }
                Bukkit.getScheduler().runTaskLater(instance, () -> executing.remove(p), instance.getConfig().getInt("worlds." + world + ".repeat-fix", 3) * 20L);
            }
        }
    }

    private boolean checkMode(Player p, String mode, String world) {
        switch (mode) {
            case "floor":
                return p.getLocation().getY() <= instance.getConfig().getInt("worlds." + world + ".execute-on-height", 0);
            case "roof":
                return p.getLocation().getY() >= instance.getConfig().getInt("worlds." + world + ".execute-on-height", 127);
            default:
                VoidFall.debug("&You're trying to use a non-existent mod.");
                VoidFall.debug("&cPath to: worlds." + world + ".mode");
                VoidFall.debug("&cMode: " + mode);
                return p.getLocation().getY() <= instance.getConfig().getInt("worlds." + world + ".execute-on-height", 0);
        }
    }
}

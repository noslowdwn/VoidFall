package noslowdwn.voidfall.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.Parser;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

import static noslowdwn.voidfall.VoidFall.instance;
import static org.bukkit.Sound.ENTITY_SHULKER_HURT_CLOSED;

public class OnHeightReach implements Listener {

    public static final Set<Player> executing = new HashSet<>();

    @EventHandler
    public void onVoidFall(PlayerMoveEvent e) {
        if (instance.getConfig().getConfigurationSection("worlds").getKeys(false).isEmpty()) return;

        Player p = e.getPlayer();
        if (executing.contains(p)) return;

        String world = p.getWorld().getName();
        double pHeight = p.getLocation().getY();

        if (instance.getConfig().contains("worlds." + world)) {
            for (String mode : instance.getConfig().getConfigurationSection("worlds." + world).getKeys(false)) {
                if (pHeight <= Parser.applyInt(p, "worlds." + world + ".floor.executing-height", "0") && mode.equals("floor")) {
                    run(p, world, mode);
                }
                if (pHeight >= Parser.applyInt(p, "worlds." + world + ".roof.executing-height", "1000") && mode.equals("roof")) {
                    run(p, world, mode);
                }
            }
        }
    }

    private void run(Player p, String world, String mode) {
        List<String> commands = instance.getConfig().getStringList("worlds." + world + "."  + mode + ".execute-commands");
        if (commands.isEmpty()) {
            VoidFall.debug("Nothing to execute because execute commands is empty!");
            VoidFall.debug("Path to: worlds." + world + "." + mode + "execute-commands");
            return;
        }

        executing.add(p);
        if (Parser.applyBoolean(p, "worlds." + world + "." + mode + ".random")) {
            execute(p, commands, world);
        } else {
            for (String str : commands) {
                str = Parser.hex(Parser.applyPlaceholders(p, str.replace("%player%", p.getName()).replace("%world%", world)));
                execute(p, str, world);
            }
        }

        Bukkit.getScheduler().runTaskLater(instance, () -> executing.remove(p), Parser.applyInt(p, "worlds." + world + ".floor.repeat-fix", "3") * 20L);
    }

    private void execute(Player p, List<String> list, String world) {
        Random random = new Random();
        String cmd = Parser.hex(Parser.applyPlaceholders(p, list.get(random.nextInt(list.size()))));
        execute(p, cmd, world);
    }

    private void execute(Player p, String str, String world) {
        if (str.startsWith("[CONSOLE] ")) {
            str = str.replace("[CONSOLE] ", "");
            if (str.isEmpty()) {
                VoidFall.debug("There are not enough arguments to run the command in console");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands");
            } else {
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), str.replace("[CONSOLE] ", ""));
            }
        } else if (str.startsWith("[MESSAGE] ")) {
            str = str.replace("[MESSAGE] ", "");
            if (str.isEmpty()) {
                VoidFall.debug("There are not enough arguments to send the message to a player");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands");
            } else {
                p.sendMessage(str);
            }
        } else if (str.startsWith("[BROADCAST] ")) {
            str = str.replace("[BROADCAST] ", "");
            if (str.isEmpty()) {
                VoidFall.debug("There are not enough arguments to send the message to all players");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands");
            } else {
                for (Player pp : Bukkit.getOnlinePlayers()) {
                    pp.sendMessage(str);
                }
            }
        } else if (str.startsWith("[PLAYER] ")) {
            str = str.replace("[PLAYER] ", "");
            if (str.isEmpty()) {
                VoidFall.debug("There are not enough arguments to execute the command from a player");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands");
            } else {
                p.chat("/" + str);
            }
        } else if (str.startsWith("[EFFECT] ")) {
            String[] params = str.replace("[EFFECT] ", "").split(";", 3);
            switch (params.length) {
                case 0:
                    VoidFall.debug("There are not enough arguments to apply effect to player");
                    VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                    break;
                case 1:
                case 2:
                default:
                    PotionEffectType effect = PotionEffectType.BLINDNESS;
                    try {
                        effect = PotionEffectType.getByName(params[0].toUpperCase());
                        if (effect == null) {
                            throw new IllegalArgumentException("Invalid effect type");
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        VoidFall.debug("Potion effect type given for [EFFECT] action by name: " + params[0] + ", is not valid!");
                    }

                    int duration = 60, amplifier = 1;
                    if (params.length >= 2) {
                        try {
                            duration = Integer.parseInt(params[1]) * 20;
                        } catch (final NumberFormatException e) {
                            e.printStackTrace();
                            VoidFall.debug("Duration given for [EFFECT] action: " + Parser.applyPlaceholders(p, params[1]) + ", is not a valid number!");
                        }
                    }

                    if (params.length >= 3) {
                        try {
                            amplifier = Integer.parseInt(params[2]);
                        } catch (final NumberFormatException e) {
                            e.printStackTrace();
                            VoidFall.debug("Amplifier given for [EFFECT] action: " + Parser.applyPlaceholders(p, params[2]) + ", is not a valid number!");
                        }
                    }

                    p.addPotionEffect(new PotionEffect(effect, duration, amplifier));
            }
        } else if (str.startsWith("[TITLE] ")) {
            String[] params = str.replace("[TITLE] ", "").split(";", 5);
            String main, sub = "";
            int fadeIn = 10, stay = 40, fadeOut = 10;
            switch (params.length) {
                case 0:
                    VoidFall.debug("There are not enough arguments to display the title message");
                    VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                case 1:
                    main = params[0];
                    break;
                case 2:
                    main = params[0];
                    sub = params[1];
                    break;
                case 3:
                    main = params[0];
                    sub = params[1];
                    try {
                        fadeIn = Integer.parseInt(params[2]);
                    } catch (final NumberFormatException e) {
                        e.printStackTrace();
                        VoidFall.debug("Value given for [TITLE] action: " + params[2] + ", is not a valid number!");
                    }
                    break;
                case 4:
                    main = params[0];
                    sub = params[1];
                    try {
                        fadeIn = Integer.parseInt(params[2]);
                        stay = Integer.parseInt(params[3]);
                    } catch (final NumberFormatException e) {
                        e.printStackTrace();
                        VoidFall.debug("Values given for [TITLE] action: " + params[2] + " or " + params[3] + ", is not a valid number!");
                    }
                    break;
                default:
                    main = params[0];
                    sub = params[1];
                    try {
                        fadeIn = Integer.parseInt(params[2]);
                        stay = Integer.parseInt(params[3]);
                        fadeOut = Integer.parseInt(params[4]);
                    } catch (final NumberFormatException e) {
                        e.printStackTrace();
                        VoidFall.debug("Values given for [TITLE] action: " + params[2] + ", " + params[3] + " or " + params[4] + ", is not a valid number!");
                    }
                    break;
            }
            p.sendTitle(main, sub, fadeIn, stay, fadeOut);
        } else if (str.startsWith("[ACTIONBAR] ")) {
            str = str.replace("[ACTIONBAR] ", "");
            if (str.isEmpty()) {
                VoidFall.debug("There are not enough arguments to display the message in actionbar");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands");
            } else
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                        str.replace("[ACTIONBAR] ", "")));
        } else if (str.startsWith("[PLAY_SOUND] ")) {
            String[] params = str.replace("[PLAY_SOUND] ", "").split(";", 3);
            switch (params.length) {
                case 0:
                    VoidFall.debug("There are not enough arguments to play the sound");
                    VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                    break;
                case 1:
                case 2:
                default:
                    Sound sound = ENTITY_SHULKER_HURT_CLOSED;
                    try {
                        sound = Sound.valueOf(params[0].toUpperCase());
                    } catch (final IllegalArgumentException exception) {
                        VoidFall.debug("Sound name given for [PLAY_SOUND] action: " + params[0] + ", is not a valid sound!");
                    }
                    float volume = 1, pitch = 1;
                    switch (params.length) {
                        case 2:
                            try {
                                volume = Float.parseFloat(params[1]);
                            } catch (final NumberFormatException exception) {
                                VoidFall.debug("Volume given for [PLAY_SOUND] action: " + params[1] + ", is not a valid number!");
                            }
                            break;
                        case 3:
                            try {
                                pitch = Float.parseFloat(params[2]);
                            } catch (final NumberFormatException exception) {
                                VoidFall.debug("Pitch given for [PLAY_SOUND] action: " + params[2] + ", is not a valid number!");
                            }
                            break;
                    }
                    p.playSound(p.getLocation(), sound, volume, pitch);
            }
        } else {
            VoidFall.debug("&cYou're trying to cause an action that doesn't exist.");
            VoidFall.debug("&cPath to: worlds." + world + ".execute-commands");
            VoidFall.debug("&cAction: " + str);
        }
    }
}

package noslowdwn.voidfall.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.ColorsParser;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

import static noslowdwn.voidfall.VoidFall.instance;
import static org.bukkit.Sound.ENTITY_SHULKER_HURT_CLOSED;

public class OnHeightReach implements Listener {

    private final Set<Player> executing = new HashSet<>();

    @EventHandler
    public void onVoidFall(PlayerMoveEvent e) {
        if (instance.getConfig().getConfigurationSection("worlds").getKeys(false).isEmpty()) return;

        Player p = e.getPlayer();
        if (executing.contains(p)) return;

        String world = p.getWorld().getName();
        double pHeight = p.getLocation().getY();

        if (instance.getConfig().contains("worlds." + world)) {
            for (String mode : instance.getConfig().getConfigurationSection("worlds." + world).getKeys(false)) {
                if (pHeight <= instance.getConfig().getInt("worlds." + world + ".floor.executing-height") && mode.equals("floor"))
                    run(p, world, mode);
                if (pHeight >= instance.getConfig().getInt("worlds." + world + ".roof.executing-height") && mode.equals("roof"))
                    run(p, world, mode);
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
        if (instance.getConfig().getBoolean("worlds." + world + "." + mode + ".random"))
            execute(p, commands, world);
        else
            for (String str : commands) {
                str = str.replace("%player%", p.getName())
                        .replace("%world%", world);
                execute(p, str, world);
            }

        Bukkit.getScheduler().runTaskLater(instance, () -> executing.remove(p), instance.getConfig().getInt("worlds." + world + ".floor.repeat-fix", 3) * 20L);
    }

    private void execute(Player p, List<String> list, String world) {
        Random random = new Random();
        String cmd = list.get(random.nextInt(list.size()));
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
                p.sendMessage(ColorsParser.of(str));
            }
        } else if (str.startsWith("[PLAYER] ")) {
            str = str.replace("[PLAYER] ", "");
            if (str.isEmpty()) {
                VoidFall.debug("There are not enough arguments to execute the command from a player");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands");
            } else {
                p.chat("/" + str);
            }
        } else if (str.startsWith("[TITLE] ")) {
            str = str.replace("[TITLE] ", "");
            if (str.isEmpty()) {
                VoidFall.debug("There are not enough arguments to display the title message");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands");
            } else {
                String[] title = str.split(";", 2);
                String main = "", sub = "";
                switch (title.length) {
                    case 1:
                        main = title[0];
                        sub = "";
                        break;
                    case 2:
                        main = title[0];
                        sub = title[1];
                        break;
                }
                p.sendTitle(ColorsParser.of(main), ColorsParser.of(sub), 10, 40, 10);
            }
        } else if (str.startsWith("[ACTIONBAR] ")) {
            str = str.replace("[ACTIONBAR] ", "");
            if (str.isEmpty()) {
                VoidFall.debug("There are not enough arguments to display the message in actionbar");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands");
            } else
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                        ColorsParser.of(str.replace("[ACTIONBAR] ", ""))));
        } else if (str.startsWith("[PLAY_SOUND] ")) {
            String[] params = str.replace("[PLAY_SOUND] ", "").split(";", 3);
            switch (params.length) {
                case 0:
                    VoidFall.debug("There are not enough arguments to play the sound");
                    VoidFall.debug("Path to: worlds." + world + ".execute-commands");
                    break;
                case 1:
                case 2:
                case 3:
                    Sound sound = ENTITY_SHULKER_HURT_CLOSED;
                    try {
                        sound = Sound.valueOf(params[0].toUpperCase());
                    } catch (final IllegalArgumentException exception) {
                        VoidFall.debug("Sound name given for sound action: " + params[0] + ", is not a valid sound!");
                    }
                    float volume = 1, pitch = 1;
                    switch (params.length) {
                        case 2:
                            try {
                                volume = Float.parseFloat(params[1]);
                            } catch (final NumberFormatException exception) {
                                VoidFall.debug("Volume given for sound action: " + params[1] + ", is not a valid number!");
                            }
                            break;
                        case 3:
                            try {
                                pitch = Float.parseFloat(params[2]);
                            } catch (final NumberFormatException exception) {
                                VoidFall.debug("Pitch given for sound action: " + params[2] + ", is not a valid number!");
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

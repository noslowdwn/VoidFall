package noslowdwn.voidfall.handlers;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.ColorsParser;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static noslowdwn.voidfall.VoidFall.getInstance;
import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.Bukkit.getServer;

public class Actions
{

    public static void executeRandom(Player p, List<String> list, String world)
    {
        Random random = new Random();
        String cmd = list.get(random.nextInt(list.size()));
        execute(p, cmd, world);
    }


    public static void execute(Player p, String str, String world)
    {

        str = str
                .replace("%player%", p.getName())
                .replace("%world%", world)
                .replace("%world_display_name%", getWorldDisplayName(world));

        if (str.startsWith("[CONSOLE] "))
        {
            str = str.replace("[CONSOLE] ", "");
            if (str.isEmpty())
            {
                VoidFall.debug("There are not enough arguments to run the command in console", p, "warn");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            }
            else
            {
                final String fStr = str;
                getScheduler().runTask(getInstance(), () ->
                {
                    getInstance().getServer().dispatchCommand(getInstance().getServer().getConsoleSender(), fStr);
                });
            }
        }

        else if (str.startsWith("[MESSAGE] "))
        {
            str = str.replace("[MESSAGE] ", "");
            if (str.isEmpty())
            {
                VoidFall.debug("There are not enough arguments to send the message to a player", p, "warn");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            }
            else
            {
                p.sendMessage(ColorsParser.of(p, str));
            }
        }

        else if (str.startsWith("[BROADCAST] "))
        {
            str = str.replace("[BROADCAST] ", "");
            if (str.isEmpty())
            {
                VoidFall.debug("There are not enough arguments to send the broadcast message to all", p, "warn");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            }
            else
            {
                String finalStr = str;
                Bukkit.getOnlinePlayers().stream()
                        .filter(Objects::nonNull)
                        .forEach(
                                player -> {
                                    player.sendMessage(ColorsParser.of(p, finalStr));
                                });
            }
        }

        else if (str.startsWith("[PLAYER] "))
        {
            str = str.replace("[PLAYER] ", "");
            if (str.isEmpty())
            {
                VoidFall.debug("There are not enough arguments to execute the command from a player", p, "warn");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            }
            else
            {
                final String fStr = str;
                getScheduler().runTask(getInstance(), () ->
                {
                    p.chat("/" + fStr);
                });
            }
        }

        else if (str.startsWith("[TITLE] "))
        {
            str = str.replace("[TITLE] ", "");
            if (str.isEmpty())
            {
                VoidFall.debug("There are not enough arguments to display the title message", p, "warn");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            }
            else
            {
                String[] title = str.split(";", 2);
                String main = "", sub = "";
                switch (title.length)
                {
                    case 2:
                        sub = title[1];
                    case 1:
                        main = title[0];
                }
                final String fMain = main, fSub = sub;
                getScheduler().runTask(getInstance(), () ->
                {
                    p.sendTitle(ColorsParser.of(p, fMain), ColorsParser.of(p, fSub), 10, 40, 10);
                });
            }
        }

        else if (str.startsWith("[ACTIONBAR] "))
        {
            str = str.replace("[ACTIONBAR] ", "");
            if (str.isEmpty())
            {
                VoidFall.debug("There are not enough arguments to display the message in actionbar", p, "warn");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            }
            else
            {
                final String fStr = str;
                getScheduler().runTask(getInstance(), () ->
                {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorsParser.of(p, fStr)));
                });
            }
        }

        else if (str.startsWith("[PLAY_SOUND] ") || str.startsWith("[PLAY_SOUND_ALL] "))
        {
            boolean all = str.startsWith("[PLAY_SOUND_ALL] ");
            String[] params = str
                    .replace("[PLAY_SOUND] ", "")
                    .replace("[PLAY_SOUND_ALL] ", "")
                    .split(";", 3);

            Sound sound = Sound.ENTITY_SHULKER_HURT_CLOSED;
            float volume = 1f, pitch = 1f;

            switch (params.length)
            {
                case 0:
                    VoidFall.debug("There are not enough arguments to play the sound", p, "warn");
                    VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
                    break;
                case 3:
                    try
                    {
                        pitch = Float.parseFloat(params[2]);
                    }
                    catch (final NumberFormatException exception)
                    {
                        VoidFall.debug("Pitch given for sound action: " + params[2] + ", is not a valid number!", p, "warn");
                    }
                case 2:
                    try
                    {
                        volume = Float.parseFloat(params[1]);
                    }
                    catch (final NumberFormatException exception)
                    {
                        VoidFall.debug("Volume given for sound action: " + params[1] + ", is not a valid number!", p, "warn");
                    }
                case 1:
                    try
                    {
                        sound = Sound.valueOf(params[0].toUpperCase());
                    }
                    catch (final IllegalArgumentException exception)
                    {
                        VoidFall.debug("Failed to play sound! Maybe:", p, "warn");
                        VoidFall.debug("Given sound name: " + params[0] + ", is not a valid?!", p, "warn");
                    }
                default:
                    final Sound fSound = sound;
                    float fVolume = volume, fPitch = pitch;
                    getScheduler().runTask(getInstance(), () ->
                    {
                        if (all)
                        {
                            Bukkit.getOnlinePlayers().stream()
                                    .filter(Objects::nonNull)
                                    .forEach(
                                            player ->
                                            {
                                                p.playSound(p.getLocation(), fSound, fVolume, fPitch);
                                            });
                        }
                        else
                        {
                            p.playSound(p.getLocation(), fSound, fVolume, fPitch);
                        }
                    });
            }
        }

        else if (str.startsWith("[EFFECT] "))
        {
            String[] params = str.replace("[EFFECT] ", "").split(";", 3);

            PotionEffectType effect = PotionEffectType.BLINDNESS;
            int duration = 60, amplifier = 1;

            switch (params.length)
            {
                case 0:
                    VoidFall.debug("There are not enough arguments to apply effect to player", p, "warn");
                    VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
                    break;
                case 3:
                    try
                    {
                        duration = Integer.parseInt(params[1]) * 20;
                    }
                    catch (final NumberFormatException e)
                    {
                        e.printStackTrace();
                        VoidFall.debug("Duration given for [EFFECT] action: " + params[1] + ", is not a valid number!", p, "warn");
                    }
                case 2:
                    try
                    {
                        amplifier = Integer.parseInt(params[2]);
                    }
                    catch (final NumberFormatException e)
                    {
                        e.printStackTrace();
                        VoidFall.debug("Amplifier given for [EFFECT] action: " + params[1] + ", is not a valid number!", p, "warn");
                    }
                case 1:
                    try
                    {
                        effect = PotionEffectType.getByName(params[0].toUpperCase());
                        if (effect == null)
                        {
                            throw new IllegalArgumentException("Invalid effect type");
                        }
                    }
                    catch (IllegalArgumentException e)
                    {
                        e.printStackTrace();
                        VoidFall.debug("Potion effect type given for [EFFECT] action by name: " + params[0] + ", doesn't exist!", p, "warn");
                    }
                default:
                    final PotionEffectType fEffect = effect; int fDuration = duration, fAmplifier = amplifier;
                    getScheduler().runTask(getInstance(), () ->
                    {
                        p.addPotionEffect(new PotionEffect(fEffect, fDuration, fAmplifier));
                    });
            }
        }

        else if (str.startsWith("[TELEPORT] "))
        {
            String[] params = str.replace("[TELEPORT] ", "").split(";", 6);

            World tpWorld = null;
            double x = 0, y = 90, z = 0;
            float yaw = 180f, pitch = 0f;

            switch (params.length)
            {
                case 0:
                    VoidFall.debug("There are not enough arguments to teleport the player!", p, "warn");
                    VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
                    break;
                case 6:
                    try
                    {
                        yaw = Float.parseFloat(params[5]);
                    }
                    catch (final NumberFormatException e)
                    {
                        e.printStackTrace();
                        VoidFall.debug("Given value for [TELEPORT]: " + params[5] + ", is not a valid number!", p, "warn");
                    }
                case 5:
                    try
                    {
                        pitch = Float.parseFloat(params[4]);
                    }
                    catch (final NumberFormatException e)
                    {
                        e.printStackTrace();
                        VoidFall.debug("Given value for [TELEPORT]: " + params[4] + ", is not a valid number!", p, "warn");
                    }
                case 4:
                    try
                    {
                        z = Double.parseDouble(params[3]);
                    }
                    catch (final NumberFormatException e)
                    {
                        e.printStackTrace();
                        VoidFall.debug("Given value for [TELEPORT]: " + params[3] + ", is not a valid number!", p, "warn");
                    }
                case 3:
                    try
                    {
                        y = Double.parseDouble(params[2]);
                    }
                    catch (final NumberFormatException e)
                    {
                        e.printStackTrace();
                        VoidFall.debug("Given value for [TELEPORT]: " + params[2] + ", is not a valid number!", p, "warn");
                    }
                case 2:
                    try
                    {
                        x = Double.parseDouble(params[1]);
                    }
                    catch (final NumberFormatException e)
                    {
                        e.printStackTrace();
                        VoidFall.debug("Given value for [TELEPORT]: " + params[1] + ", is not a valid number!", p, "warn");
                    }
                case 1:
                    tpWorld = getServer().getWorld(params[0]);
                default:

                    if (tpWorld == null)
                    {
                        VoidFall.debug("The world given for [TELEPORT]: " + params[0] + ", doesn't exist!", p, "warn");
                        tpWorld = getServer().getWorlds().get(0);
                    }

                    Location location = tpWorld.getSpawnLocation();
                    location.setX(x);
                    location.setY(y);
                    location.setZ(z);
                    location.setPitch(pitch);
                    location.setYaw(yaw);
                    getScheduler().runTask(getInstance(), () ->
                    {
                        p.teleport(location);
                    });
            }
        }

        else if (str.startsWith("[GAMEMODE] "))
        {
            str = str.replace("[GAMEMODE] ", "");
            GameMode gm;
            if (str.isEmpty())
            {
                VoidFall.debug("There are not enough arguments to set gamemode to the player", p, "warn");
                VoidFall.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            }
            else
            {
                if (str.equalsIgnoreCase("1") || str.equalsIgnoreCase("creative"))
                {
                    gm = GameMode.CREATIVE;
                }
                else if (str.equalsIgnoreCase("2") || str.equalsIgnoreCase("adventure"))
                {
                    gm = GameMode.ADVENTURE;
                }
                else if (str.equalsIgnoreCase("3") || str.equalsIgnoreCase("spectator"))
                {
                    gm = GameMode.SPECTATOR;
                }
                else if (str.equalsIgnoreCase("0") || str.equalsIgnoreCase("survival"))
                {
                    gm = GameMode.SURVIVAL;
                }
                else
                {
                    VoidFall.debug("The gamemode given for [GAMEMODE]: " + str + ", doesn't exist or not valid!", p, "warn");
                    VoidFall.debug("&cPath to: worlds." + world + ".execute-commands", p, "warn");
                    return;
                }
                getScheduler().runTask(getInstance(), () ->
                {
                    p.setGameMode(gm);
                });
            }
        }

        else
        {
            VoidFall.debug("&cYou're trying to cause an action that doesn't exist.", p, "warn");
            VoidFall.debug("&cPath to: worlds." + world + ".execute-commands", p, "warn");
            VoidFall.debug("&cAction: " + str, p, "warn");
        }
    }

    private static String getWorldDisplayName(String worldName)
    {
        if (getInstance().getConfig().getConfigurationSection("messages.worlds-display-names").contains(worldName))
        {
            return getInstance().getConfig().getString("messages.worlds-display-names." + worldName);
        }
        else
        {
            return worldName;
        }
    }
}
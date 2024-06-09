package noslowdwn.voidfall.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import noslowdwn.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static noslowdwn.voidfall.VoidFall.instance;

public class Parser {

    public static String hex(String message) {
        if (message == null) {
            VoidFall.debug("[VoidFall] Error message parsing!");
            VoidFall.debug("[VoidFall] Please check your messages.yml to find error!");
            VoidFall.debug("[VoidFall] You can also check syntax on https://yamlchecker.com/!");
            VoidFall.debug("[VoidFall] Or just delete 'messages.yml' and reload plugin!");
            message = "&c[VoidFall] Error message parsing! Please contact administrator or check console!";
        }

        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        int subVersion = Integer.parseInt(version.replace("1_", "").replaceAll("_R\\d", "").replace("v", ""));
        if (subVersion >= 16) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                StringBuilder replacement = new StringBuilder("x");
                for (char c : color.substring(1).toCharArray()) {
                    replacement.append('\u00A7').append(c);
                }
                message = message.replace(color, replacement.toString());
                matcher = pattern.matcher(message);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String applyPlaceholders(Player player, String text) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, text);
        } else {
            return text;
        }
    }

    public static Boolean applyBoolean(Player p, String way, String def) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return applyPlaceholders(p, instance.getConfig().getString(way).replace("%player%", p.getName()).replace("%world%", p.getWorld().getName()))
                    .equalsIgnoreCase("true");
        } else {
            try {
                return instance.getConfig().getBoolean(way, Boolean.parseBoolean(def));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static int applyInt(Player p, String way, String def) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                return Integer.parseInt(applyPlaceholders(p, instance.getConfig().getString(way, def)
                        .replace("%player%", p.getName())
                        .replace("%world%", p.getWorld().getName())));
            } catch (final NumberFormatException e) {
                e.printStackTrace();
                return 1;
            }
        } else {
            try {
                return instance.getConfig().getInt(way, Integer.parseInt(def));
            } catch (final NumberFormatException e) {
                e.printStackTrace();
                return 1;
            }
        }
    }

//    public static double applyDouble(Player p, String way) {
//        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
//            try {
//                return Double.parseDouble(applyPlaceholders(p, instance.getConfig().getString(way)
//                        .replace("%player%", p.getName())
//                        .replace("%world%", p.getWorld().getName())));
//            } catch (final NumberFormatException e) {
//                e.printStackTrace();
//                return 1;
//            }
//        } else {
//            return instance.getConfig().getDouble(way);
//        }
//    }
}

package noslowdwn.voidfall.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import noslowdwn.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static noslowdwn.voidfall.VoidFall.getSubversion;

public class ColorsParser
{
    private static final Pattern pattern = Pattern.compile("&#([a-fA-F\\d]{6})");

    public static String of(CommandSender sender, String message)
    {
        if (message == null)
        {
            VoidFall.debug("[VoidFall] Error message parsing!", null, "warn");
            VoidFall.debug("[VoidFall] Please check your messages.yml to find error!", null, "warn");
            VoidFall.debug("[VoidFall] You can also check syntax on https://yamlchecker.com/!", null, "warn");
            VoidFall.debug("[VoidFall] Or just delete 'messages.yml' and reload plugin!", null, "warn");
            message = "&c[VoidFall] Error message parsing! Please contact administrator or check console!";
        }

        if (getSubversion() >= 16)
        {
            // Took it from here https://github.com/Overwrite987/OverwriteAPI/blob/main/src/main/java/ru/overwrite/api/commons/StringUtils.java
            // Thx to Overwrite
            Matcher matcher = pattern.matcher(message);
            StringBuffer builder = new StringBuffer(message.length() + 32);
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(builder,
                        pattern + "x" +
                                pattern + group.charAt(0) +
                                pattern + group.charAt(1) +
                                pattern + group.charAt(2) +
                                pattern + group.charAt(3) +
                                pattern + group.charAt(4) +
                                pattern + group.charAt(5));
            }
            message = matcher.appendTail(builder).toString();
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && sender instanceof Player)
        {
            message = PlaceholderAPI.setPlaceholders((Player) sender, message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
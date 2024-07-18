package noslowdwn.voidfall.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import noslowdwn.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorsParser
{

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    private static final int subVersion = Integer.parseInt(version.replace("1_", "").replaceAll("_R\\d", "").replace("v", ""));

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

        if (subVersion >= 16)
        {
            Matcher matcher = pattern.matcher(message);

            while (matcher.find())
            {
                String color = message.substring(matcher.start(), matcher.end());
                StringBuilder replacement = new StringBuilder("x");

                for (char c : color.substring(1).toCharArray())
                    replacement.append('\u00A7').append(c);

                message = message.replace(color, replacement.toString());
                matcher = pattern.matcher(message);
            }
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && sender instanceof Player)
        {
            message = PlaceholderAPI.setPlaceholders((Player) sender, message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
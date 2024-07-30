package noslowdwn.voidfall.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import noslowdwn.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class ColorsParser
{

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F\\d]{6})");

    private ColorsParser()
    {
        throw new ExceptionInInitializerError("This class may not be initialized!");
    }

    public static String of(CommandSender sender, @NotNull String message) {

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && sender instanceof Player)
        {
            message = PlaceholderAPI.setPlaceholders((Player) sender, message);
        }

        if (VoidFall.getSubVersion() >= 16) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer builder = new StringBuffer(message.length() + 32);
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(builder,
                        COLOR_CHAR + "x" +
                                COLOR_CHAR + group.charAt(0) +
                                COLOR_CHAR + group.charAt(1) +
                                COLOR_CHAR + group.charAt(2) +
                                COLOR_CHAR + group.charAt(3) +
                                COLOR_CHAR + group.charAt(4) +
                                COLOR_CHAR + group.charAt(5));
            }
            message = matcher.appendTail(builder).toString();
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
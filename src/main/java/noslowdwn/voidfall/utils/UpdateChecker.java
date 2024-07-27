package noslowdwn.voidfall.utils;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static noslowdwn.voidfall.VoidFall.getInstance;

public class UpdateChecker implements Listener
{

    private static final String VERSION_URL = "https://raw.githubusercontent.com/noslowdwn/VoidFall/master/version";
    private static Boolean new_version = false;
    private static String latestVersion, downloadLink;

    public static void checkVersion()
    {
        if (getInstance().getConfig().getBoolean("check-updates", false))
        {
            Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&6[VoidFall] Checking for updates..."));

            try
            {
                URL url = new URL(VERSION_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null)
                {
                    content.append(line);
                }
                in.close();
                connection.disconnect();

                String[] versionInfo = content.toString().split("->");
                if (versionInfo.length != 2)
                {
                    Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&6[VoidFall] Got version from server is invalid!"));
                    return;
                }

                latestVersion = versionInfo[0].trim();
                downloadLink = versionInfo[1].trim();

                String[] currVersion = getInstance().getDescription().getVersion().split("\\.");
                String[] newVersion = latestVersion.split("\\.");
                for (int i = 0; i < newVersion.length; i++)
                {
                    int currVer = Integer.parseInt(currVersion[i]);
                    int newVer = Integer.parseInt(newVersion[i]);

                    if (currVer < newVer)
                    {
                        new_version = true;
                    }
                }

                if (!new_version)
                {
                    Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&c[VoidFall] No updates were found!"));
                }
                else
                {
                    Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&f============ VoidFall ============"));
                    Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&fCurrent version: &7" + getInstance().getDescription().getVersion()));
                    Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&fNew version: &a" + latestVersion));
                    Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&fDownload link: &7" + downloadLink));
                    Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&f=================================="));
                }
            }
            catch (Exception e)
            {
                VoidFall.debug(ColorsParser.of(null, "&c[VoidFall] Failed to check for updates: " + e.getMessage()), null, "warn");
            }
        }
    }

    @EventHandler
    private void onJoinNotification(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), () ->
        {
            if ((p.isOp() || p.hasPermission("voidfall.updates")) && new_version)
            {
                p.sendMessage("");
                p.sendMessage(ColorsParser.of(null, "&6[VoidFall] &aWas found an update!"));
                p.sendMessage(ColorsParser.of(null, "&fCurrent version: &7" + getInstance().getDescription().getVersion()));
                p.sendMessage(ColorsParser.of(null, "&fNew version: &a" + latestVersion));
                p.sendMessage(ColorsParser.of(null, "&fDownload link: &7" + downloadLink));
                p.sendMessage("");
            }
        }, 100L);
    }
}
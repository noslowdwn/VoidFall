package noslowdwn.voidfall.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

import static noslowdwn.voidfall.VoidFall.getInstance;

public class Config
{

    private static File file;

    public static void load()
    {
        file = new File(getInstance().getDataFolder(), "config.yml");
        if (!file.exists())
            getInstance().saveResource("config.yml", false);

        YamlConfiguration config = new YamlConfiguration();

        try
        {
            config.load(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void checkVersion()
    {
        double version = 1.2;
        if (getInstance().getConfig().getDouble("config-version") != version || !getInstance().getConfig().getKeys(false).contains("config-version"))
        {
            int i = 1;
            File backupFile;
            do
            {
                backupFile = new File(getInstance().getDataFolder(), "config_backup_" + i + ".yml");
                i++;
            } while (backupFile.exists());

            if (file.renameTo(backupFile))
            {
                Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&cYour configuration file is old and was renamed to: &7" + backupFile.getName()));
                load();
            }
            else
            {
                Bukkit.getConsoleSender().sendMessage(ColorsParser.of(null, "&cYour configuration file is old, but &ncreate new is not possible&c."));
            }
        }
    }
}
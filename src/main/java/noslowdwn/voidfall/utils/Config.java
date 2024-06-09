package noslowdwn.voidfall.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

import static noslowdwn.voidfall.VoidFall.instance;

public class Config {

    public static void load() {
        File file = new File(instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            instance.saveResource("config.yml", false);
        }

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkVersion() {
        double version = 1.2;
        if (instance.getConfig().getDouble("config-version") != version || !instance.getConfig().getKeys(false).contains("config-version")) {

            File backupFolder = new File(instance.getDataFolder(), "backups");
            if (!backupFolder.exists() && !backupFolder.mkdirs()) {
                Bukkit.getConsoleSender().sendMessage(Parser.hex("&c[VoidFall] Error! Failed to create backups folder!"));
                return;
            }

            int backupNumber = instance.getDataFolder().listFiles().length;
            File backupFile = new File(backupFolder, ("config_backup_" + backupNumber + ".yml"));
            try {
                new YamlConfiguration().save(backupFile);
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(Parser.hex("&c[VoidFall] Error! Failed to save config_backup_" + backupNumber + ".yml to " + backupFile.getPath()));
            }
        }
    }
}

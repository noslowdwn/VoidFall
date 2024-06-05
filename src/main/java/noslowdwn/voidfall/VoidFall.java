package noslowdwn.voidfall;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class VoidFall extends JavaPlugin {

    public static VoidFall instance;

    @Override
    public void onEnable() {
        instance = this;
        load();

        this.getCommand("voidfall").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player && !sender.hasPermission("voidfall.reload")) {
                sender.sendMessage(ColorsUtil.of(getConfig().getString("messages.no-permission")));
                return true;
            }

            reloadConfig();
            sender.sendMessage(ColorsUtil.of(getConfig().getString("messages.reload-message")));

            return true;
        });

        this.getServer().getPluginManager().registerEvents(new FallEvent(), this);
    }

    private void load() {
        File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists())
            this.saveResource("config.yml", false);

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void debug(String error) {
        if (instance.getConfig().getBoolean("debug-mode", false))
            Bukkit.getConsoleSender().sendMessage(ColorsUtil.of(error));
    }
}

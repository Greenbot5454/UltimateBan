package ir.Greenbot.ultimateban;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class UltimateBan extends JavaPlugin {

    private BanManager banManager;

    @Override
    public void onEnable() {
        // ساخت config.yml پیش‌فرض اگه نبود
        saveDefaultConfig();

        // ساخت BanManager
        this.banManager = new BanManager(this);

        // ثبت دستورات
        getCommand("ban").setExecutor(new BanCommand(this, banManager));
        getCommand("tempban").setExecutor(new TempBanCommand(this, banManager));
        getCommand("unban").setExecutor(new UnbanCommand(this, banManager));
        getCommand("kick").setExecutor(new KickCommand(this, banManager));
        getCommand("banlist").setExecutor(new BanListCommand(this, banManager));

        // ثبت listener برای بلاک کردن بازیکن بن‌شده
        getServer().getPluginManager().registerEvents(new BanListener(this, banManager), this);

        getLogger().info("UltimateBan enabled!");
    }

    @Override
    public void onDisable() {
        if (banManager != null) {
            banManager.save();
        }
        getLogger().info("UltimateBan disabled!");
    }

    public BanManager getBanManager() {
        return banManager;
    }

    // گرفتن یه پیام از config و جایگزینی placeholderها
    public String getMessage(String key, String... replacements) {
        String msg = getConfig().getString("messages." + key, "&cMessage not found: " + key);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("messages.prefix", "&8[&c&lUltimateBan&r&8] &r"));
    }
}

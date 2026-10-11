package ir.Greenbot.ultimateban;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class BanListener implements Listener {

    private final UltimateBan plugin;
    private final BanManager banManager;

    public BanListener(UltimateBan plugin, BanManager banManager) {
        this.plugin = plugin;
        this.banManager = banManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();

        if (!banManager.isBanned(name)) return;

        BanData data = banManager.getBan(name);
        if (data == null) return;

        String msg = plugin.getConfig().getString("messages.join-denied",
                "&cYou are banned!\n&7Reason: &f{reason}\n&7By: &c{staff}");

        msg = msg.replace("{reason}", data.getReason())
                .replace("{staff}", data.getStaff())
                .replace("{player}", name);

        if (!data.isPermanent()) {
            long remaining = data.getExpireTime() - System.currentTimeMillis();
            msg += "\n&7Expires in: &e" + BanManager.formatTime(remaining);
        }

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                ChatColor.translateAlternateColorCodes('&', msg));
    }
}

package ir.Greenbot.ultimateban;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BanListCommand implements CommandExecutor {

    private final UltimateBan plugin;
    private final BanManager banManager;

    public BanListCommand(UltimateBan plugin, BanManager banManager) {
        this.plugin = plugin;
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("ultimateban.banlist")) {
            sender.sendMessage(plugin.getPrefix() + "§cYou don't have permission!");
            return true;
        }

        List<BanData> bans = banManager.getAllBans();

        sender.sendMessage(plugin.getMessage("banlist-header"));

        if (bans.isEmpty()) {
            sender.sendMessage(plugin.getMessage("banlist-empty"));
        } else {
            for (BanData data : bans) {
                String entry = plugin.getMessage("banlist-entry",
                        "{player}", data.getPlayer(),
                        "{reason}", data.getReason(),
                        "{staff}", data.getStaff());

                // اضافه کردن زمان باقی‌مانده اگه موقته
                if (!data.isPermanent()) {
                    long remaining = data.getExpireTime() - System.currentTimeMillis();
                    entry += " §7(" + BanManager.formatTime(remaining) + " left)";
                }

                sender.sendMessage(entry);
            }
        }

        sender.sendMessage(plugin.getMessage("banlist-footer"));
        return true;
    }
}

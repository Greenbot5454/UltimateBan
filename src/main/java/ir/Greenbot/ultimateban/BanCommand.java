package ir.Greenbot.ultimateban;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BanCommand implements CommandExecutor {

    private final UltimateBan plugin;
    private final BanManager banManager;

    public BanCommand(UltimateBan plugin, BanManager banManager) {
        this.plugin = plugin;
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("ultimateban.ban")) {
            sender.sendMessage(plugin.getPrefix() + "§cYou don't have permission!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessage("ban-usage"));
            return true;
        }

        String target = args[0];

        if (banManager.isBanned(target)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessage("ban-already", "{player}", target));
            return true;
        }

        String reason = args.length > 1
                ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length))
                : plugin.getConfig().getString("default-reason", "No reason specified");

        String staff = sender.getName();

        // بن دائم (expireTime = -1)
        banManager.ban(target, reason, staff, -1);

        // کیک کردن اگه آنلاینه
        Player online = Bukkit.getPlayerExact(target);
        if (online != null) {
            String kickScreen = plugin.getConfig().getString("messages.kick-screen",
                    "&cYou have been banned!\n&7Reason: &f{reason}\n&7By: &c{staff}");
            kickScreen = kickScreen.replace("{reason}", reason).replace("{staff}", staff);
            online.kickPlayer(org.bukkit.ChatColor.translateAlternateColorCodes('&', kickScreen));
        }

        // پیام به ادمین
        sender.sendMessage(plugin.getPrefix() + plugin.getMessage("ban-success",
                "{player}", target, "{reason}", reason, "{staff}", staff));

        // برودکست
        if (plugin.getConfig().getBoolean("broadcast-ban", true)) {
            String broadcast = plugin.getMessage("ban-broadcast",
                    "{player}", target, "{reason}", reason, "{staff}", staff);
            Bukkit.broadcastMessage(plugin.getPrefix() + broadcast);
        }

        return true;
    }
}

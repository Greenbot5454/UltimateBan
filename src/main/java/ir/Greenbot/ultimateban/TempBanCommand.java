package ir.Greenbot.ultimateban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TempBanCommand implements CommandExecutor {

    private final UltimateBan plugin;
    private final BanManager banManager;

    public TempBanCommand(UltimateBan plugin, BanManager banManager) {
        this.plugin = plugin;
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("ultimateban.tempban")) {
            sender.sendMessage(plugin.getPrefix() + "§cYou don't have permission!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessage("tempban-usage"));
            return true;
        }

        String target = args[0];
        String timeStr = args[1];

        long duration = BanManager.parseTime(timeStr);
        if (duration <= 0) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessage("tempban-invalid-time"));
            return true;
        }

        if (banManager.isBanned(target)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessage("ban-already", "{player}", target));
            return true;
        }

        String reason = args.length > 2
                ? String.join(" ", Arrays.copyOfRange(args, 2, args.length))
                : plugin.getConfig().getString("default-reason", "No reason specified");

        String staff = sender.getName();
        long expireTime = System.currentTimeMillis() + duration;

        banManager.ban(target, reason, staff, expireTime);

        // کیک کردن
        Player online = Bukkit.getPlayerExact(target);
        if (online != null) {
            String kickScreen = plugin.getConfig().getString("messages.kick-screen",
                    "&cYou have been banned!\n&7Reason: &f{reason}\n&7By: &c{staff}");
            kickScreen = kickScreen.replace("{reason}", reason).replace("{staff}", staff)
                    + "\n&7Expires in: &e" + BanManager.formatTime(duration);
            online.kickPlayer(ChatColor.translateAlternateColorCodes('&', kickScreen));
        }

        String formattedTime = BanManager.formatTime(duration);

        sender.sendMessage(plugin.getPrefix() + plugin.getMessage("tempban-success",
                "{player}", target, "{reason}", reason, "{staff}", staff, "{time}", formattedTime));

        if (plugin.getConfig().getBoolean("broadcast-ban", true)) {
            String broadcast = plugin.getMessage("tempban-broadcast",
                    "{player}", target, "{reason}", reason, "{staff}", staff, "{time}", formattedTime);
            Bukkit.broadcastMessage(plugin.getPrefix() + broadcast);
        }

        return true;
    }
}

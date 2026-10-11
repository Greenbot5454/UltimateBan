package ir.Greenbot.ultimateban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class KickCommand implements CommandExecutor {

    private final UltimateBan plugin;
    private final BanManager banManager;

    public KickCommand(UltimateBan plugin, BanManager banManager) {
        this.plugin = plugin;
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("ultimateban.kick")) {
            sender.sendMessage(plugin.getPrefix() + "§cYou don't have permission!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessage("kick-usage"));
            return true;
        }

        String target = args[0];
        Player online = Bukkit.getPlayerExact(target);

        if (online == null) {
            sender.sendMessage(plugin.getPrefix() + "§cPlayer '§e" + target + "§c' is not online!");
            return true;
        }

        String reason = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : plugin.getConfig().getString("default-reason", "No reason specified");

        String staff = sender.getName();

        String kickScreen = plugin.getConfig().getString("messages.kick-screen",
                "&cYou have been kicked!\n&7Reason: &f{reason}\n&7By: &c{staff}");
        kickScreen = kickScreen.replace("{reason}", reason).replace("{staff}", staff);
        online.kickPlayer(ChatColor.translateAlternateColorCodes('&', kickScreen));

        sender.sendMessage(plugin.getPrefix() + plugin.getMessage("kick-success",
                "{player}", target, "{reason}", reason, "{staff}", staff));

        if (plugin.getConfig().getBoolean("broadcast-kick", true)) {
            String broadcast = plugin.getMessage("kick-broadcast",
                    "{player}", target, "{reason}", reason, "{staff}", staff);
            Bukkit.broadcastMessage(plugin.getPrefix() + broadcast);
        }

        return true;
    }
}

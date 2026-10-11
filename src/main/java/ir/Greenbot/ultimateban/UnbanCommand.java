package ir.Greenbot.ultimateban;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UnbanCommand implements CommandExecutor {

    private final UltimateBan plugin;
    private final BanManager banManager;

    public UnbanCommand(UltimateBan plugin, BanManager banManager) {
        this.plugin = plugin;
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("ultimateban.unban")) {
            sender.sendMessage(plugin.getPrefix() + "§cYou don't have permission!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessage("unban-usage"));
            return true;
        }

        String target = args[0];

        if (!banManager.isBanned(target)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessage("unban-not-banned", "{player}", target));
            return true;
        }

        banManager.unban(target);

        String staff = sender.getName();

        sender.sendMessage(plugin.getPrefix() + plugin.getMessage("unban-success",
                "{player}", target, "{staff}", staff));

        if (plugin.getConfig().getBoolean("broadcast-unban", true)) {
            String broadcast = plugin.getMessage("unban-broadcast",
                    "{player}", target, "{staff}", staff);
            Bukkit.broadcastMessage(plugin.getPrefix() + broadcast);
        }

        return true;
    }
}

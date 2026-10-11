package ir.Greenbot.ultimateban;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanManager {

    private final UltimateBan plugin;
    private final File bansFile;
    private final FileConfiguration bansConfig;

    // نام بازیکن (کوچیک) -> BanData
    private final Map<String, BanData> bans = new HashMap<>();

    public BanManager(UltimateBan plugin) {
        this.plugin = plugin;
        this.bansFile = new File(plugin.getDataFolder(), "bans.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!bansFile.exists()) {
            try {
                bansFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create bans.yml: " + e.getMessage());
            }
        }

        this.bansConfig = YamlConfiguration.loadConfiguration(bansFile);
        load();
    }

    // خواندن بن‌ها از فایل
    public void load() {
        bans.clear();
        ConfigurationSection section = bansConfig.getConfigurationSection("bans");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection s = section.getConfigurationSection(key);
            if (s == null) continue;

            String reason = s.getString("reason", "No reason");
            String staff = s.getString("staff", "Console");
            long banTime = s.getLong("banTime", System.currentTimeMillis());
            long expireTime = s.getLong("expireTime", -1L);
            boolean ipBan = s.getBoolean("ipBan", false);

            BanData data = new BanData(key, reason, staff, banTime, expireTime, ipBan);
            if (!data.isExpired()) {
                bans.put(key.toLowerCase(), data);
            }
        }
    }

    // نوشتن بن‌ها به فایل
    public void save() {
        bansConfig.set("bans", null);
        for (Map.Entry<String, BanData> entry : bans.entrySet()) {
            BanData d = entry.getValue();
            String path = "bans." + entry.getKey();
            bansConfig.set(path + ".reason", d.getReason());
            bansConfig.set(path + ".staff", d.getStaff());
            bansConfig.set(path + ".banTime", d.getBanTime());
            bansConfig.set(path + ".expireTime", d.getExpireTime());
            bansConfig.set(path + ".ipBan", d.isIpBan());
        }
        try {
            bansConfig.save(bansFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save bans.yml: " + e.getMessage());
        }
    }

    public boolean isBanned(String player) {
        BanData d = bans.get(player.toLowerCase());
        if (d == null) return false;
        if (d.isExpired()) {
            bans.remove(player.toLowerCase());
            save();
            return false;
        }
        return true;
    }

    public BanData getBan(String player) {
        BanData d = bans.get(player.toLowerCase());
        if (d == null) return null;
        if (d.isExpired()) {
            bans.remove(player.toLowerCase());
            save();
            return null;
        }
        return d;
    }

    public void ban(String player, String reason, String staff, long expireTime) {
        BanData data = new BanData(player, reason, staff, System.currentTimeMillis(), expireTime, false);
        bans.put(player.toLowerCase(), data);
        save();
    }

    public boolean unban(String player) {
        if (bans.remove(player.toLowerCase()) != null) {
            save();
            return true;
        }
        return false;
    }

    public List<BanData> getAllBans() {
        return new ArrayList<>(bans.values());
    }

    // تبدیل زمان مثل "1d" یا "2h30m" به میلی‌ثانیه
    public static long parseTime(String input) {
        if (input == null || input.isEmpty()) return -1;

        long total = 0;
        StringBuilder num = new StringBuilder();
        for (char c : input.toLowerCase().toCharArray()) {
            if (Character.isDigit(c)) {
                num.append(c);
            } else {
                if (num.length() == 0) return -1;
                long value;
                try {
                    value = Long.parseLong(num.toString());
                } catch (NumberFormatException e) {
                    return -1;
                }
                num.setLength(0);

                switch (c) {
                    case 's' -> total += value * 1000L;
                    case 'm' -> total += value * 60_000L;
                    case 'h' -> total += value * 3_600_000L;
                    case 'd' -> total += value * 86_400_000L;
                    case 'w' -> total += value * 604_800_000L;
                    default -> {
                        return -1;
                    }
                }
            }
        }
        if (num.length() > 0) return -1;
        return total == 0 ? -1 : total;
    }

    // تبدیل میلی‌ثانیه به رشته‌ی خوانا
    public static String formatTime(long millis) {
        if (millis <= 0) return "permanent";
        long seconds = millis / 1000;
        long days = seconds / 86400;
        seconds %= 86400;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s");
        return sb.toString().trim();
    }
}

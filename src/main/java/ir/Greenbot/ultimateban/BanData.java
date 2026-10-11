package ir.Greenbot.ultimateban;

public class BanData {

    private final String player;
    private final String reason;
    private final String staff;
    private final long banTime;
    private final long expireTime; // -1 = permanent
    private final boolean ipBan;

    public BanData(String player, String reason, String staff, long banTime, long expireTime, boolean ipBan) {
        this.player = player;
        this.reason = reason;
        this.staff = staff;
        this.banTime = banTime;
        this.expireTime = expireTime;
        this.ipBan = ipBan;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public String getStaff() {
        return staff;
    }

    public long getBanTime() {
        return banTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public boolean isIpBan() {
        return ipBan;
    }

    public boolean isPermanent() {
        return expireTime == -1;
    }

    public boolean isExpired() {
        return !isPermanent() && System.currentTimeMillis() > expireTime;
    }
}

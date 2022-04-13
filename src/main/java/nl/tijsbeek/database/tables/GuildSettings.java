package nl.tijsbeek.database.tables;

import org.jetbrains.annotations.Contract;

public class GuildSettings {
    private final long guildId;

    private long reportChannelId;
    private long auditLogChannelId;

    @Contract(pure = true)
    public GuildSettings() {
        this.guildId = 0L;
    }

    @Contract(pure = true)
    public GuildSettings(final long guildId) {
        this.guildId = guildId;
    }

    public long getGuildId() {
        return guildId;
    }

    public long getReportChannelId() {
        return reportChannelId;
    }

    public void setReportChannelId(final long reportChannelId) {
        this.reportChannelId = reportChannelId;
    }
}

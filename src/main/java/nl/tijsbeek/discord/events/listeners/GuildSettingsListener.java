package nl.tijsbeek.discord.events.listeners;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import nl.tijsbeek.database.databases.Databases;
import nl.tijsbeek.database.databases.GuildSettingsDatabase;
import nl.tijsbeek.database.tables.GuildSettings;
import nl.tijsbeek.discord.events.AbstractEventListener;
import org.jetbrains.annotations.NotNull;

public class GuildSettingsListener extends AbstractEventListener {

    private final GuildSettingsDatabase database;

    public GuildSettingsListener(@NotNull final Databases databases) {
        this.database = databases.getGuildSettingsDatabase();
    }

    @Override
    public void onGuildReady(@NotNull final GuildReadyEvent event) {
        database.insert(new GuildSettings(event.getGuild().getIdLong()));
    }

    @Override
    public void onGuildJoin(@NotNull final GuildJoinEvent event) {
        database.insert(new GuildSettings(event.getGuild().getIdLong()));
    }
}

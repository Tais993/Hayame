package nl.tijsbeek.prometheus;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import nl.tijsbeek.config.Config;
import nl.tijsbeek.discord.system.CommandHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricsHandler implements EventListener {
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public MetricsHandler(@NotNull final CommandHandler commandHandler, @NotNull final Config config) {

        DefaultExports.initialize();
        try {
            new HTTPServer(config.getPrometheusPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduledExecutor.schedule(() -> {
            Metrics.BUSY_THREADS.set(commandHandler.getPool().getActiveThreadCount());
            Metrics.TOTAL_THREADS.set(commandHandler.getPool().getPoolSize());
        }, 15, TimeUnit.SECONDS);
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        //noinspection OverlyComplexBooleanExpression
        if (event instanceof GuildJoinEvent ||
                event instanceof GuildLeaveEvent ||
                event instanceof GuildAvailableEvent ||
                event instanceof GuildUnavailableEvent ||
                event instanceof ReadyEvent) {

            Metrics.GUILD_COUNT.set(event.getJDA().getGuildCache().size());
        }
    }
}
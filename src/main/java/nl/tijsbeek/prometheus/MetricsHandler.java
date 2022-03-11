package nl.tijsbeek.prometheus;

import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import nl.tijsbeek.config.Config;
import nl.tijsbeek.discord.system.CommandHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MetricsHandler implements EventListener {
    private JDA jda;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public MetricsHandler(@NotNull final CommandHandler commandHandler, @NotNull final Config config) {

        DefaultExports.initialize();
        try {
            new HTTPServer(config.getPrometheusBotPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduledExecutor.schedule(() -> {
            ThreadPoolExecutor executor = commandHandler.getExecutor();
            Metrics.RUNNING_COMMAND_THREADS.set(executor.getActiveCount());
            Metrics.TOTAL_COMMAND_THREADS.set(executor.getMaximumPoolSize());
            Metrics.IDLE_COMMAND_THREADS.set(executor.getCorePoolSize() - executor.getActiveCount());
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (jda == null) {
            jda = event.getJDA();
        }

        if (event instanceof ReadyEvent) {
            scheduledExecutor.schedule(() -> {
                int totalUserCount = jda.getGuildCache().stream()
                        .mapToInt(Guild::getMemberCount)
                        .sum();

                Metrics.USER_COUNT.set(totalUserCount);
            }, 60, TimeUnit.SECONDS);
        }

        //noinspection OverlyComplexBooleanExpression
        if (event instanceof GuildJoinEvent ||
                event instanceof GuildLeaveEvent ||
                event instanceof GuildAvailableEvent ||
                event instanceof GuildUnavailableEvent ||
                event instanceof GuildReadyEvent ||
                event instanceof ReadyEvent) {

            Metrics.GUILD_COUNT.set(event.getJDA().getGuildCache().size());
        }
    }

    public static void handleCommandCounter(@NotNull final CommandInteractionPayload event) {
        switch (event.getCommandType()) {
            case USER -> {
                Metrics.Commands.GENERIC_COMMANDS.labels("user").inc();
                Metrics.Commands.USER_CONTEXTCOMMANDS.labels(event.getName()).inc();
            }
            case MESSAGE -> {
                Metrics.Commands.GENERIC_COMMANDS.labels("message").inc();
                Metrics.Commands.MESSAGE_CONTEXTCOMMANDS.labels(event.getName()).inc();
            }
            case SLASH -> {
                Metrics.Commands.GENERIC_COMMANDS.labels("slash").inc();
                Metrics.Commands.SLASHCOMMANDS.labels(event.getName()).inc();
            }
        }
    }

    public static HistogramTimerDouble getHandlingCommandTimer(@NotNull final CommandInteractionPayload event) {
        return switch (event.getCommandType()) {
            case USER -> {
                Histogram.Timer genericTimer = Metrics.Commands.GENERIC_COMMAND_HANDLING_DURATION.labels("user").startTimer();
                Histogram.Timer commandTimer = Metrics.Commands.USER_CONTEXTCOMMANDS_HANDLING_DURATION.labels(event.getName()).startTimer();

                yield new HistogramTimerDouble(genericTimer, commandTimer);
            }
            case MESSAGE -> {
                Histogram.Timer genericTimer = Metrics.Commands.GENERIC_COMMAND_HANDLING_DURATION.labels("message").startTimer();
                Histogram.Timer commandTimer = Metrics.Commands.MESSAGE_CONTEXTCOMMANDS_HANDLING_DURATION.labels(event.getName()).startTimer();

                yield new HistogramTimerDouble(genericTimer, commandTimer);
            }
            case SLASH -> {
                Histogram.Timer genericTimer = Metrics.Commands.GENERIC_COMMAND_HANDLING_DURATION.labels("slash").startTimer();
                Histogram.Timer commandTimer = Metrics.Commands.SLASHCOMMAND_HANDLING_DURATION.labels(event.getName()).startTimer();

                yield new HistogramTimerDouble(genericTimer, commandTimer);
            }
            default -> throw new IllegalStateException("Unexpected value: " + event.getCommandType());
        };
    }

    public static void runInvocationCommandTimer(@NotNull final CommandInteractionPayload event, @NotNull final Runnable runnable) {
        switch (event.getCommandType()) {
            case USER -> {
                Histogram.Timer genericTimer = Metrics.Commands.GENERIC_COMMAND_INVOCATION_DURATION.labels("user").startTimer();
                Histogram.Timer commandTimer = Metrics.Commands.USER_CONTEXTCOMMAND_INVOCATION_DURATION.labels(event.getName()).startTimer();

                runnable.run();

                genericTimer.observeDuration();
                commandTimer.observeDuration();
            }
            case MESSAGE -> {
                Histogram.Timer genericTimer = Metrics.Commands.GENERIC_COMMAND_INVOCATION_DURATION.labels("message").startTimer();
                Histogram.Timer commandTimer = Metrics.Commands.MESSAGE_CONTEXTCOMMAND_INVOCATION_DURATION.labels(event.getName()).startTimer();

                runnable.run();

                genericTimer.observeDuration();
                commandTimer.observeDuration();
            }
            case SLASH -> {
                Histogram.Timer genericTimer = Metrics.Commands.GENERIC_COMMAND_INVOCATION_DURATION.labels("slash").startTimer();
                Histogram.Timer commandTimer = Metrics.Commands.SLASHCOMMAND_INVOCATION_DURATION.labels(event.getName()).startTimer();

                runnable.run();

                genericTimer.observeDuration();
                commandTimer.observeDuration();
            }
            default -> throw new IllegalStateException("Unexpected value: " + event.getCommandType());
        };
    }

    public static class HistogramTimerDouble {
        private final Histogram.Timer timer1;
        private final Histogram.Timer timer2;

        @Contract(pure = true)
        HistogramTimerDouble(Histogram.Timer timer1, Histogram.Timer timer2) {
            this.timer1 = timer1;
            this.timer2 = timer2;
        }

        public void observe() {
            timer1.observeDuration();
            timer2.observeDuration();
        }

        @NonNls
        @NotNull
        @Override
        public String toString() {
            return "HistogramTimerDouble{" +
                    "timer1=" + timer1 +
                    ", timer2=" + timer2 +
                    '}';
        }
    }
}
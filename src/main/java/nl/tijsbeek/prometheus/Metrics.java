package nl.tijsbeek.prometheus;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

public class Metrics {
    public static final Gauge GUILD_COUNT = Gauge.build()
            .name("guilds")
            .help("Guild Count")
            .subsystem("bot")
            .register();


    public static final Counter GENERIC_COMMANDS = Counter.build()
            .name("commands_invocations_total")
            .help("Total commands invoked")
            .labelNames("type", "visibility", "command")
            .subsystem("bot")
            .register();

    public static final Histogram GENERIC_COMMAND_HANDLING_DURATION = Histogram.build()
            .name("commands_handling_duration")
            .help("Duration of command handling, before the command runs")
            .subsystem("bot")
            .labelNames("type", "visibility", "command")
            .register();

    public static final Histogram GENERIC_COMMAND_INVOCATION_DURATION = Histogram.build()
            .name("commands_invocation_duration")
            .help("Duration of command invocation")
            .labelNames("type", "visibility", "command")
            .subsystem("bot")
            .register();


    public static final Counter GENERIC_COMPONENTS = Counter.build()
            .name("components_invocations_total")
            .help("Total components invoked")
            .labelNames("type", "listener")
            .subsystem("bot")
            .register();

    public static final Histogram GENERIC_COMPONENT_INVOCATION_DURATION = Histogram.build()
            .name("components_invocation_duration")
            .help("Duration of component invocation")
            .labelNames("type", "listener")
            .subsystem("bot")
            .register();


    public static final Counter GENERIC_MODALS = Counter.build()
            .name("modala_invocations_total")
            .help("Total modals invoked")
            .labelNames("listener")
            .subsystem("bot")
            .register();

    public static final Histogram GENERIC_MODAL_INVOCATION_DURATION = Histogram.build()
            .name("modala_invocation_duration")
            .help("Duration of modal invocation")
            .labelNames("listener")
            .subsystem("bot")
            .register();


    public static final Gauge BUSY_THREADS = Gauge.build()
            .name("busy_threads")
            .help("Busy threads")
            .subsystem("bot")
            .register();

    public static final Gauge TOTAL_THREADS = Gauge.build()
            .name("total_threads")
            .help("Total threads")
            .subsystem("bot")
            .register();
}
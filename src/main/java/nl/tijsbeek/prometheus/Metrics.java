package nl.tijsbeek.prometheus;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

@SuppressWarnings("WeakerAccess")
public class Metrics {
    public static final Gauge GUILD_COUNT = Gauge.build()
            .name("guilds")
            .help("Guild Count")
            .subsystem("bot")
            .register();


    public static final class Commands {
        public static final Counter GENERIC_COMMANDS = Counter.build()
                .name("commands_invocations_total")
                .help("Total commands invoked")
                .labelNames("type")
                .subsystem("bot")
                .register();

        public static final Histogram GENERIC_COMMAND_HANDLING_DURATION = Histogram.build()
                .name("commands_handling_duration")
                .help("Duration of command handling, before the command runs")
                .labelNames("type")
                .subsystem("bot")
                .register();

        public static final Histogram GENERIC_COMMAND_INVOCATION_DURATION = Histogram.build()
                .name("commands_invocation_duration")
                .help("Duration of command invocation")
                .labelNames("type")
                .subsystem("bot")
                .register();


        public static final Counter SLASHCOMMANDS = Counter.build()
                .name("slashcommands_invocations_total")
                .help("Total slash-commands invoked")
                .labelNames("command")
                .subsystem("bot")
                .register();

        public static final Histogram SLASHCOMMAND_HANDLING_DURATION = Histogram.build()
                .name("slashcommands_handling_duration")
                .help("Duration of slash-command handling, before the command runs")
                .labelNames("command")
                .subsystem("bot")
                .register();

        public static final Histogram SLASHCOMMAND_INVOCATION_DURATION = Histogram.build()
                .name("slashcommands_invocation_duration")
                .help("Duration of slash-command invocation")
                .labelNames("command")
                .subsystem("bot")
                .register();


        public static final Counter AUTOCOMPLETES = Counter.build()
                .name("autocomplete_invocations_total")
                .help("Total times autocomplete was invoked")
                .labelNames("command", "option")
                .subsystem("bot")
                .register();

        public static final Histogram AUTOCOMPLETE_INVOCATION_DURATION = Histogram.build()
                .name("autocomplete_slashcommands_invocation_duration")
                .help("Duration of autocomplete invocation")
                .labelNames("command", "option")
                .subsystem("bot")
                .register();


        public static final Counter USER_CONTEXTCOMMANDS = Counter.build()
                .name("user_contextcommands_invocations_total")
                .help("Total user-contextcommands invoked")
                .labelNames("command")
                .subsystem("bot")
                .register();

        public static final Histogram USER_CONTEXTCOMMANDS_HANDLING_DURATION = Histogram.build()
                .name("user_contextcommands_handling_duration")
                .help("Duration of user-contextcommand handling, before the command runs")
                .labelNames("command")
                .subsystem("bot")
                .register();

        public static final Histogram USER_CONTEXTCOMMAND_INVOCATION_DURATION = Histogram.build()
                .name("user_contextcommands_invocation_duration")
                .help("Duration of user-contextcommand invocation")
                .labelNames("command")
                .subsystem("bot")
                .register();


        public static final Counter MESSAGE_CONTEXTCOMMANDS = Counter.build()
                .name("message_contextcommands_invocations_total")
                .help("Total message-contextcommands invoked")
                .labelNames("command")
                .subsystem("bot")
                .register();

        public static final Histogram MESSAGE_CONTEXTCOMMANDS_HANDLING_DURATION = Histogram.build()
                .name("message_contextcommands_handling_duration")
                .help("Duration of message-contextcommand handling, before the command runs")
                .labelNames("command")
                .subsystem("bot")
                .register();

        public static final Histogram MESSAGE_CONTEXTCOMMAND_INVOCATION_DURATION = Histogram.build()
                .name("message_contextcommands_invocation_duration")
                .help("Duration of message-contextcommand invocation")
                .labelNames("command")
                .subsystem("bot")
                .register();
    }



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
            .name("modal_invocations_total")
            .help("Total modals invoked")
            .labelNames("listener")
            .subsystem("bot")
            .register();

    public static final Histogram GENERIC_MODAL_INVOCATION_DURATION = Histogram.build()
            .name("modal_invocation_duration")
            .help("Duration of modal invocation")
            .labelNames("listener")
            .subsystem("bot")
            .register();


    public static final Gauge TOTAL_COMMAND_THREADS = Gauge.build()
            .name("total_command_threads")
            .help("Total command threads")
            .subsystem("bot")
            .register();

    public static final Gauge RUNNING_COMMAND_THREADS = Gauge.build()
            .name("running_command_threads")
            .help("Running command threads")
            .subsystem("bot")
            .register();

    public static final Gauge IDLE_COMMAND_THREADS = Gauge.build()
            .name("idle_command_threads")
            .help("Idle command threads")
            .subsystem("bot")
            .register();
}
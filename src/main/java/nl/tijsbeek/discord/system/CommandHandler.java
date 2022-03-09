package nl.tijsbeek.discord.system;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import nl.tijsbeek.discord.commands.*;
import nl.tijsbeek.discord.components.ComponentDatabase;
import nl.tijsbeek.discord.components.ComponentEntity;
import nl.tijsbeek.prometheus.Metrics;
import nl.tijsbeek.utils.EmbedUtils;
import nl.tijsbeek.utils.StreamUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The command handler.
 * <br/>
 * A command handler registers all {@link InteractionCommand InteractionCommands} to Discord, and will forward their events.
 * All commands have to be added to {@link ListenersList}, otherwise they will be ignored.
 */
public class CommandHandler extends ListenerAdapter {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1,
            Runtime.getRuntime().availableProcessors(),
            1, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(64));

    private final ComponentDatabase componentDatabase;

    private final Map<String, InteractionCommand> nameToInteractionCommand;
    private final Map<String, SlashCommand> nameToSlashCommandCommand;
    private final Map<String, UserContextCommand> nameToUserContextCommand;
    private final Map<String, MessageContextCommand> nameToMessageContextCommand;

    private final List<InteractionCommand> commands;


    /**
     * Creates an instance based of {@link ListenersList#getCommands()}.
     *
     * @param componentDatabase the {@link ComponentDatabase} for components
     * @param listenersList the {@link ListenersList} which contains all commands
     */
    public CommandHandler(@NotNull final ComponentDatabase componentDatabase, @NotNull final ListenersList listenersList) {
        this.componentDatabase = componentDatabase;

        commands = listenersList.getCommands();

        nameToInteractionCommand = streamToMap(commands.stream());
        nameToSlashCommandCommand = filterCommandsToMap(SlashCommand.class, commands.stream());
        nameToUserContextCommand = filterCommandsToMap(UserContextCommand.class, commands.stream());
        nameToMessageContextCommand = filterCommandsToMap(MessageContextCommand.class, commands.stream());
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        updateCommands(event.getJDA());
    }

    /**
     * Registers all the commands coming from the {@link ListenersList} in the constructor to Discord.
     *
     * @param jda the {@link JDA} instance to register on
     */
    public void updateCommands(@NotNull final JDA jda) {
        jda.updateCommands().addCommands(
                commands.stream()
                        .filter(CommandHandler::isEnabled)
                        .filter(isVisibility(InteractionCommandVisibility.GLOBAL))
                        .map(InteractionCommand::getData)
                        .toList()
        ).queue();


        List<CommandData> guildCommands = commands.stream()
                .filter(CommandHandler::isEnabled)
                .filter(isVisibility(InteractionCommandVisibility.GUILD_ONLY))
                .map(InteractionCommand::getData)
                .toList();

        jda.getGuildCache().forEach(guild -> {
            guild.updateCommands()
                    .addCommands(guildCommands)
                    .addCommands(getPrivateEnabledCommands(guild))
                    .queue();
        });
    }

    /**
     * Whenever the {@link InteractionCommand} is enabled, this is defined by {@link InteractionCommand#getState()}.
     *
     * @param interactionCommand the {@link InteractionCommand} to check
     *
     * @return whenever the {@link InteractionCommand#getState()} is {@link InteractionCommandState#ENABLED}
     */
    private static boolean isEnabled(@NotNull final InteractionCommand interactionCommand) {
        return interactionCommand.getState() == InteractionCommandState.ENABLED;
    }

    /**
     * Returns all commands that are enabled in the given {@link Guild}.
     *
     * @param guild the {@link Guild} to check on
     *
     * @return a {@link List} of commands that are enabled
     */
    private List<CommandData> getPrivateEnabledCommands(@NotNull final Guild guild) {
        return commands.stream()
                .filter(CommandHandler::isEnabled)
                .filter(isVisibility(InteractionCommandVisibility.PRIVATE))
                .filter(isEnabledInGuild(guild))
                .map(InteractionCommand::getData)
                .toList();
    }

    /**
     * Creates a {@link Predicate} that checks whenever the command is enabled in the given {@link Guild}.
     *
     * @param guild the {@link Guild} to check on
     *
     * @return the {@link Predicate}
     */
    @NotNull
    @Contract(pure = true)
    private static Predicate<? super InteractionCommand> isEnabledInGuild(@NotNull final Guild guild) {
        return (interactionCommand -> {
            return interactionCommand.getEnabledGuilds().contains(guild.getIdLong());
        });
    }

    /**
     * Creates a {@link Predicate} that checks whenever the command is the right visibility according to {@link InteractionCommand#getVisibility()}.
     *
     * @param visibility the {@link InteractionCommandVisibility} to check on
     *
     * @return the {@link Predicate}
     */
    @NotNull
    @Contract(pure = true)
    private static Predicate<? super InteractionCommand> isVisibility(@NotNull final InteractionCommandVisibility visibility) {
        return (interactionCommand -> {
            return interactionCommand.getVisibility() == visibility;
        });
    }

    /**
     * Filters and maps the given {@link Stream} of {@link InteractionCommand} into {@link T}, which gets converted into a {@link Map}
     * where the key is the {@link InteractionCommand#getName()}, and the value is the {@link InteractionCommand}.
     *
     * @param clazz the {@link Class} to cast to
     * @param commands the {@link Stream} to filter and map
     * @param <T> the {@link InteractionCommand} type, like {@link SlashCommand} and {@link UserContextCommand}
     *
     * @return a {@link Map} of {@link InteractionCommand#getName()} and {@link T}
     */
    private static <T extends InteractionCommand> Map<String, T> filterCommandsToMap(final @NotNull Class<? extends T> clazz, @NotNull final Stream<? super T> commands) {
        return streamToMap(commands
                .filter(clazz::isInstance)
                .map(clazz::cast));
    }

    /**
     * Collects the Stream of {@link InteractionCommand} into a Map where the key is {@link InteractionCommand#getName()} and the value is {@link T}.
     *
     * @param commands the {@link Stream} to collect
     * @param <T> the {@link InteractionCommand} type, like {@link SlashCommand} and {@link UserContextCommand}
     *
     * @return a {@link Map} of {@link InteractionCommand#getName()} and {@link T}
     */
    private static <T extends InteractionCommand> Map<String, T> streamToMap(@NotNull final Stream<? extends T> commands) {
        return commands.collect(Collectors.toMap(InteractionCommand::getName, Function.identity()));
    }

    /**
     * Forwards the given {@link SelectMenu} to the correct command, if the {@link SelectMenu} is expired the {@link SelectMenu} will get disabled.
     *
     * @param event the {@link SelectMenuInteractionEvent} to forward
     */
    @Override
    public void onSelectMenuInteraction(@NotNull final SelectMenuInteractionEvent event) {
        executor.execute(() -> {
            String id = event.getId();

            ComponentEntity componentEntity = componentDatabase.retrieveComponentEntity(id);

            String effectiveListenerId = (componentEntity.getListenerId() == null) ? "" : componentEntity.getListenerId();
            Metrics.GENERIC_COMPONENTS.labels("selectmenu", effectiveListenerId).inc();

            if (componentEntity.isExpired()) {
                expireComponentsMessage(event);
            } else {
                InteractionCommand command = nameToInteractionCommand.get(componentEntity.getListenerId());

                Metrics.GENERIC_COMPONENT_INVOCATION_DURATION.labels("selectmenu", effectiveListenerId).time(() -> {
                    command.onSelectMenuInteraction(event);
                });
            }
        });
    }

    /**
     * Forwards the given {@link Button} to the correct command, if the {@link Button} is expired the {@link Button} will get disabled.
     *
     * @param event the {@link ButtonInteractionEvent} to forward
     */
    @Override
    public void onButtonInteraction(@NotNull final ButtonInteractionEvent event) {
        executor.execute(() -> {
            String id = event.getId();

            ComponentEntity componentEntity = componentDatabase.retrieveComponentEntity(id);

            String effectiveListenerId = (componentEntity.getListenerId() == null) ? "" : componentEntity.getListenerId();
            Metrics.GENERIC_COMPONENTS.labels("button", effectiveListenerId).inc();

            if (componentEntity.isExpired()) {
                expireComponentsMessage(event);
            } else {
                InteractionCommand command = nameToInteractionCommand.get(componentEntity.getListenerId());

                if (command != null) {
                    Metrics.GENERIC_COMPONENT_INVOCATION_DURATION.labels("button", command.getName()).time(() -> {
                        command.onButtonInteraction(event);
                    });
                }
            }
        });
    }

    /**
     * Forwards the given {@link Modal} to the correct command.
     *
     * @param event the {@link ModalInteractionEvent} to forward
     */
    @Override
    public void onModalInteraction(@NotNull final ModalInteractionEvent event) {
        executor.execute(() -> {
            String id = event.getId();

            ComponentEntity componentEntity = componentDatabase.retrieveComponentEntity(id);

            String effectiveListenerId = (componentEntity.getListenerId() == null) ? "" : componentEntity.getListenerId();
            Metrics.GENERIC_MODALS.labels(effectiveListenerId).inc();

            InteractionCommand command = nameToInteractionCommand.get(componentEntity.getListenerId());

            if (command != null) {
                Metrics.GENERIC_MODAL_INVOCATION_DURATION.labels(effectiveListenerId).time(() -> {
                    command.onModalInteraction(event);
                });
            }
        });
    }


    /**
     * Checks the message's components, and disables them when they are expired.
     *
     * @param event the {@link ComponentInteraction} to reply to
     */
    private void expireComponentsMessage(@NotNull final ComponentInteraction event) {
        List<ActionRow> components = event.getMessage().getActionRows()
                .stream()
                .map(actionRow -> {
                    return ActionRow.of(actionRow.getComponents().stream()
                            .map(this::disableComponentWhenExpired)
                            .toList());
                }).toList();

        event.editComponents(components).queue();
    }

    /**
     * Disables the component if it's expired.
     *
     * @param component the {@link ItemComponent} to check
     *
     * @return itself, or itself as disabled
     */
    private ItemComponent disableComponentWhenExpired(@NotNull final ItemComponent component) {

        if (!(component instanceof ActionComponent actionComponent)) {
            return component;
        }

        if (actionComponent.getId() == null) {
            return component;
        }

        ComponentEntity componentEntity = componentDatabase.retrieveComponentEntity(actionComponent.getId());

        if (!componentEntity.isExpired()) {
            return component;
        }

        componentDatabase.remove(componentEntity.getId());

        if (component instanceof Button button) {
            return button.asDisabled();
        } else if (component instanceof SelectMenu selectMenu) {
            return selectMenu.asDisabled();
        } else {
            return component;
        }
    }


    /**
     * Forwards the given {@link SlashCommandInteractionEvent} to the correct command.
     *
     * @param event the {@link SlashCommandInteractionEvent} to forward
     */
    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        executor.execute(() -> {
            if (checkCanRunGeneralCommand(nameToSlashCommandCommand, event)) {
                SlashCommand command = nameToSlashCommandCommand.get(event.getName());

                invocationDurationHistogramByCommand(command).time(() -> {
                    command.onSlashCommandInteraction(event);
                });
            }
        });
    }

    private static Counter.Child commandsCounterByCommand(@NotNull final InteractionCommand command) {
        return Metrics.GENERIC_COMMANDS.labels(command.getType().name(), command.getVisibility().name(), command.getName());
    }

    private static Histogram.Child invocationDurationHistogramByCommand(@NotNull final InteractionCommand command) {
        return Metrics.GENERIC_COMMAND_INVOCATION_DURATION.labels(command.getType().name(), command.getVisibility().name(), command.getName());
    }

    /**
     * Forwards the given {@link CommandAutoCompleteInteractionEvent} to the correct command.
     *
     * @param event the {@link CommandAutoCompleteInteractionEvent} to forward
     */
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        executor.execute(() -> {
            SlashCommand command = nameToSlashCommandCommand.get(event.getName());

            if (null == command) {
                throw new IllegalStateException("Autocomplete, with the command %s wasn't found! Something went extremely wrong.".formatted(event.getName()));
            }

            Metrics.GENERIC_COMMAND_INVOCATION_DURATION.time(() -> {
                command.onCommandAutoCompleteInteractionEvent(event);
            });
        });
    }


    /**
     * Forwards the given {@link UserContextInteractionEvent} to the correct command.
     *
     * @param event the {@link UserContextInteractionEvent} to forward
     */
    @Override
    public void onUserContextInteraction(@NotNull final UserContextInteractionEvent event) {
        executor.execute(() -> {
            if (checkCanRunGeneralCommand(nameToUserContextCommand, event)) {
                UserContextCommand command = nameToUserContextCommand.get(event.getName());

                Metrics.GENERIC_COMMAND_INVOCATION_DURATION.time(() -> {
                    command.onUserContextInteraction(event);
                });
            }
        });
    }

    /**
     * Forwards the given {@link MessageContextInteractionEvent} to the correct command.
     *
     * @param event the {@link MessageContextInteractionEvent} to forward
     */
    @Override
    public void onMessageContextInteraction(@NotNull final MessageContextInteractionEvent event) {
        executor.execute(() -> {
            if (checkCanRunGeneralCommand(nameToMessageContextCommand, event)) {
                MessageContextCommand command = nameToMessageContextCommand.get(event.getName());

                Metrics.GENERIC_COMMAND_INVOCATION_DURATION.time(() -> {
                    command.onMessageContextInteraction(event);
                });
            }
        });
    }

    /**
     * Checks whenever the command exists, whenever the user and the bot have the right permission, and possibly more in the future.
     *
     * @param nameToCommand the {@link Map} which maps the {@link InteractionCommand#getName()} to the {@link InteractionCommand}
     * @param event the {@link CommandInteraction} to reply to on failure
     *
     * @return whenever the command should/can be run
     *
     * @see InteractionCommand#getRequiredUserPermission()
     * @see InteractionCommand#getRequiredBotPermission()
     */
    private static boolean checkCanRunGeneralCommand(@NotNull final Map<String, ? extends InteractionCommand> nameToCommand,
                                                     @NotNull final CommandInteraction event) {
        //noinspection resource - observeDuration closes it
        Histogram.Timer preCommandDuration = Metrics.GENERIC_COMMAND_HANDLING_DURATION.labels(event.getCommandType().name(), event.getName()).startTimer();

        String commandName = event.getName();

        InteractionCommand command = nameToCommand.get(commandName);

        if (null == command) {
            event.reply("Something went wrong.").queue();
            throw new IllegalStateException("%s with the name %s wasn't found! Something went extremely wrong.".formatted(event.getCommandType(), commandName));
        }

        Metrics.GENERIC_COMMANDS.labels(command.getType().name(), command.getVisibility().name(), command.getName()).inc();

        boolean canRun = switch (command.getVisibility()) {
            case GLOBAL -> checkCanRunGlobalCommand(event, command);
            case GUILD_ONLY -> checkCanRunGuildOnlyCommand(event, command);
            case PRIVATE -> checkCanRunPrivateCommand(event, command);
        };

        preCommandDuration.observeDuration();
        return canRun;
    }

    /**
     * Whenever the global command can be run.
     *
     * @param event the {@link CommandInteraction} to reply to on failure
     * @param command the relating {@link InteractionCommand}
     *
     * @return whenever the command can be run
     */
    @Contract(pure = true)
    private static boolean checkCanRunGlobalCommand(@NotNull final CommandInteraction event, @NotNull final InteractionCommand command) {
        return true;
    }

    /**
     * Whenever the guild command can be run.
     *
     * @param event the {@link IReplyCallback} to reply to on failure
     * @param command the relating {@link InteractionCommand}
     *
     * @return whenever the command can be run
     */
    @Contract()
    private static boolean checkCanRunGuildOnlyCommand(@NotNull final IReplyCallback event, @NotNull final InteractionCommand command) {
        Collection<Permission> requiredUserPermission = command.getRequiredUserPermission();
        Collection<Permission> requiredBotPermission = command.getRequiredBotPermission();

        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        GuildChannel guildChannel = event.getGuildChannel();

        boolean userIsMissingPermissions = checkMissingPermissions(event, member.getPermissions(guildChannel), requiredUserPermission, "You are");

        if (userIsMissingPermissions) {
            return false;
        }

        //noinspection UnnecessaryLocalVariable
        boolean botIsNotMissingPermissions = !checkMissingPermissions(event, selfMember.getPermissions(guildChannel), requiredBotPermission, "The bot is");

        return botIsNotMissingPermissions;
    }


    /**
     * Whenever the user has the required permissions.
     *
     * @param event the {@link IReplyCallback} to reply to on failure
     * @param permissions the user's permissions in a {@link Collection} of {@link Permission Permissions}
     * @param requiredPermissions the required permissions in a {@link Collection} of {@link Permission Permissions}
     * @param user the user, examples are "You are" and "The bot is"
     *
     * @return whenever the user has the required permissions
     */
    private static boolean checkMissingPermissions(@NotNull final IReplyCallback event, @NotNull final Collection<Permission> permissions, @NotNull final Collection<Permission> requiredPermissions, String user) {
        Collection<Permission> mutableMissingPermissions = new ArrayList<>(requiredPermissions);
        mutableMissingPermissions.removeAll(permissions);

        if (mutableMissingPermissions.isEmpty()) {
            return false;
        } else {
            event.replyEmbeds(generateLackingPermissionEmbed(event.getMember(), mutableMissingPermissions, user)).queue();
            return true;
        }
    }

    /**
     * Generates the embed for when the user is lacking permissions.
     *
     * @param member the {@link Member}
     * @param missingPermissions the {@link Collection} of {@link Permission Permissions} they are missing
     * @param user the user, examples are "You are" and "The bot is"
     *
     * @return the {@link MessageEmbed} that got generated
     */
    @NotNull
    private static MessageEmbed generateLackingPermissionEmbed(@NotNull final Member member, @NotNull final Collection<Permission> missingPermissions,
                                                               @NotNull @NonNls final String user) {

        String formattedPermissions = StreamUtils.toJoinedString(
                missingPermissions.stream()
                        .map(Permission::getName)
        );

        return EmbedUtils.createBuilder(member)
                .setTitle("Lacking permissions!")
                .setDescription("""
                        %s missing %s.
                        """.formatted(user, formattedPermissions))
                .build();
    }


    /**
     * Whenever the private command can be run.
     *
     * @param event the {@link IReplyCallback} to reply to on failure
     * @param command the relating {@link InteractionCommand}
     *
     * @return whenever the command can be run
     */
    private static boolean checkCanRunPrivateCommand(@NotNull final IReplyCallback event, @NotNull final InteractionCommand command) {
        return checkCanRunGuildOnlyCommand(event, command);
    }
    
    /**
     * The {@link ThreadPoolExecutor} that is being used by the command handler
     *
     * @return the {@link ThreadPoolExecutor}
     */
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
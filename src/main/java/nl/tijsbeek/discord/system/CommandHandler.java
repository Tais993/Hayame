package nl.tijsbeek.discord.system;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
import nl.tijsbeek.discord.commands.*;
import nl.tijsbeek.discord.components.ComponentDatabase;
import nl.tijsbeek.discord.components.ComponentEntity;
import nl.tijsbeek.utils.EmbedUtils;
import nl.tijsbeek.utils.StreamUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandHandler extends ListenerAdapter {
    private final ForkJoinPool pool = ForkJoinPool.commonPool();

    private final ComponentDatabase componentDatabase;

    private final Map<String, InteractionCommand> nameToInteractionCommand;
    private final Map<String, SlashCommand> nameToSlashCommandCommand;
    private final Map<String, UserContextCommand> nameToUserContextCommand;
    private final Map<String, MessageContextCommand> nameToMessageContextCommand;

    private final List<InteractionCommand> commands;


    public CommandHandler(@NotNull final ComponentDatabase componentDatabase, @NotNull final ListenersList listenersList) {
        this.componentDatabase = componentDatabase;

        commands = listenersList.getCommands();

        nameToInteractionCommand = streamToMap(commands.stream());
        nameToSlashCommandCommand = filterCommandsToMap(SlashCommand.class, commands.stream());
        nameToUserContextCommand = filterCommandsToMap(UserContextCommand.class, commands.stream());
        nameToMessageContextCommand = filterCommandsToMap(MessageContextCommand.class, commands.stream());
    }

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

    private static boolean isEnabled(@NotNull final InteractionCommand interactionCommand) {
        return interactionCommand.getState() == InteractionCommandState.ENABLED;
    }

    private List<CommandData> getPrivateEnabledCommands(@NotNull final Guild guild) {
        return commands.stream()
                .filter(CommandHandler::isEnabled)
                .filter(isVisibility(InteractionCommandVisibility.PRIVATE))
                .filter(isEnabledInGuild(guild))
                .map(InteractionCommand::getData)
                .toList();
    }

    @NotNull
    @Contract(pure = true)
    private static Predicate<? super InteractionCommand> isEnabledInGuild(@NotNull final Guild guild) {
        return (interactionCommand -> {
            return interactionCommand.getEnabledGuilds().contains(guild.getIdLong());
        });
    }

    @NotNull
    @Contract(pure = true)
    private static Predicate<? super InteractionCommand> isVisibility(@NotNull final InteractionCommandVisibility visibility) {
        return (interactionCommand -> {
            return interactionCommand.getVisibility() == visibility;
        });
    }


    private static <T extends InteractionCommand> Map<String, T> filterCommandsToMap(final @NotNull Class<? extends T> clazz, @NotNull final Stream<? super T> commands) {
        return streamToMap(commands
                .filter(clazz::isInstance)
                .map(clazz::cast));
    }

    private static <T extends InteractionCommand> Map<String, T> streamToMap(@NotNull final Stream<? extends T> commands) {
        return commands.collect(Collectors.toMap(InteractionCommand::getName, Function.identity()));
    }

    @Override
    public void onSelectMenuInteraction(@NotNull final SelectMenuInteractionEvent event) {
        pool.execute(() -> {
            String id = event.getId();

            ComponentEntity componentEntity = componentDatabase.retrieveComponentEntity(id);

            if (componentEntity.isExpired()) {
                expireComponentsMessage(event);
            } else {
                InteractionCommand command = nameToInteractionCommand.get(componentEntity.getListenerId());

                if (command != null) {
                    command.onSelectMenuInteraction(event);
                }
            }
        });
    }

    @Override
    public void onButtonInteraction(@NotNull final ButtonInteractionEvent event) {
        pool.execute(() -> {
            String id = event.getId();

            ComponentEntity componentEntity = componentDatabase.retrieveComponentEntity(id);

            if (componentEntity.isExpired()) {
                expireComponentsMessage(event);
            } else {
                InteractionCommand command = nameToInteractionCommand.get(componentEntity.getListenerId());

                if (command != null) {
                    command.onButtonInteraction(event);
                }
            }
        });
    }

    @Override
    public void onModalInteraction(@NotNull final ModalInteractionEvent event) {
        pool.execute(() -> {
            String id = event.getId();

            ComponentEntity componentEntity = componentDatabase.retrieveComponentEntity(id);

            InteractionCommand command = nameToInteractionCommand.get(componentEntity.getListenerId());

            if (command != null) {
                command.onModalInteraction(event);
            }
        });
    }


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


    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        pool.execute(() -> {
            if (checkCanRunGeneralCommand(nameToSlashCommandCommand, event)) {
                SlashCommand command = nameToSlashCommandCommand.get(event.getName());

                command.onSlashCommandInteraction(event);
            }
        });
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        pool.execute(() -> {
            SlashCommand command = nameToSlashCommandCommand.get(event.getName());

            command.onCommandAutoCompleteInteractionEvent(event);
        });
    }

    @Override
    public void onUserContextInteraction(@NotNull final UserContextInteractionEvent event) {
        pool.execute(() -> {
            if (checkCanRunGeneralCommand(nameToUserContextCommand, event)) {
                UserContextCommand command = nameToUserContextCommand.get(event.getName());

                command.onUserContextInteraction(event);
            }
        });
    }

    @Override
    public void onMessageContextInteraction(@NotNull final MessageContextInteractionEvent event) {
        pool.execute(() -> {
            if (checkCanRunGeneralCommand(nameToMessageContextCommand, event)) {

                MessageContextCommand command = nameToMessageContextCommand.get(event.getName());

                if (null == command) {
                    event.reply("Something went wrong.").queue();
                    throw new IllegalStateException("%s with the name %s wasn't found! Something went extremely wrong.".formatted(event.getCommandType(), event.getName()));
                }

                command.onMessageContextInteraction(event);
            }
        });
    }


    private static boolean checkCanRunGeneralCommand(@NotNull final Map<String, ? extends InteractionCommand> nameToCommand,
                                                     @NotNull final CommandInteraction event) {

        String commandName = event.getName();

        InteractionCommand command = nameToCommand.get(commandName);

        if (null == command) {
            event.reply("Something went wrong.").queue();
            throw new IllegalStateException("%s with the name %s wasn't found! Something went extremely wrong.".formatted(event.getCommandType(), commandName));
        }

        return switch (command.getVisibility()) {
            case GLOBAL -> checkCanRunGlobalCommand(event, command);
            case GUILD_ONLY -> checkCanRunGuildOnlyCommand(event, command);
            case PRIVATE -> checkCanRunPrivateCommand(event, command);
        };
    }

    private static boolean checkCanRunGlobalCommand(@NotNull final CommandInteraction event, @NotNull final InteractionCommand command) {
        return true;
    }


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


    private static boolean checkMissingPermissions(@NotNull final IReplyCallback event, @NotNull final Collection<Permission> permissions, @NotNull final Collection<Permission> requiredPermissions, String user) {
        Collection<Permission> mutableMissingPermissions = new ArrayList<>(permissions);
        mutableMissingPermissions.removeAll(requiredPermissions);

        if (mutableMissingPermissions.isEmpty()) {
            return false;
        } else {
            event.replyEmbeds(generateLackingPermissionEmbed(event.getMember(), mutableMissingPermissions, user)).queue();
            return true;
        }
    }

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


    private static boolean checkCanRunPrivateCommand(@NotNull final IReplyCallback event, @NotNull final InteractionCommand command) {
        return checkCanRunGuildOnlyCommand(event, command);
    }
}
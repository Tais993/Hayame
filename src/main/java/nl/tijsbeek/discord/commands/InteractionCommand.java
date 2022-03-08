package nl.tijsbeek.discord.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;
import nl.tijsbeek.discord.components.ComponentDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Represents a Discord command.
 *
 * @see AbstractInteractionCommand
 */
public interface InteractionCommand {

    /**
     * The {@link CommandData} that will be registered to Discord, minor adjustments can be made using this instance.
     *
     * @return a modifiable {@link CommandData}
     */
    @NotNull
    CommandData getData();

    /**
     * A shortcut of {@link #getData()}, {@link CommandData#getName()}.
     *
     * @return the name of this command
     */
    @NotNull
    default String getName() {
        return getData().getName();
    }

    /**
     * A shortcut of {@link #getData()}, {@link CommandData#getType()}.
     *
     * @return the type of this command
     */
    default Command.Type getType() {
        return getData().getType();
    }

    /**
     * The {@link InteractionCommandVisibility visibility} of the command. If set to {@link InteractionCommandVisibility#PRIVATE},
     * the guilds from {@link #getEnabledGuilds()} will receive access to this command.
     *
     * @return the {@link InteractionCommandVisibility} of this command
     */
    @NotNull
    InteractionCommandVisibility getVisibility();

    /**
     * A {@link Collection} of all enabled guilds their ID.
     * <br/>
     * If {@link #getVisibility()} is set to {@link InteractionCommandVisibility#PRIVATE PRIVATE}, the returned guilds will receive the command.
     *
     * @return all enabled guilds
     *
     * @see #getVisibility()
     */
    @NotNull
    @UnmodifiableView
    Collection<Long> getEnabledGuilds();

    /**
     * A {@link Collection} of the {@link Permission permissions} required for the user when running this command.
     * <br/>
     * If the user lacks permissions an error message targeted to the user will be displayed, and the command event won't trigger.
     *
     * @return a {@link Collection} of {@link Permission permissions}
     */
    @NotNull
    @UnmodifiableView
    Collection<Permission> getRequiredUserPermission();


    /**
     * A {@link Collection} of the {@link Permission permissions} required for the bot when running this command.
     * <br/>
     * If the bot lacks permissions an error message targeted to the moderators will be displayed, and the command event won't trigger.
     *
     * @return a {@link Collection} of {@link Permission permissions}
     */
    @NotNull
    @UnmodifiableView
    Collection<Permission> getRequiredBotPermission();

    /**
     * Whenever the command is enabled or disabled, if disabled the command won't be registered and no events will be sent.
     *
     * @return The {@link InteractionCommandState}
     */
    @NotNull
    InteractionCommandState getState();


    /**
     * This event triggers when a select menu with the right command ID gets triggered.
     * <br/>
     * Component ID's should be created using {@link ComponentDatabase#createId(LocalDateTime, String...)}
     *
     * @param event the {@link SelectMenuInteractionEvent}
     */
    void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event);

    /**
     * This event triggers when a button with the right command ID gets triggered.
     * <br/>
     * Component ID's should be created using {@link ComponentDatabase#createId(LocalDateTime, String...)}
     *
     * @param event the {@link ButtonInteractionEvent}
     */
    void onButtonInteraction(@NotNull ButtonInteractionEvent event);

    /**
     * This event triggers when a modal with the right command ID gets triggered.
     * <br/>
     * Component ID's should be created using {@link ComponentDatabase#createId(LocalDateTime, String...)}
     *
     * @param event the {@link ModalInteractionEvent}
     */
    void onModalInteraction(@NotNull ModalInteractionEvent event);
}
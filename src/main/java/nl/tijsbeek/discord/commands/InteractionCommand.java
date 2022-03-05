package nl.tijsbeek.discord.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface InteractionCommand {

    @NotNull
    CommandData getData();

    @NotNull
    default String getName() {
        return getData().getName();
    }

    @NotNull
    InteractionCommandVisibility getVisibility();

    @NotNull
    @UnmodifiableView
    Collection<Long> getEnabledGuilds();

    @NotNull
    @UnmodifiableView
    Collection<Permission> getRequiredUserPermission();

    @NotNull
    @UnmodifiableView
    Collection<Permission> getRequiredBotPermission();

    @NotNull
    InteractionCommandState getState();

    void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event);

    void onButtonInteraction(@NotNull ButtonInteractionEvent event);

    void onModalInteraction(@NotNull ModalInteractionEvent event);
}
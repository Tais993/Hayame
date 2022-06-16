package nl.tijsbeek.discord.commands.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import nl.tijsbeek.database.databases.Databases;
import nl.tijsbeek.database.databases.UserProfileDatabase;
import nl.tijsbeek.database.databases.UserSocialDatabase;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import org.flywaydb.core.internal.database.base.Database;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UserProfileCommand extends AbstractSlashCommand {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileCommand.class);

    private final UserProfileDatabase userProfileDatabase;
    private final UserSocialDatabase userSocialDatabase;

    public static final String VIEW_SUBCOMMAND = "view";
    public static final String EDIT_SUBCOMMAND = "edit";

    protected UserProfileCommand(Databases databases) {
        super(Commands.slash("user-profile", "Shows another user's profile, or edit/create your own profile!"), InteractionCommandVisibility.GLOBAL);


        this.userProfileDatabase = databases.getUserProfileDatabase();
        this.userSocialDatabase = databases.getUserSocialDatabase();


        SubcommandData view = new SubcommandData(VIEW_SUBCOMMAND, "View an user's profile")
                .addOption(OptionType.USER, "user", "The user to view the profile from");

        SubcommandData edit = new SubcommandData(EDIT_SUBCOMMAND, "Edit your own profile");

        getData().addSubcommands(view, edit);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getSubcommandName().equals(VIEW_SUBCOMMAND)) {
            viewCommandInteraction(event);
        } else if (event.getSubcommandName().equals(EDIT_SUBCOMMAND)) {
            editCommandInteraction(event);
        }
    }

    private void viewCommandInteraction(SlashCommandInteraction event) {


    }

    private void editCommandInteraction(SlashCommandInteractionEvent event) {
    }
}

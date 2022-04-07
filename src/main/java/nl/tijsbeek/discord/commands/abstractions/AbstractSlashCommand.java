package nl.tijsbeek.discord.commands.abstractions;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.SlashCommand;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An abstraction of {@link SlashCommand} with the intention to improve developer experience.
 * <p/>
 * This implements methods like {@link #getData()}, {@link #getVisibility()} and {@link #getState()} for you. Their values are
 * inserted within the constructor.
 * <p/>
 * Methods like {@link #addEnabledGuilds(Long...)}, {@link #addRequiredBotPermission(Permission...)} and {@link #addRequiredUserPermission(Permission...)} exist to improve the experience.
 */
public abstract class AbstractSlashCommand extends AbstractInteractionCommand implements SlashCommand {
    private final SlashCommandData commandData;

    protected AbstractSlashCommand(@NotNull final SlashCommandData commandData, @NotNull final InteractionCommandVisibility visibility) {
        this(commandData, visibility, InteractionCommandState.ENABLED);
    }

    protected AbstractSlashCommand(@NotNull final SlashCommandData commandData, @NotNull final InteractionCommandVisibility visibility, @NotNull final InteractionCommandState state) {
        super(commandData, visibility, state);

        this.commandData = commandData;
    }

    @Override
    @SuppressWarnings("SuspiciousGetterSetter")
    public @NotNull SlashCommandData getData() {
        return commandData;
    }

    @Override
    public void onCommandAutoCompleteInteractionEvent(@NotNull final CommandAutoCompleteInteractionEvent event) {

    }

    /**
     * This method copies the given {@link OptionData} for the given amount of times into a
     * {@link List}. <br>
     * This makes all the {@link OptionData OptionData's} optional! Everything else gets exactly
     * copied.
     *
     * @param optionData The {@link OptionData} to copy.
     * @param amount The amount of times to copy
     *
     * @return An unmodifiable {@link List} of the copied {@link OptionData OptionData's}
     *
     * @see #varArgOptionsToList(Collection, Function, String)
     */
    @Unmodifiable
    protected static final @NotNull List<OptionData> generateOptionalVarArgList(
            final @NotNull OptionData optionData, @Range(from = 1, to = 25) final int amount) {

        return IntStream.range(1, amount).mapToObj(i -> copyOption(optionData, optionData.getName() + i)).toList();
    }

    /**
     * Copies the given {@link OptionData} and changes the name to the given name.
     *
     * @param optionData the {@link OptionData} to copy
     * @param name the new name
     *
     * @return the copied {@link OptionData}
     */
    @Contract("_, _ -> new")
    private static final @NotNull OptionData copyOption(@NotNull final OptionData optionData, final String name) {
        return new OptionData(optionData.getType(), name, optionData.getDescription());
    }

    /**
     * This method takes a {@link Collection} of {@link OptionMapping OptionMapping's}, these get
     * mapped using the given {@link Function}
     *
     * @param options A {@link Collection} of {@link OptionMapping OptionMapping's}.
     * @param mapper The mapper {@link Function}
     * @param <T> The type to map it to.
     *
     * @return A modifiable {@link List} of the given type
     *
     * @see #generateOptionalVarArgList(OptionData, int)
     */
    protected static <T> List<T> varArgOptionsToList(
            final @NotNull Collection<? extends OptionMapping> options,
            final @NotNull Function<? super OptionMapping, ? extends T> mapper,
            final @NotNull String name) {

        return options.stream()
                .filter(option -> option.getName().startsWith(name))
                .map(mapper)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
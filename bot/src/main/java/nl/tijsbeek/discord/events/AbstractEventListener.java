package nl.tijsbeek.discord.events;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Abtraction of {@link CustomEventListener}.
 * <br/>
 * This implements {@link #getRequiredCacheFlags()} and {@link #getRequiredIntents()} for you, you can give them values using
 * {@link #addRequiredCacheFlags(CacheFlag...)} and {@link #addRequiredIntents(GatewayIntent...)}.
 * <br/>
 * Also offers you many helpers methods from {@link ListenerAdapter}.
 */
public class AbstractEventListener extends ListenerAdapter implements CustomEventListener {
    private final Collection<GatewayIntent> requiredIntents = new ArrayList<>();
    private final Collection<CacheFlag> requiredCacheFlags = new ArrayList<>();

    /**
     * Adds the given {@link GatewayIntent GatewayIntents} to a {@link List} that will be returned when calling {@link #getRequiredIntents()}.
     *
     * @param intents an array of {@link GatewayIntent GatewayIntents}
     */
    protected final void addRequiredIntents(@NotNull final GatewayIntent... intents) {
        requiredIntents.addAll(List.of(intents));
    }

    /**
     * Adds the given {@link CacheFlag CacheFlags} to a {@link List}  that will be returned when calling {@link #getRequiredCacheFlags()}.
     *
     * @param cacheFlags an array of {@link CacheFlag CacheFlags}
     */
    protected final void addRequiredCacheFlags(@NotNull final CacheFlag... cacheFlags) {
        requiredCacheFlags.addAll(List.of(cacheFlags));
    }

    @NotNull
    @Override
    @UnmodifiableView
    public Collection<CacheFlag> getRequiredCacheFlags() {
        return Collections.unmodifiableCollection(requiredCacheFlags);
    }

    @NotNull
    @Override
    @UnmodifiableView
    public Collection<GatewayIntent> getRequiredIntents() {
        return Collections.unmodifiableCollection(requiredIntents);
    }
}

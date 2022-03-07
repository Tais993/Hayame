package nl.tijsbeek.discord.events;

import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

/**
 * Adds event support for classes.
 *
 * @see AbstractEventListener
 */
public interface CustomEventListener extends EventListener {

    /**
     * Returns all the {@link GatewayIntent GatewayIntents} this class requires for its events.
     *
     * @return a {@link Collection} of {@link GatewayIntent GatewayIntents}
     */
    @NotNull
    @UnmodifiableView
    Collection<GatewayIntent> getRequiredIntents();

    /**
     * Returns all the {@link CacheFlag CacheFlags} this class requires for its events.
     *
     * @return a {@link Collection} of {@link CacheFlag CacheFlags}
     */
    @NotNull
    @UnmodifiableView
    Collection<CacheFlag> getRequiredCacheFlags();
}
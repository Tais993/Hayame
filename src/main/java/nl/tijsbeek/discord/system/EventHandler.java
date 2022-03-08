package nl.tijsbeek.discord.system;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import nl.tijsbeek.discord.events.CustomEventListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles all {@link CustomEventListener CustomEventListeners}.
 */
public class EventHandler implements EventListener {
    private final List<CustomEventListener> listeners;

    /**
     * Creates an instance based of {@link ListenersList#getEventListeners()}.
     *
     * @param listenersList the {@link ListenersList} to take the {@link CustomEventListener CustomEventListeners} from
     */
    @Contract(pure = true)
    public EventHandler(@NotNull final ListenersList listenersList) {
        listeners = listenersList.getEventListeners();
    }

    /**
     * Returns the {@link CacheFlag CacheFlags} as a {@link Set} based of {@link CustomEventListener#getRequiredCacheFlags()}.
     *
     * @return the {@link Set} of {@link CacheFlag CacheFlags}
     */
    @NotNull
    @Contract(pure = true)
    public final Set<CacheFlag> getCacheFlags() {
        return listeners.stream()
                .map(CustomEventListener::getRequiredCacheFlags)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Returns the {@link GatewayIntent GatewayIntents} as a {@link Set} based of {@link CustomEventListener#getRequiredIntents()}.
     *
     * @return the {@link Set} of {@link GatewayIntent GatewayIntents}
     */
    @NotNull
    @Contract(pure = true)
    public final Set<GatewayIntent> getGatewayIntents() {
        return listeners.stream()
                .map(CustomEventListener::getRequiredIntents)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }


    /**
     * Forwards the given {@link GenericEvent} to all {@link CustomEventListener CustomEventListeners}.
     *
     * @param event the {@link GenericEvent} to forward
     */
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }
}
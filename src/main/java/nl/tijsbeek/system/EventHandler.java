package nl.tijsbeek.system;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import nl.tijsbeek.events.CustomEventListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventHandler implements EventListener {
    private final List<CustomEventListener> listeners;

    @Contract(pure = true)
    public EventHandler(@NotNull final ListenersList listenersList) {
        listeners = listenersList.getEventListeners();
    }

    @NotNull
    @Contract(pure = true)
    public final Set<CacheFlag> getCacheFlags() {
        return listeners.stream()
                .map(CustomEventListener::getRequiredCacheFlags)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @NotNull
    @Contract(pure = true)
    public final Set<GatewayIntent> getGatewayIntents() {
        return listeners.stream()
                .map(CustomEventListener::getRequiredIntents)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }


    @Override
    public void onEvent(@NotNull GenericEvent event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }

}
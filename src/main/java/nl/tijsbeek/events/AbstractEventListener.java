package nl.tijsbeek.events;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AbstractEventListener extends ListenerAdapter implements CustomEventListener {
    private final Collection<GatewayIntent> requiredIntents = new ArrayList<>();
    private final Collection<CacheFlag> requiredCacheFlags = new ArrayList<>();

    protected final void addRequiredIntents(@NotNull final GatewayIntent... intents) {
        requiredIntents.addAll(List.of(intents));
    }

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
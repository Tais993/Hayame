package nl.tijsbeek.commands;

import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface CustomEventListener extends EventListener {

    @NotNull
    @UnmodifiableView
    Collection<GatewayIntent> getRequiredIntents();

    @NotNull
    @UnmodifiableView
    Collection<CacheFlag> getRequiredCacheFlags();
}
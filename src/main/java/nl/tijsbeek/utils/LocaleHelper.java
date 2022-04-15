package nl.tijsbeek.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Has some helper methods for dealing with resource bundles.
 */
public class LocaleHelper {


    /**
     * Returns a resource based on the given name.
     *
     * @param locale the {@link Locale} of the resource to return
     * @param baseName the resource bundle's name
     *
     * @return a {@link ResourceBundle} of the specified locale, or default
     */
    public static ResourceBundle getResource(@NotNull final Locale locale, @NotNull final String baseName) {
        return ResourceBundle.getBundle("i18n\\" + baseName, locale);
    }

    /**
     * Returns the default resource used by this bot.
     *
     * @param locale the {@link Locale} of the resource to return
     *
     * @return a {@link ResourceBundle} of the specified locale, or default
     */
    public static ResourceBundle getBotResource(@NotNull final Locale locale) {
        return getResource(locale, "bot");
    }
}
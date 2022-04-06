package nl.tijsbeek.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleHelper {


    public static ResourceBundle getResource(@NotNull final Locale locale, @NotNull final String baseName) {
        return ResourceBundle.getBundle("i18n\\" + baseName, locale);
    }

    public static ResourceBundle getSlashCommandResource(@NotNull final Locale locale) {
        return getResource(locale, "SlashCommands");
    }
}

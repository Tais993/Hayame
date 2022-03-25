/*
    This file is part of TJ-Bot (https://github.com/Together-Java/TJ-Bot).

    TJ-Bot is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TJ-Bot is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TJ-Bot.  If not, see <http://www.gnu.org/licenses/> & <https://github.com/Together-Java/TJ-Bot>.
 */
package nl.tijsbeek.utils;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Class which contains all actions a Discord client accepts.
 * <p>
 * This allows you to open DM's {@link Channels#DM_CHANNEL}, specific settings
 * {@link Settings.App#VOICE} and much more.
 *
 * <p>
 * A few notes;
 * <ul>
 * <li>iOS and Android are NOT supported</li>
 * <li>It opens the LAST installed Discord version (Discord, Canary, PTB)</li>
 * </ul>
 *
 * <p>
 * Example:
 *
 * <pre>
 * <code>
 * event.reply("Open Discord's secret home page!")
 *      .addActionRow(DiscordClientAction.Guild.GUILD_HOME_CHANNEL.asLinkButton("Open home page!", event.getGuild().getId())
 * </code>
 * </pre>
 *
 * To improve readability, one might want to use a static import like:
 *
 * <pre>
 * <code>
 * event.reply(whoIsCommandOutput)
 *      .addActionRow(USER.asLinkButton("Open home page!", target.getId())
 * </code>
 * </pre>
 */
public final class DiscordClientAction {

    public static final String DISCORD_PROTOCOL = "discord://-/";

    /**
     * Creates a DiscordClientAction based on the given URL, this might be used by maintainers for easy testing for new "URL's"
     * If an url requires arguments, these have to be added within brackets, examples are {@code {ARGUMENT-NAME}}, {@code {GUILD-ID}} and more.
     * See the linked examples for more
     *
     * @param url the url
     *
     * @return a DiscordClientAction based on the given URL
     *
     * @see General#USER
     * @see Guild#GUILD_CHANNEL
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static DiscordClientAction ofCustomUrl(String url) {
        return new DiscordClientAction(url);
    }

    /**
     * Contains some of the more general actions
     */
    public enum General {
        ;

        public static final DiscordClientAction HOME = new DiscordClientAction("");
        public static final DiscordClientAction FRIENDS = new DiscordClientAction("");

        public static final DiscordClientAction USER =
                new DiscordClientAction("users/{USER-ID}");
        public static final DiscordClientAction JOIN_INVITE =
                new DiscordClientAction("invite/{INVITE-CODE}");
        public static final DiscordClientAction HUB_MEMBERSHIP_SCREENING =
                new DiscordClientAction("member-verification-for-hub/{HUB-ID}");
        public static final DiscordClientAction STORE =
                new DiscordClientAction("store");

        public static final DiscordClientAction HYPESQUAD =
                new DiscordClientAction("settings/hypesquad_online");
        public static final DiscordClientAction CHANGELOGS =
                new DiscordClientAction("settings/changelogs");
    }

    /**
     * Contains guild specific actions
     */
    public enum Guild {
        ;

        public static final DiscordClientAction GUILD =
                new DiscordClientAction("channels/{GUILD-ID}");
        public static final DiscordClientAction GUILD_CHANNEL =
                new DiscordClientAction("channels/{GUILD-ID}/{CHANNEL-ID}");

        public static final DiscordClientAction GUILD_DISCOVERY =
                new DiscordClientAction("guild-discovery");
        public static final DiscordClientAction GUILDS_CREATE =
                new DiscordClientAction("guilds/create");


        public static final DiscordClientAction GUILD_EVENT =
                new DiscordClientAction("events/{GUILD-ID}/{EVENT-ID}");
        public static final DiscordClientAction GUILD_MEMBERSHIP_SCREENING =
                new DiscordClientAction("member-verification/{GUILD-ID}");

        /**
         * Beta Discord feature
         */
        public static final DiscordClientAction GUILD_HOME_CHANNEL =
                new DiscordClientAction("channels/{GUILD-ID}/@home");
    }

    /**
     * Contains actions related to channels
     */
    public enum Channels {
        ;

        public static final DiscordClientAction DM_CHANNEL =
                new DiscordClientAction("channels/@me/{CHANNEL-ID}");
        public static final DiscordClientAction DM_CHANNEL_MESSAGE =
                new DiscordClientAction("channels/@me/{CHANNEL-ID}/{MESSAGE-ID}");
        public static final DiscordClientAction GUILD_CHANNEL =
                new DiscordClientAction("channels/{GUILD-ID}/{CHANNEL-ID}");
        public static final DiscordClientAction GUILD_CHANNEL_MESSAGE = new DiscordClientAction(
                "channels/{GUILD-ID}/{CHANNEL-ID}/{MESSAGE-ID}");
    }

    /**
     * Contains actions related to the settings menu
     */
    public enum Settings {
        ;

        /**
         * Contains all user settings
         */
        public enum User {
            ;

            public static final DiscordClientAction ACCOUNT =
                    new DiscordClientAction("settings/account");
            public static final DiscordClientAction PROFILE_CUSTOMIZATION =
                    new DiscordClientAction("settings/profile-customization");
            public static final DiscordClientAction PRIVACY_AND_SAFETY =
                    new DiscordClientAction("settings/privacy-and-safety");
            public static final DiscordClientAction AUTHORIZED_APPS =
                    new DiscordClientAction("settings/authorized-apps");
            public static final DiscordClientAction CONNECTIONS =
                    new DiscordClientAction("settings/connections");
        }

        /**
         * Contains all payment settings
         */
        public enum Payment {
            ;

            public static final DiscordClientAction PREMIUM =
                    new DiscordClientAction("settings/premium");
            public static final DiscordClientAction SUBSCRIPTIONS =
                    new DiscordClientAction("settings/subscriptions");
            public static final DiscordClientAction INVENTORY =
                    new DiscordClientAction("settings/inventory");
            public static final DiscordClientAction BILLING =
                    new DiscordClientAction("settings/billing");
        }

        /**
         * Contains all app settings
         */
        public enum App {
            ;

            public static final DiscordClientAction APPEARANCE =
                    new DiscordClientAction("settings/appearance");
            public static final DiscordClientAction ACCESSIBILITY =
                    new DiscordClientAction("settings/accessibility");
            public static final DiscordClientAction VOICE =
                    new DiscordClientAction("settings/voice");
            public static final DiscordClientAction TEXT =
                    new DiscordClientAction("settings/text");
            public static final DiscordClientAction NOTIFICATIONS =
                    new DiscordClientAction("settings/notifications");
            public static final DiscordClientAction KEYBINDS =
                    new DiscordClientAction("settings/keybinds");
            public static final DiscordClientAction LOCALE =
                    new DiscordClientAction("settings/locale");

            /**
             * @see #LINUX
             */
            public static final DiscordClientAction WINDOWS =
                    new DiscordClientAction("settings/windows");

            /**
             * @see #WINDOWS
             */
            public static final DiscordClientAction LINUX =
                    new DiscordClientAction("settings/linux");

            public static final DiscordClientAction STREAMER_MODE =
                    new DiscordClientAction("settings/streamer-mode");
            public static final DiscordClientAction ADVANCED =
                    new DiscordClientAction("settings/advanced");
        }

        /**
         * Contains some of the more general settings
         */
        public enum General {
            ;

            public static final DiscordClientAction ACTIVITY_STATUS =
                    new DiscordClientAction("settings/activity-status");
            public static final DiscordClientAction ACTIVITY_OVERLAY =
                    new DiscordClientAction("settings/overlay");
            public static final DiscordClientAction HYPESQUAD =
                    new DiscordClientAction("settings/hypesquad_online");
            public static final DiscordClientAction CHANGELOGS =
                    new DiscordClientAction("settings/changelogs");
        }
    }

    public enum Library {
        ;

        public static final DiscordClientAction LIBRARY_GAMES =
                new DiscordClientAction("library");
        public static final DiscordClientAction LIBRARY_SETTINGS =
                new DiscordClientAction("library/settings");
        public static final DiscordClientAction LIBRARY_ITEM_ACTION =
                new DiscordClientAction("library/{SKU-ID}/LAUNCH");
        public static final DiscordClientAction SKU_STORE_PAGE =
                new DiscordClientAction("store/skus/{SKU-ID}");
        public static final DiscordClientAction APPLICATION_STORE_PAGE =
                new DiscordClientAction("store/applications/{APPLICATION-ID}");
    }

    /**
     * Pattern for the arguments, finds everything within brackets
     */
    public static final Pattern argumentPattern = Pattern.compile("\\{[^}]*}");

    private final String url;

    @Contract(pure = true)
    private DiscordClientAction(final String url) {
        this.url = DISCORD_PROTOCOL + url;
    }

    /**
     * The raw URL without any arguments.
     *
     * <p>
     * Most likely you should use {@link #formatUrl(String...)} instead, that one throws when an
     * argument is lacking.
     *
     * @return A {@link String} of the URL
     * @see #formatUrl(String...)
     */
    public String getRawUrl() {
        return url;
    }

    /**
     * Format's the URL with the given arguments.
     *
     * @param arguments An array of the arguments this action requires
     * @return The formatted URL as an {@link String}
     * @throws IllegalArgumentException When missing arguments
     */
    public String formatUrl(final String @NotNull... arguments) {
        String localUrl = url;

        for (final String argument : arguments) {
            localUrl = argumentPattern.matcher(localUrl).replaceFirst(argument);
        }

        if (argumentPattern.matcher(localUrl).find()) {
            throw new IllegalArgumentException("Missing arguments for URL " + localUrl + "!");
        }

        return localUrl;
    }

    /**
     * Format's the action as a link button.
     *
     * @param label The label of the button, see {@link Button#link(String, String)} for the
     *        requirements
     * @param arguments An array of the arguments this action requires
     * @return A {@link Button} of {@link ButtonStyle#LINK} with the given label
     * @throws IllegalArgumentException When missing arguments
     */
    public Button asLinkButton(@NotNull final String label, final String... arguments) {
        return Button.link(formatUrl(arguments), label);
    }

    @Override
    public String toString() {
        return "DiscordClientAction{" + "url='" + url + '\'' + '}';
    }
}
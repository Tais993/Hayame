package nl.tijsbeek.database.tables;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class UserSocial {
    private final long id;

    private String platformName;
    private String platformIcon;
    private String platformUserUrl;
    private String platformUserName;

    @Contract(pure = true)
    public UserSocial(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getPlatformIcon() {
        return platformIcon;
    }

    public String getPlatformUserUrl() {
        return platformUserUrl;
    }

    public String getPlatformUserName() {
        return platformUserName;
    }


    @Contract(pure = true)
    public UserSocial setPlatform(@NotNull final String platformName, @Nullable final String platformIcon) {
        setPlatformName(platformName);
        setPlatformIcon(platformIcon);
        return this;
    }

    @Contract(pure = true)
    public UserSocial setPlatformName(@NotNull final String platformName) {
        this.platformName = Objects.requireNonNull(platformName, "PlatformName cannot be null!");
        return this;
    }

    @Contract(pure = true)
    public UserSocial setPlatformIcon(final String platformIcon) {
        this.platformIcon = platformIcon;
        return this;
    }


    public UserSocial setPlatformUser(@NotNull final String platformUserName, @Nullable final String platformUserUrl) {
        setPlatformUserName(platformUserName);
        setPlatformUserUrl(platformUserUrl);
        return this;
    }

    public UserSocial setPlatformUserName(@NotNull final String platformUserName) {
        this.platformUserName = Objects.requireNonNull(platformUserName, "PlatformUserName cannot be null!");
        return this;
    }

    public UserSocial setPlatformUserUrl(@Nullable final String platformUserUrl) {
        this.platformUserUrl = platformUserUrl;
        return this;
    }



    public enum Platforms {
        BITBUCKET("BitBucket", "https://seeklogo.com/images/B/bitbucket-logo-D072214725-seeklogo.com.png"),
        INSTAGRAM("Instagram", "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fa%2Fa5%2FInstagram_icon.png&f=1&nofb=1"),
        GITHUB("GitHub", "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"),
        GITLAB("GitLab", "https://about.gitlab.com/images/press/logo/png/gitlab-logo-500.png"),
        PINTEREST("Pinterest", "https://d3cy9zhslanhfa.cloudfront.net/media/BBEEEEC7-E954-4223-B5A061E37D0C03E2/D40AE47F-73E4-4E1E-8F00E2FF6C607646/webimage-29AE0DE1-EBD2-43B7-BBE6FF0B512493BC.png"),
        REDDIT("Reddit", "https://www.redditinc.com/assets/images/site/reddit-logo.png"),
        TWITTER("Twitter", "https://about.twitter.com/content/dam/about-twitter/en/brand-toolkit/brand-download-img-1.jpg.twimg.1920.jpg"),
        TWITCH("Twitch", "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.pngfind.com%2Fpngs%2Fm%2F180-1800308_twitch-logo-png-transparent-background-twitch-logo-no.png&f=1&nofb=1"),
        YOUTUBE("YouTube", "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2F1000logos.net%2Fwp-content%2Fuploads%2F2017%2F05%2FNew-YouTube-logo.jpg&f=1&nofb=1")
        ;


        private final String platformName;
        private final String platformIcon;

        Platforms(@NotNull final String platformName, @NotNull final String platformIcon) {
            this.platformName = Objects.requireNonNull(platformName, "PlatformName cannot be null!");
            this.platformIcon = Objects.requireNonNull(platformIcon, "PlatformIcon cannot be null!");
        }

        @NotNull
        @Contract(pure = true)
        public String getPlatformName() {
            return platformName;
        }

        @NotNull
        @Contract(pure = true)
        public String getPlatformIcon() {
            return platformIcon;
        }
    }
}

package nl.tijsbeek.database.tables;

import net.dv8tion.jda.api.entities.User;
import nl.tijsbeek.database.databases.AuditLogDatabase;
import org.jetbrains.annotations.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Contains an audit log entry, examples for types are "report", "warn" and more
 * @param author the action taker / author's ID
 * @param target the target's ID
 * @param reason the reason for the action
 * @param type the action type
 * @param expireTime the duration of the action, {@link LocalDateTime#MAX} can be useful for infinite and NULL for none.
 * @param creationTime the creation date of the
 * @param attachmentUrls the duration of the action, 01 for infinite and NULL for none
 */
public record CustomAuditLogEntry(long caseId, long author, long target, String reason, Type type, Instant expireTime, Instant creationTime, List<String> attachmentUrls) {

    /**
     * See {@link CustomAuditLogEntryBuilder} for building entries.
     *
     * @param caseId the case's ID (will be ignored when using {@link AuditLogDatabase#insert(CustomAuditLogEntry)}
     * @param author the author's ID
     * @param target the target's ID
     * @param reason the reason for the action
     * @param type the action type
     * @param expireTime the expiration time of the action, {@link LocalDateTime#MAX} can be useful for infinite and NULL for none.
     * @param creationTime the creation time of this action, when {@code NULL} this will be set automatically.
     * @param attachmentUrls
     */
    public CustomAuditLogEntry(final long caseId, final long author, final long target, final String reason, @NotNull final Type type,
                               final Instant expireTime, final @Nullable Instant creationTime, @NotNull final List<String> attachmentUrls) {
        this.caseId = caseId;
        this.author = author;
        this.target = target;
        this.reason = reason;
        this.type = Objects.requireNonNull(type);
        this.expireTime = expireTime;
        this.creationTime = creationTime;
        this.attachmentUrls = Objects.requireNonNull(attachmentUrls);
    }

    public enum Type {
        REPORT(1);

        private final int key;
        private final boolean isModerationAction;

        @Contract(pure = true)
        Type(final int key) {
            this.key = key;
            this.isModerationAction = false;
        }

        @Contract(pure = true)
        Type(final int key, final boolean isModerationAction) {
            this.key = key;
            this.isModerationAction = isModerationAction;
        }


        @Contract(pure = true)
        public int getKey() {
            return key;
        }

        @Contract(pure = true)
        public boolean isModerationAction() {
            return isModerationAction;
        }


        @NotNull
        @Contract(pure = true)
        public static Type byKey(@Range(from = 1L, to = 1L) final int key) {
            for (final Type type : values()) {
                if (type.getKey() == key) {
                    return type;
                }
            }

            throw new IllegalArgumentException("Invalid key!");
        }


        @NonNls
        @NotNull
        @Override
        @Contract(pure = true)
        public String toString() {
            return "Type{" +
                    "key=" + key +
                    ", isModerationAction=" + isModerationAction +
                    '}';
        }
    }


    public static final class CustomAuditLogEntryBuilder {
        private long caseId;

        private long author;
        private long target;

        private String reason;
        private CustomAuditLogEntry.Type type;

        private Instant expireTime;
        private Instant creationTime;

        private List<String> attachmentUrls;

        @Contract(pure = true)
        public CustomAuditLogEntryBuilder() {}

        /**
         * <b>This will be ignored when inserting using {@link AuditLogDatabase#insert(CustomAuditLogEntry)}</b>
         *
         * @param caseId the case's ID
         *
         * @return this builder
         */
        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setCaseId(final long caseId) {
            this.caseId = caseId;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setAuthor(@NotNull final User author) {
            this.author = author.getIdLong();
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setAuthor(final long author) {
            this.author = author;
            return this;
        }


        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setTarget(@NotNull final User target) {
            this.target = target.getIdLong();
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setTarget(final long target) {
            this.target = target;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setReason(final String reason) {
            this.reason = reason;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setType(final CustomAuditLogEntry.Type type) {
            this.type = type;
            return this;
        }


        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setExpireTime(final Instant expireTime) {
            this.expireTime = expireTime;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setExpireTime(final Timestamp expireTimestamp) {
            if (null != expireTimestamp) {
                this.expireTime = expireTimestamp.toInstant();
            }
            return this;
        }


        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setCreationTime(final Instant creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setCreationTime(final Timestamp creationTimestamp) {
            if (null != creationTimestamp) {
                this.creationTime = creationTimestamp.toInstant();
            }
            return this;
        }


        @Contract(value = "_ -> this", mutates = "this")
        public CustomAuditLogEntryBuilder setAttachmentUrls(final List<String> attachmentUrls) {
            this.attachmentUrls = attachmentUrls;
            return this;
        }

        @NotNull
        @Contract(" -> new")
        public CustomAuditLogEntry createAuditLogEntry() {
            if (0L == target) {
                throw new IllegalArgumentException("Target shall not be 0! You shall not pass!");
            }

            Objects.requireNonNull(type, "Type may not be null!");

            return new CustomAuditLogEntry(0L, author, target, reason, type, expireTime, creationTime, Collections.unmodifiableList(attachmentUrls));
        }


        @NonNls
        @NotNull
        @Override
        @Contract(pure = true)
        public String toString() {
            return "CustomAuditLogEntryBuilder{" +
                    "caseId=" + caseId +
                    ", author=" + author +
                    ", target=" + target +
                    ", reason='" + reason + '\'' +
                    ", type=" + type +
                    ", expireTime=" + expireTime +
                    ", creationTime=" + creationTime +
                    ", attachmentUrls=" + attachmentUrls +
                    '}';
        }
    }

    @NonNls
    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return "CustomAuditLogEntry{" +
                "caseId=" + caseId +
                ", author=" + author +
                ", target=" + target +
                ", reason='" + reason + '\'' +
                ", type=" + type +
                ", expireTime=" + expireTime +
                ", creationTime=" + creationTime +
                ", attachmentUrls=" + attachmentUrls +
                '}';
    }
}

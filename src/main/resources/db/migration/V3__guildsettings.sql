create table guild_settings
(
    guild_id            BIGINT not null,
    reports_log_channel BIGINT null
);

create unique index guild_settings_guild_id_uindex
    on guild_settings (guild_id);

alter table guild_settings
    add constraint guild_settings_pk
        primary key (guild_id);
create unique index guild_settings_guild_id_uindex
    on guild_settings (guild_id);

alter table guild_settings
    add constraint guild_settings_pk
        primary key (guild_id);
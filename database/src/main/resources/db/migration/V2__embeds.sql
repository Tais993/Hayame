create table embeds
(
    id               varchar(256) PRIMARY KEY,
    timestamp        boolean     null,
    author_name      varchar(256)           null,
    author_url       varchar(1048) null,
    author_icon_url  varchar(1048) null,
    colour           int           null,
    footer_url       varchar(1048) null,
    image_url        varchar(1048) null,
    thumbnail_url    varchar(1048) null,
    who_what_to_ping varchar(256)  null
);


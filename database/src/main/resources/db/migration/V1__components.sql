CREATE TABLE IF NOT EXISTS component
(
    id          int AUTO_INCREMENT
        PRIMARY KEY,
    listener_id varchar(50)   null,
    expire_date datetime      null,
    arguments   varchar(2000) null,
    CONSTRAINT component_id_uindex
        UNIQUE (id)
);
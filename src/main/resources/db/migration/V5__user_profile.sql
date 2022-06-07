CREATE TABLE discordbot.user_profiles
(
    id BIGINT PRIMARY KEY,

    description VARCHAR(2048),
    field_one_name    VARCHAR(256),
    field_one_content    VARCHAR(1024),
    field_two_name    VARCHAR(256),
    field_two_content    VARCHAR(1024),
    field_three_name    VARCHAR(256),
    field_three_content    VARCHAR(1024)
);
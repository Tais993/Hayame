CREATE TABLE user_socials
(
    id BIGINT NOT NULL,

    platform_name VARCHAR(50),
    platform_icon VARCHAR(50),
    platform_user_url VARCHAR(50),
    platform_user_name VARCHAR(50),

    FOREIGN KEY (id)
        REFERENCES user_profiles(id)
);
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS alert_day_tb;

DROP TABLE IF EXISTS archive_tb;

DROP TABLE IF EXISTS favorite_artist_tb;

DROP TABLE IF EXISTS favorite_genre_tb;

DROP TABLE IF EXISTS favorite_track_tb;

DROP TABLE IF EXISTS following_tb;

DROP TABLE IF EXISTS history_tb;

DROP TABLE IF EXISTS playlist_tb;

DROP TABLE IF EXISTS playlist_track_tb;

DROP TABLE IF EXISTS recommend_tb;

DROP TABLE IF EXISTS refresh_token_tb;

DROP TABLE IF EXISTS track_artist_tb;

DROP TABLE IF EXISTS track_tb;

DROP TABLE IF EXISTS track_play_count_tb;

DROP TABLE IF EXISTS user_detail_tb;

DROP TABLE IF EXISTS user_tb;

SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE user_detail_tb
(
    id                   BIGINT  NOT NULL AUTO_INCREMENT,
    sex                  VARCHAR(10),
    age                  INT ,
    recommend_time       TIME,
    name                 VARCHAR(10),
    nickname             VARCHAR(10),
    profile_image_url    VARCHAR(50),
    popup_alert_flag     VARCHAR(10) NOT NULL,
    favorite_public_flag VARCHAR(10) NOT NULL,
    calender_public_flag VARCHAR(10) NOT NULL,
    all_public_flag      VARCHAR(10) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE user_tb
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    email    VARCHAR(100) not null,
    password VARCHAR(50),
    role     VARCHAR(255),
    type     VARCHAR(255),
    del_flag varchar(255),
    user_detail_id BIGINT ,
    FOREIGN KEY (user_detail_id) REFERENCES user_detail_tb (id),
    PRIMARY KEY (id)
);


CREATE TABLE playlist_tb
(
    id               BIGINT  NOT NULL AUTO_INCREMENT,
    playlist_title   VARCHAR(100),
    alarm_start_time TIME,
    alarm_flag       BOOLEAN,
    list_sequence    INTEGER,
    del_flag         VARCHAR(1),
    user_id          BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user_tb (id)
);

CREATE TABLE alert_day_tb
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    alarm_day   SMALLINT,
    playlist_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (playlist_id) REFERENCES playlist_tb (id)
);

CREATE TABLE track_tb
(
    id                 BIGINT NOT NULL AUTO_INCREMENT,
    track_title        VARCHAR(30),
    duration_time_ms   INTEGER,
    album_image_url    VARCHAR(50),
    spotify_track_id   VARCHAR(50),
    spotify_track_href VARCHAR(50),
    PRIMARY KEY (id)
);

CREATE TABLE playlist_track_tb
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    play_sequence       INTEGER,
    play_flag           BOOLEAN,
    track_alarm_flag    BOOLEAN,
    playlist_id         BIGINT,
    track_id            BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (playlist_id) REFERENCES playlist_tb (id),
    FOREIGN KEY (track_id) REFERENCES track_tb (id)
);

CREATE TABLE archive_tb
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    content     TEXT,
    public_flag BOOLEAN,
    created_at  DATETIME,
    user_id     BIGINT,
    playlist_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (playlist_id) REFERENCES playlist_tb (id),
    FOREIGN KEY (user_id) REFERENCES user_tb (id)
);

CREATE TABLE favorite_artist_tb
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    artist_name      VARCHAR(50),
    artist_image_url VARCHAR(50),
    user_id          BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user_tb (id)
);

CREATE TABLE favorite_genre_tb
(
    id              BIGINT      NOT NULL auto_increment,
    genre_name      VARCHAR(45) NOT NULL,
    genre_image_url VARCHAR(50),
    user_id         BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user_tb (id)
);

CREATE TABLE favorite_track_tb
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    play_count    INTEGER,
    favorite_flag BOOLEAN,
    user_id       BIGINT,
    track_id      BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user_tb (id),
    FOREIGN KEY (track_id) REFERENCES track_tb (id)
);

CREATE TABLE following_tb
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    block_flag BOOLEAN,
    user_from  BIGINT,
    user_to    BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_from) REFERENCES user_tb (id),
    FOREIGN KEY (user_to) REFERENCES user_tb (id)
);

CREATE TABLE recommend_tb
(
    id             BIGINT NOT NULL AUTO_INCREMENT,
    recommend_type VARCHAR(15),
    comment        TEXT,
    playlist_id    BIGINT,
    from_user_id BIGINT,
    to_user_id   BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (playlist_id) REFERENCES playlist_tb (id),
    FOREIGN KEY (from_user_id) REFERENCES user_tb (id),
    FOREIGN KEY (to_user_id) REFERENCES user_tb (id)
);

CREATE TABLE track_artist_tb
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    artist_name VARCHAR(30),
    track_id    BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (track_id) REFERENCES track_tb (id)
);

CREATE TABLE history_tb
(
    id             BIGINT NOT NULL AUTO_INCREMENT,
    search_history VARCHAR(50),
    del_flag       VARCHAR(1),
    user_id        BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user_tb (id)
);

CREATE TABLE track_play_count_tb
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    play_count       INTEGER DEFAULT 1,
    spotify_track_id VARCHAR(255),
    user_id          BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user_tb (id)
);

CREATE TABLE refresh_token_tb
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    token     VARCHAR(255) NOT NULL,
    key_email VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

insert into user_tb (email , password , type , role) values ("testEmail" , "password" , "GENERAL_USER" , "USER");

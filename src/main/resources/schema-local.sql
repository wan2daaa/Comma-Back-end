SET foreign_key_checks = 0;

drop table if exists archive_tb;

drop table if exists favorite_artist_tb;

drop table if exists favorite_genre_tb;

drop table if exists following_tb;

drop table if exists playlist_tb;

drop table if exists recommend_tb;

drop table if exists t_refresh_token;

drop table if exists track_tb;

drop table if exists user_tb;

SET foreign_key_checks = 1;

create table user_tb
(
    id             bigint       not null auto_increment,
    email          varchar(100),
    name           varchar(10),
    sex            varchar(10),
    age            integer,
    nickname       varchar(255),
    password       varchar(50),
    recommend_time time,
    role           varchar(255),
    type           varchar(255),
    sound_flag     TINYINT(1),
    vibrate_flag   TINYINT(1),
    leaved_flag    TINYINT(1),
    created_at     datetime(6),
    updated_at     datetime(6),
    del_flag       TINYINT(1),
    primary key (id)
);

create table playlist_tb
(
    id             bigint not null auto_increment,
    user_id        bigint,
    alarm_day      smallint,
    alarm_flag     bit,
    alarm_time     time,
    playlist_title varchar(255),
    created_at     datetime(6),
    updated_at     datetime(6),
    del_flag       TINYINT(1),
    primary key (id),
    foreign key (user_id) references user_tb (id)
);

create table track_tb
(
    id              bigint not null auto_increment,
    playlist_id       bigint,
    album_flag      TINYINT(1),
    album_image_url varchar(255),
    album_name      varchar(255),
    artist_names    varchar(255),
    duration_ms     integer,
    track_title     varchar(255),
    created_at      datetime(6),
    updated_at      datetime(6),
    del_flag       TINYINT(1),
    primary key (id),
    foreign key (playlist_id) references playlist_tb (id)
);

create table archive_tb
(
    id          bigint not null auto_increment,
    content     TEXT,
    playlist_id bigint,
    user_id     bigint,
    created_at     datetime(6),
    updated_at     datetime(6),
    del_flag       TINYINT(1),
    primary key (id),
    foreign key (playlist_id) references playlist_tb (id),
    foreign key (user_id) references user_tb (id)
);

create table favorite_artist_tb
(
    id          bigint      not null auto_increment,
    artist_name varchar(45) not null,
    user_id     bigint,
    created_at     datetime(6),
    updated_at     datetime(6),
    del_flag       TINYINT(1),
    primary key (id),
    foreign key (user_id) references user_tb (id)
);

create table favorite_genre_tb
(
    id                 bigint      not null auto_increment,
    genre_name         varchar(45) not null,
    user_id            bigint,
    created_at     datetime(6),
    updated_at     datetime(6),
    del_flag       TINYINT(1),
    primary key (id),
    foreign key (user_id) references user_tb(id)
);

create table following_tb
(
    id              bigint not null auto_increment,
    user_email_to   bigint,
    user_email_from bigint,
    block_flag      TINYINT(1),
    created_at     datetime(6),
    updated_at     datetime(6),
    del_flag       TINYINT(1),
    primary key (id),
    foreign key (user_email_to) references user_tb (id),
    foreign key (user_email_from) references user_tb (id)
);



create table recommend_tb
(
    id             bigint       not null auto_increment,
    playlist_id    bigint,
    recommend_from bigint,
    recommend_to   bigint,
    comment        TEXT,
    play_count     integer default 0,
    recommend_type varchar(255) not null,
    created_at     datetime(6),
    updated_at     datetime(6),
    del_flag       TINYINT(1),
    primary key (id),
    foreign key (playlist_id) references playlist_tb (id),
    foreign key (recommend_from) references recommend_tb (id),
    foreign key (recommend_to) references recommend_tb (id)
);

create table t_refresh_token
(
    refresh_token_id bigint       not null auto_increment,
    key_email        varchar(255) not null,
    refresh_token    varchar(255) not null,
    primary key (refresh_token_id)
);




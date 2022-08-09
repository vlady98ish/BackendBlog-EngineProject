create TABLE captcha_code
(
    id          INT      NOT NULL,
    code        TINYTEXT NOT NULL,
    secret_code TINYTEXT NOT NULL,
    time        datetime NOT NULL,
    CONSTRAINT pk_captcha_code PRIMARY KEY (id)
);

create TABLE global_settings
(
    id                   INT    NOT NULL,
    multiuser_mode       BIT(1) NOT NULL,
    post_pre_moderation  BIT(1) NOT NULL,
    statistics_is_public BIT(1) NOT NULL,
    CONSTRAINT pk_global_settings PRIMARY KEY (id)
);

create TABLE post_comments
(
    id        INT AUTO_INCREMENT NOT NULL,
    text      TEXT               NOT NULL,
    time      datetime           NOT NULL,
    parent_id INT                NULL,
    post_id   INT                NULL,
    user_id   INT                NULL,
    CONSTRAINT pk_post_comments PRIMARY KEY (id)
);

create TABLE post_votes
(
    id      INT AUTO_INCREMENT NOT NULL,
    time    datetime           NOT NULL,
    value   TINYINT            NOT NULL,
    post_id INT                NULL,
    user_id INT                NULL,
    CONSTRAINT pk_post_votes PRIMARY KEY (id)
);

create TABLE posts
(
    id                INT AUTO_INCREMENT NOT NULL,
    is_active         TINYINT            NOT NULL,
    moderation_status VARCHAR(255)       NOT NULL,
    text              TEXT               NOT NULL,
    time              datetime           NOT NULL,
    title             TEXT               NOT NULL,
    view_count        INT                NOT NULL,
    user_id           INT                NOT NULL,
    moderator_id      INT                NULL,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);

create TABLE tag2post
(
    post_id INT NOT NULL,
    tag_id  INT NOT NULL
);

create TABLE tags
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_tags PRIMARY KEY (id)
);

create TABLE users
(
    id           INT AUTO_INCREMENT NOT NULL,
    is_moderator TINYINT            NOT NULL,
    reg_time     datetime           NOT NULL,
    name         VARCHAR(255)       NOT NULL,
    email        VARCHAR(255)       NOT NULL,
    password     VARCHAR(255)       NOT NULL,
    code         VARCHAR(255)       NULL,
    photo        TEXT               NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

alter table posts
    add CONSTRAINT FK_POSTS_ON_MODERATOR FOREIGN KEY (moderator_id) REFERENCES users (id);

alter table posts
    add CONSTRAINT FK_POSTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

alter table post_comments
    add CONSTRAINT FK_POST_COMMENTS_ON_PARENT FOREIGN KEY (parent_id) REFERENCES post_comments (id);

alter table post_comments
    add CONSTRAINT FK_POST_COMMENTS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

alter table post_comments
    add CONSTRAINT FK_POST_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

alter table post_votes
    add CONSTRAINT FK_POST_VOTES_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

alter table post_votes
    add CONSTRAINT FK_POST_VOTES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

alter table tag2post
    add CONSTRAINT fk_tag2post_on_post FOREIGN KEY (post_id) REFERENCES posts (id);

alter table tag2post
    add CONSTRAINT fk_tag2post_on_tag FOREIGN KEY (tag_id) REFERENCES tags (id);
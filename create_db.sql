CREATE DATABASE taskmanager;
USE taskmanager;

create table activities (
    actor_id bigint not null,
    board_id bigint not null,
    card_id bigint,
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    updated_at datetime(6) not null,
    version bigint not null,
    payload varchar(4000),
    type enum ('BOARD_CREATED','CARD_ARCHIVED','CARD_CREATED','CARD_MOVED','CARD_UPDATED','CHECKLIST_ITEM_TOGGLED','COMMENT_ADDED','LIST_CREATED','MEMBER_ADDED') not null,
    primary key (id)
) engine=InnoDB;

create table board_lists (
    archived bit not null,
    board_id bigint not null,
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    position bigint not null,
    updated_at datetime(6) not null,
    version bigint not null,
    name varchar(120) not null,
    primary key (id)
) engine=InnoDB;

create table board_members (
    board_id bigint not null,
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    joined_at datetime(6) not null,
    updated_at datetime(6) not null,
    user_id bigint not null,
    version bigint not null,
    role enum ('MEMBER','OWNER','VIEWER') not null,
    primary key (id)
) engine=InnoDB;

create table boards (
    archived bit not null,
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    owner_id bigint not null,
    updated_at datetime(6) not null,
    version bigint not null,
    name varchar(150) not null,
    description varchar(1000),
    visibility enum ('PRIVATE','PUBLIC','WORKSPACE') not null,
    primary key (id)
) engine=InnoDB;

create table card_comments (
    author_id bigint not null,
    card_id bigint not null,
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    updated_at datetime(6) not null,
    version bigint not null,
    content varchar(2000) not null,
    primary key (id)
) engine=InnoDB;

create table card_labels (
    card_id bigint not null,
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    label_id bigint not null,
    updated_at datetime(6) not null,
    version bigint not null,
    primary key (id)
) engine=InnoDB;

create table card_members (
    card_id bigint not null,
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    updated_at datetime(6) not null,
    user_id bigint not null,
    version bigint not null,
    primary key (id)
) engine=InnoDB;

create table cards (
    archived bit not null,
    due_date date,
    start_date date,
    board_id bigint not null,
    created_at datetime(6) not null,
    created_by bigint not null,
    id bigint not null auto_increment,
    list_id bigint not null,
    position bigint not null,
    updated_at datetime(6) not null,
    version bigint not null,
    cover_color varchar(20),
    title varchar(200) not null,
    description varchar(4000),
    primary key (id)
) engine=InnoDB;

create table labels (
    board_id bigint not null,
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    updated_at datetime(6) not null,
    version bigint not null,
    color varchar(20) not null,
    name varchar(50) not null,
    primary key (id)
) engine=InnoDB;

create table users (
    active bit not null,
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    updated_at datetime(6) not null,
    version bigint not null,
    username varchar(50) not null,
    email varchar(100) not null,
    full_name varchar(100) not null,
    password_hash varchar(255) not null,
    system_role enum ('ADMIN','USER') not null,
    primary key (id)
) engine=InnoDB;

create index idx_activity_board_created
   on activities (board_id, created_at);

create index idx_activity_card_created
   on activities (card_id, created_at);

create index idx_activity_actor_created
   on activities (actor_id, created_at);

create index idx_board_list_board_pos
   on board_lists (board_id, position);

create index idx_board_member_board
   on board_members (board_id);

create index idx_board_member_user
   on board_members (user_id);

alter table board_members
   add constraint UKdip0gtav4cxokql76sf0981t4 unique (board_id, user_id);

create index idx_board_owner
   on boards (owner_id);

create index idx_board_visibility
   on boards (visibility);

create index idx_card_comment_card
   on card_comments (card_id);

create index idx_card_comment_author
   on card_comments (author_id);

create index idx_card_label_card
   on card_labels (card_id);

create index idx_card_label_label
   on card_labels (label_id);

alter table card_labels
   add constraint UKc0qd3ywn86je6kco1t35eyv07 unique (card_id, label_id);

create index idx_card_member_card
   on card_members (card_id);

create index idx_card_member_user
   on card_members (user_id);

alter table card_members
   add constraint UKdxgs7sla9ad87g1b1xnlw2t3y unique (card_id, user_id);

create index idx_card_board_list_pos
   on cards (board_id, list_id, position);

create index idx_card_due_date
   on cards (due_date);

create index idx_card_created_by
   on cards (created_by);

create index idx_label_board
   on labels (board_id);

alter table labels
   add constraint UK7dmu6fqo8oylf2xw1wlc5dyuf unique (board_id, name);

alter table users
   add constraint idx_user_username unique (username);

alter table users
   add constraint idx_user_email unique (email);

alter table activities
   add constraint FKmfjrc8jdvy0yrr7x67qmrxkue
   foreign key (actor_id)
   references users (id);

alter table activities
   add constraint FKnju5v17qjhtup894irq03w4ij
   foreign key (board_id)
   references boards (id);

alter table activities
   add constraint FKh20algdvtxecmx04p0marhay8
   foreign key (card_id)
   references cards (id);

alter table board_lists
   add constraint FKnnhqguyrw91k14c7gqbkj1r3d
   foreign key (board_id)
   references boards (id);

alter table board_members
   add constraint FKfqm0ki2w8yabmxwctnct8sb91
   foreign key (board_id)
   references boards (id);

alter table board_members
   add constraint FK80hd8sx9wrhibcfwv37pvmxb6
   foreign key (user_id)
   references users (id);

alter table boards
   add constraint FKbng8kmryb5aa0r8p2yq4x2l5l
   foreign key (owner_id)
   references users (id);

alter table card_comments
   add constraint FKfax7uwvbkbc9kjkb1uckk1ysw
   foreign key (author_id)
   references users (id);

alter table card_comments
   add constraint FK56yn9p41ijrjb2ujighsvms06
   foreign key (card_id)
   references cards (id);

alter table card_labels
   add constraint FKo589elw417q3d7d7bbvooenm4
   foreign key (card_id)
   references cards (id);

alter table card_labels
   add constraint FKjtuk7wmotl3wjflqgnk5tve2y
   foreign key (label_id)
   references labels (id);

alter table card_members
   add constraint FKkfc4clyo8lw6mraqme1ax3pk2
   foreign key (card_id)
   references cards (id);

alter table card_members
   add constraint FKct2y3d055f305miv95mvtuxl9
   foreign key (user_id)
   references users (id);

alter table cards
   add constraint FKk0nnnx4q6pmiiwp0u5i26vhlm
   foreign key (board_id)
   references boards (id);

alter table cards
   add constraint FK2qgc639ele5paxfuc3gru1ljk
   foreign key (created_by)
   references users (id);

alter table cards
   add constraint FKpp84tabsxkkahdynsapvc5ms5
   foreign key (list_id)
   references board_lists (id);

alter table labels
   add constraint FK2cqcyvlx9wc7pn9vr6n6uqops
   foreign key (board_id)
   references boards (id);
insert ignore into users (id, created_at, updated_at, version, username, email, password_hash, full_name, system_role, active) values (1, now(), now(), 0, 'demo_admin', 'admin@demo.local', '$2a$10$demo.hash.value', 'Demo Admin', 'ADMIN', true);
insert ignore into users (id, created_at, updated_at, version, username, email, password_hash, full_name, system_role, active) values (2, now(), now(), 0, 'demo_user', 'user@demo.local', '$2a$10$demo.hash.value', 'Demo User', 'USER', true);
insert ignore into boards (id, created_at, updated_at, version, archived, name, description, visibility, owner_id) values (1, now(), now(), 0, false, 'Product Roadmap', 'Demo board for Trello-style workflow', 'PRIVATE', 1);
insert ignore into board_members (id, created_at, updated_at, version, joined_at, role, board_id, user_id) values (1, now(), now(), 0, now(), 'OWNER', 1, 1);
insert ignore into board_members (id, created_at, updated_at, version, joined_at, role, board_id, user_id) values (2, now(), now(), 0, now(), 'MEMBER', 1, 2);
insert ignore into board_lists (id, created_at, updated_at, version, archived, name, position, board_id) values (1, now(), now(), 0, false, 'To Do', 1000, 1);
insert ignore into board_lists (id, created_at, updated_at, version, archived, name, position, board_id) values (2, now(), now(), 0, false, 'Doing', 2000, 1);
insert ignore into board_lists (id, created_at, updated_at, version, archived, name, position, board_id) values (3, now(), now(), 0, false, 'Done', 3000, 1);
insert ignore into labels (id, created_at, updated_at, version, color, name, board_id) values (1, now(), now(), 0, 'green', 'backend', 1);
insert ignore into labels (id, created_at, updated_at, version, color, name, board_id) values (2, now(), now(), 0, 'blue', 'frontend', 1);
insert ignore into cards (id, created_at, updated_at, version, archived, cover_color, description, due_date, position, start_date, title, board_id, created_by, list_id) values (1, now(), now(), 0, false, 'blue', 'Design board-state API response', current_date + interval 3 day, 1000, current_date, 'Implement board-state endpoint', 1, 1, 1);
insert ignore into cards (id, created_at, updated_at, version, archived, cover_color, description, due_date, position, start_date, title, board_id, created_by, list_id) values (2, now(), now(), 0, false, 'green', 'Wire board UI to GET /boards/{id}/board-state', current_date + interval 5 day, 1000, current_date, 'Connect frontend to board API', 1, 2, 2);
insert ignore into card_members (id, created_at, updated_at, version, card_id, user_id) values (1, now(), now(), 0, 1, 1);
insert ignore into card_members (id, created_at, updated_at, version, card_id, user_id) values (2, now(), now(), 0, 2, 2);
insert ignore into card_labels (id, created_at, updated_at, version, card_id, label_id) values (1, now(), now(), 0, 1, 1);
insert ignore into card_labels (id, created_at, updated_at, version, card_id, label_id) values (2, now(), now(), 0, 2, 2);
insert ignore into card_comments (id, created_at, updated_at, version, content, author_id, card_id) values (1, now(), now(), 0, 'Initial demo comment on card', 1, 1);
insert ignore into activities (id, created_at, updated_at, version, payload, type, actor_id, board_id, card_id) values (1, now(), now(), 0, '{"message":"Board initialized with demo data"}', 'BOARD_CREATED', 1, 1, null);
insert ignore into activities (id, created_at, updated_at, version, payload, type, actor_id, board_id, card_id) values (2, now(), now(), 0, '{"message":"Created first card"}', 'CARD_CREATED', 1, 1, 1);

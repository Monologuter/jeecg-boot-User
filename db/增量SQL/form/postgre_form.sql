drop database if exists form;
create database form;

\connect form

drop table if exists fd_form;
create table fd_form
(
    id                    varchar(32)  not null,
    code                  varchar(100) not null,
    name                  varchar(100) not null,
    json                  jsonb,
    department            varchar(100) default null,
    dynamic_data_source   text,
    auto_count_collection text         default null,
    is_template           bool         default false,
    create_by             varchar(100) default null,
    update_by             varchar(100) default null,
    create_time           timestamp    default null,
    update_time           timestamp    default null,
    del_flag              bool         default false,
    primary key (id),
    unique (code, is_template)
);

drop table if exists fd_form_data;
create table fd_form_data
(
    id          varchar(32) not null,
    form_id     varchar(32) not null,
    row_data    jsonb,
    create_by   varchar(100) default null,
    update_by   varchar(100) default null,
    create_time timestamp    default null,
    update_time timestamp    default null,
    del_flag    bool         default false,
    primary key (id)
);

drop table if exists fd_form_sys_permission;
create table fd_form_sys_permission
(
    id                varchar(32) not null,
    form_id           varchar(32) not null,
    sys_permission_id varchar(32) not null,
    fields            text,
    searches          text,
    create_by         varchar(100) default null,
    update_by         varchar(100) default null,
    create_time       timestamp    default null,
    update_time       timestamp    default null,
    del_flag          bool         default false,
    primary key (id)
);

drop table if exists fd_form_style;
create table fd_form_style
(
    id          varchar(32)  not null,
    name        varchar(100) not null,
    content     text         default null,
    code        varchar(100) not null,
    type        varchar(100) default null,
    create_by   varchar(100) default null,
    update_by   varchar(100) default null,
    create_time timestamp    default null,
    update_time timestamp    default null,
    del_flag    bool         default false,
    primary key (id)
);

drop table if exists fd_form_style_mapping;
create table fd_form_style_mapping
(
    id       varchar(32) not null,
    form_id  varchar(32) not null,
    style_id varchar(32) not null,
    primary key (id)
);

drop table if exists fd_form_role;
create table fd_form_role
(
    id         varchar(32)  not null,
    form_id    varchar(32)  not null,
    role_id    varchar(32)  not null,
    rule_key   varchar(100) not null,
    rule_value varchar(100) not null,
    primary key (id)
);

drop table if exists fd_form_button;
create table fd_permission_button
(
    id            varchar(32) not null,
    name          varchar(32),
    permission_id varchar(32) not null,
    type          integer     not null,
    info          jsonb,
    icon          varchar(32),
    color         varchar(32),
    primary key (id)
);

drop table if exists fd_permission_rule;
create table fd_permission_rule
(
    id         varchar(32)  not null,
    permission_id    varchar(32)  not null,
    rule_key   varchar(100) not null,
    rule_value varchar(100) not null,
    primary key (id)
);
create sequence if not exists client_seq start 1;

create table if not exists client
(
    id       bigint       not null default nextval('client_seq'::regclass),
    username varchar(255) not null,
    password varchar(255) not null,
    email    varchar(255) not null,
    role     varchar(255) not null,
    constraint pk_client primary key (id),
    constraint uq_client_username unique (username),
    constraint uq_client_email unique (email)
);

create sequence if not exists card_seq start 1;

create table if not exists card
(
    id              bigint       not null default nextval('card_seq'::regclass),
    number          text         not null,
    owner_id        bigint       not null,
    validity_period varchar(255),
    status          varchar(255) not null,
    balance         bigint       not null default 0,
    constraint pk_card primary key (id),
    constraint fk_card_owner foreign key (owner_id) references client (id)
);

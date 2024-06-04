create table IF NOT EXISTS "gruppe"
(
    id      serial primary key,
    name    varchar(250),
    is_open bool
);

create table IF NOT EXISTS "ausgabe"
(
    id           serial primary key,
    beschreibung varchar(250),
    geldgeber    varchar(250),
    geld         integer,
    gruppe       integer references "gruppe" (id),
    gruppe_key   integer
);

create table IF NOT EXISTS "profiteure"
(
    name    varchar(250),
    ausgabe integer references "ausgabe" (id)
);

create table IF NOT EXISTS "teilnehmer"
(
    name   varchar(250),
    gruppe integer references "gruppe" (id)
);



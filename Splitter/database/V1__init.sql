create table gruppe
(
    id      serial primary key,
    name    varchar(250),
    is_open bool
);

create table ausgabe
(
    id           serial primary key,
    beschreibung varchar(250),
    geldgeber    varchar(250),
    geld         integer,
    gruppe       integer references gruppe (id),
    gruppe_key   integer
);

create table profiteure
(
    name    varchar(250),
    ausgabe integer references ausgabe (id)
);

create table teilnehmer
(
    name   varchar(250),
    gruppe integer references gruppe (id)
);



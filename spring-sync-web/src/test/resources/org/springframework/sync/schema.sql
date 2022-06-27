create table Todo
(
    id          int not null default -1,
    description varchar(2000),
    complete    boolean
);

insert into Todo (id, description, complete)
values (1, 'A', false);
insert into Todo (id, description, complete)
values (2, 'B', false);
insert into Todo (id, description, complete)
values (3, 'C', false);

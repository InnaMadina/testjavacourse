create table equation
(
    id   int auto_increment primary key,
    text varchar(200) not null,
    constraint equation_text_uindex unique (text)
);

create table root
(
    id          int auto_increment primary key,
    value       double not null,
    equation_id int    not null,
    constraint root_equation_id_fk foreign key (equation_id) references equation (id)
);
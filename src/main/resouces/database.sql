use chadchat;

create table users (
    id INT primary key AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL
);

insert into users (name) values ("Chad");

create table chatlog(
    id int primary key auto_increment,
    time timestamp not null default (now()),
    user_id int not null,
    message VARCHAR(255) not null,
    foreign key (time) references users (id)
);
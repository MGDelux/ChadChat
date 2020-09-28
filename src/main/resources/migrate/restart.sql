drop database if exists chadchat;
drop user if exists 'chadchat'@'localhost';

create database chadchat;
create user 'chadchat'@'localhost';

grant all privileges on chadchat.* to 'chadchat'@'localhost';
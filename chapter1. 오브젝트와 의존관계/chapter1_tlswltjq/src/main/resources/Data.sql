delete from users where name ='백기선';
create table users(
    id varchar(10) primary key ,
    name varchar(20) not null ,
    password varchar(10) not null
)

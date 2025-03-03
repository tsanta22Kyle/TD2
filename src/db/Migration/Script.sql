create role gastropizza with login password 'tsanta' ;

drop database stockManager ;
create database stockmanager ;

GRANT all on DATABASE stockmanager to gastropizza ;

ALTER DATABASE stockmanager owner to gastropizza;
\c stockmanager gastropizza;

CREATE TYPE unit AS ENUM(
    'G','L','U'
    );

CREATE TABLE dish(
    dish_id varchar primary key ,
    name varchar(200) ,
    unit_price int
);
CREATE TABLE ingredient(
    ingredient_id varchar primary key ,
    name varchar(200),
    update_datetime TIMESTAMP
);
CREATE TABLE dish_ingredient(
    dish_id varchar  ,
    ingredient_id varchar ,
    required_quantity float,
    unit unit,
    CONSTRAINT dish_fk FOREIGN KEY (dish_id) REFERENCES dish(dish_id),
    CONSTRAINT ingredient_fk FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id)
);
CREATE TABLE price(
    price_id varchar PRIMARY KEY ,
    value float,
    ingredient_id varchar,
    date date,
    unit unit,
    unique (ingredient_id,date)

);

CREATE VIEW latests AS SELECT i.ingredient_id,MAX(date) latest from price join ingredient i on i.ingredient_id=price.ingredient_id group by i.ingredient_id  ;

CREATE TYPE moveType AS ENUM('outComing','inComing');

CREATE TABLE stock_move(
    ingredient_id varchar,
    moveType moveType,
    quantity float ,
    unit unit,
    move_date timestamp,
    CONSTRAINT ingredient_fk FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id)
);

REVOKE UPDATE ON stock_move FROM public;

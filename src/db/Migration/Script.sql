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
    move_date timestamp default current_timestamp,
    CONSTRAINT ingredient_fk FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id)
);

REVOKE UPDATE ON stock_move FROM public;
CREATE TABLE "order"(
    reference varchar unique ,
    order_time timestamp default current_timestamp,
    order_id varchar primary key
);

CREATE TABLE dish_order(
    id varchar primary key ,
    order_id varchar ,
    dish_id varchar,
    quantity int ,
    CONSTRAINT order_fk FOREIGN KEY (order_id) REFERENCES "order"(order_id) ,
    CONSTRAINT dish_fk FOREIGN KEY (dish_id) REFERENCES dish(dish_id)
);
CREATE TYPE order_advancement AS ENUM(
    'CREATED','CONFIRMED','IN_PREPARATION','FINISHED','SERVED'
    );

CREATE TABLE order_status(
    id varchar primary key ,
    order_id varchar,
    order_status order_advancement,
    order_status_datetime timestamp default current_timestamp,
    CONSTRAINT order_id FOREIGN KEY (order_id) REFERENCES "order"(order_id)
);
CREATE TABLE dish_order_status(
  id varchar primary key ,
  dish_order_id varchar,
  order_status order_status,
  do_datetime timestamp,
  CONSTRAINT dish_order_id FOREIGN KEY (dish_order_id) REFERENCES dish_order(id)
);





create database pb charset=utf8;

use pb;

SET NAMES 'utf8';

CREATE USER pb@localhost IDENTIFIED BY '***';
grant all privileges on *.* to pb@localhost with grant option;
FLUSH PRIVILEGES;


create table author(id int auto_increment primary key, nickname varchar(50));

create table post(id int auto_increment primary key, refid int, title varchar(200), 
	description text, content text, 
	f_author int, foreign key (f_author) references author(id), 
	ts timestamp default CURRENT_TIMESTAMP);

create index post_refid_index on post (refid);
create index post_author_index on post (f_author);

-- test data
insert into pb.author (nickname) values ('anonymous');
insert into pb.author (nickname) values ('ngoro');
insert into pb.author (nickname) values ('alex');
insert into pb.author (nickname) values ('test');


insert into pb.post (title, description, content) values ('test 1', 'no description', '(ns xml-test
   (:use [clojure.test]
         [clojure.data.zip.xml])
   (:require [clojure.xml :as xml]
             [clojure.zip :as zip]))
');

insert into pb.post (title, description, f_author, content) values ('Pascal\'s Trapezoid', 'tiltec\'s solution to Pascal\'s Trapezoid', 1, ';; https://4clojure.com/problem/147

(fn [start] (iterate #(map + (cons 0 %) (concat % [0])) (map bigint start)))
;iterate #(vec (map + (into [0] %) (conj % 0)))');
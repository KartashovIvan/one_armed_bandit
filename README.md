# H1 Для запуска приложения нужно:
#### 1 - создать БД:
```
create database one_armed_bandit;
```
#### 2 - Создать таблицы в БД
```
create table if not exists finance_result(income numeric(9,2), outcome numeric(9,2));
create table if not exists game (id serial primary key, sym_first char, sym_second char, sym_third char);
```
#### 3 - Создать пользователя student с паролем student в БД

-- Script BD Parcial Sumo
-- Ejecutar en phpMyAdmin o MySQL Workbench

CREATE DATABASE IF NOT EXISTS sumo_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sumo_db;

DROP TABLE IF EXISTS rikishi;

CREATE TABLE rikishi (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100)  NOT NULL,
    peso      DOUBLE        NOT NULL,
    victorias INT           NOT NULL DEFAULT 0,
    kimarites TEXT          NOT NULL,
    participo TINYINT(1)    NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

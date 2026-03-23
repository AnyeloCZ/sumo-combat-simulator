-- Script de creacion de la base de datos del Parcial Sumo
-- Universidad Distrital Francisco Jose de Caldas
-- Motor: MySQL 8.x

CREATE DATABASE IF NOT EXISTS sumo_db;
USE sumo_db;

CREATE TABLE IF NOT EXISTS rikishi (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    peso      DOUBLE NOT NULL,
    victorias INT DEFAULT 0,
    kimarites TEXT NOT NULL,
    participo BOOLEAN DEFAULT FALSE
);

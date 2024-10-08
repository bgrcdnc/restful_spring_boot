DROP DATABASE IF EXISTS restdb;
DROP USER IF EXISTS 'restadmin'@'localhost';
CREATE DATABASE IF NOT EXISTS restdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'restadmin'@'localhost' IDENTIFIED BY 'mypass';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW,
    CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON *.* TO 'restadmin'@'*';
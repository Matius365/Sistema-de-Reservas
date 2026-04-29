-- Crear y usar base de datos
CREATE DATABASE IF NOT EXISTS reservas_db;
USE reservas_db;

-- Desactivar comprobaci�n de claves for�neas para borrar tablas
SET FOREIGN_KEY_CHECKS = 0;

-- Borrado de tablas si existen
DROP TABLE IF EXISTS reserva;
DROP TABLE IF EXISTS disponibleen;
DROP TABLE IF EXISTS administrador;
DROP TABLE IF EXISTS usuarionormal;
DROP TABLE IF EXISTS recurso;
DROP TABLE IF EXISTS horario;
DROP TABLE IF EXISTS usuario;

SET FOREIGN_KEY_CHECKS = 1;

-- ======================
-- CREACI�N DE TABLAS
-- ======================

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    tipo_usuario ENUM('Administrador','Normal') NOT NULL
);

CREATE TABLE administrador (
    id_usuario INT,
    telefono_guardia VARCHAR(20) NOT NULL,

    PRIMARY KEY(id_usuario),
    FOREIGN KEY(id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
);

CREATE TABLE usuarionormal (
    id_usuario INT,
    direccion VARCHAR(200),
    telefono_movil VARCHAR(20),
    fotografia VARCHAR(255),

    PRIMARY KEY(id_usuario),
    FOREIGN KEY(id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
);

CREATE TABLE recurso (
    id_recurso INT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(100),
    ubicacion VARCHAR(200),
    capacidad INT,

    PRIMARY KEY(id_recurso)
);

CREATE TABLE horario (
    id_horario INT,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio CHAR(5) NOT NULL,
    hora_fin CHAR(5) NOT NULL,

    PRIMARY KEY(id_horario),
    CHECK(dia_semana IN 
        ('Lunes','Martes','Miercoles','Jueves',
         'Viernes','Sabado','Domingo'))
);

CREATE TABLE disponibleen (
    id_recurso INT,
    id_horario INT,

    PRIMARY KEY(id_recurso,id_horario),
    FOREIGN KEY(id_recurso)
        REFERENCES recurso(id_recurso)
        ON DELETE CASCADE,
    FOREIGN KEY(id_horario)
        REFERENCES horario(id_horario)
        ON DELETE CASCADE
);

CREATE TABLE reserva (
    id_recurso INT,
    id_reserva_local INT,
    id_usuario INT,
    fecha DATE NOT NULL,
    hora_inicio CHAR(5) NOT NULL,
    hora_fin CHAR(5) NOT NULL,
    coste DECIMAL(10,2),
    numero_plazas INT,
    motivo VARCHAR(50),
    observaciones VARCHAR(50),

    PRIMARY KEY(id_recurso,id_reserva_local),
    FOREIGN KEY(id_recurso)
        REFERENCES recurso(id_recurso),
    FOREIGN KEY(id_usuario)
        REFERENCES usuarionormal(id_usuario)
);

-- ======================
-- INSERTS
-- ======================

INSERT INTO usuario VALUES 
(1,'adminjml@mail.com','12345678','Jose Mateo Lopez','1978-10-09','Administrador'),
(2,'admin2@mail.com','1234','ADMIN2','1985-02-02','Administrador'),
(3,'user1@mail.com','1234','USER1','2000-03-03','Normal'),
(4,'user2@mail.com','1234','USER2','1998-04-04','Normal'),
(5,'user3@mail.com','1234','USER3','1995-05-05','Normal'),
(6,'user6@mail.com','1234','ADMIN3','1997-01-05','Administrador'),
(7,'user7@mail.com','1234','ADMIN4','1991-06-30','Administrador'),
(8,'user8@mail.com','1234','ADMIN5','1986-11-25','Administrador'),
(9,'user4@mail.com','1234','USER4','1988-05-05','Normal'),
(10,'user5@mail.com','1234','USER5','1999-08-01','Normal');

INSERT INTO administrador VALUES 
(1,'600111111'),
(2,'600222222'),
(6,'600333333'),
(7,'600444444'),
(8,'600555555');

INSERT INTO usuarionormal VALUES 
(3,'Calle A','611111111','foto1.jpg'),
(4,'Calle B','622222222','foto2.jpg'),
(5,'Calle C','633333333','foto3.jpg'),
(9,'Calle D','644444444','foto4.jpg'),
(10,'Calle E','655555555','foto5.jpg');

INSERT INTO recurso VALUES 
(1,'Sala reuniones','Sala grande','Planta 1',20),
(2,'Pista padel','Exterior','Zona deportiva',4),
(3,'Aula','Informatica','Edificio B',30),
(4,'Auditorio','Eventos','Centro',100),
(5,'Laboratorio','Quimica','Edificio C',25);

INSERT INTO horario VALUES 
(1,'Lunes','09:00','11:00'),
(2,'Martes','10:00','12:00'),
(3,'Miercoles','16:00','18:00'),
(4,'Jueves','18:00','20:00'),
(5,'Viernes','08:00','10:00');

INSERT INTO disponibleen VALUES 
(1,1),
(2,2),
(3,3),
(4,4),
(5,5);

INSERT INTO reserva VALUES 
(1,1,3,CURDATE(),'09:00','10:00',10,5,'Reunion','Ninguna'),
(1,2,3,CURDATE(),'10:00','11:00',10,5,'Clase','Ninguna'),
(2,1,4,CURDATE(),'16:00','17:00',20,4,'Deporte','-'),
(3,1,5,CURDATE(),'08:00','09:00',15,10,'Curso','-'),
(4,1,4,CURDATE(),'18:00','19:00',25,20,'Evento','-');

-- ======================
-- CONSULTAS
-- ======================

-- Administradores
SELECT u.correo_electronico, u.nombre, a.telefono_guardia
FROM usuario u
JOIN administrador a 
ON u.id_usuario = a.id_usuario;

-- Reservas con usuario
SELECT r.id_reserva_local, u.nombre, u.correo_electronico
FROM reserva r
JOIN usuario u 
ON r.id_usuario = u.id_usuario;

-- Recursos con horarios
SELECT r.nombre, h.dia_semana, h.hora_inicio, h.hora_fin
FROM recurso r
JOIN disponibleen d 
ON r.id_recurso = d.id_recurso
JOIN horario h 
ON d.id_horario = h.id_horario;
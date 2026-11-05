-- ============================================================
-- PuntoTecno - Sistema de Gestion de Ventas y Garantias
-- Trabajo Practico N4 - Seminario de Practica de Informatica
-- Alumno: Beber Feil Thiagoclientes
-- ============================================================

DROP DATABASE IF EXISTS puntotecno;
CREATE DATABASE puntotecno CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE puntotecno;

CREATE TABLE categorias (
    id_categoria INT PRIMARY KEY,
    nombre VARCHAR(100),
    periodo_dias INT
);

CREATE TABLE productos (
    id_producto INT PRIMARY KEY,
    nombre VARCHAR(255),
    precio DECIMAL(10, 2),
    id_categoria INT,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

CREATE TABLE clientes (
    id_cliente INT PRIMARY KEY,
    nombre VARCHAR(255),
    contacto VARCHAR(100)
);

CREATE TABLE vendedores (
    legajo INT PRIMARY KEY,
    nombre VARCHAR(255)
);

CREATE TABLE ventas (
    numero_venta INT PRIMARY KEY,
    fecha DATE,
    id_cliente INT,
    legajo INT,
    id_producto INT,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (legajo) REFERENCES vendedores(legajo),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE garantias (
    id_garantia INT PRIMARY KEY,
    numero_venta INT UNIQUE,
    fecha_inicio DATE,
    fecha_fin DATE,
    estado VARCHAR(50),
    FOREIGN KEY (numero_venta) REFERENCES ventas(numero_venta)
);

-- Datos de prueba 
INSERT INTO categorias (id_categoria, nombre, periodo_dias) VALUES
(1, 'Linea blanca', 365),
(2, 'Pequenos electrodomesticos', 180),
(3, 'Tecnologia del hogar', 90);

INSERT INTO productos (id_producto, nombre, precio, id_categoria) VALUES
(1, 'Heladera XYZ', 850000.00, 1),
(2, 'Lavarropas Ultra', 620000.00, 1),
(3, 'Microondas Quick', 180000.00, 2),
(4, 'Auriculares BT', 45000.00, 3),
(5, 'Notebook Pro 15', 1200000.00, 3),
(6, 'Smart TV 55 4K', 950000.00, 3);

INSERT INTO clientes (id_cliente, nombre, contacto) VALUES
(1, 'Juan Perez', '3564-111222'),
(2, 'Maria Gomez', '3564-222333'),
(3, 'Carlos Lopez', '3564-333444'),
(4, 'Ana Rodriguez', '3564-444555');

INSERT INTO vendedores (legajo, nombre) VALUES
(101, 'Ana Martinez'),
(104, 'Roberto Fernandez');

INSERT INTO ventas (numero_venta, fecha, id_cliente, legajo, id_producto) VALUES
(1, '2026-09-22', 1, 101, 1),
(2, '2026-08-15', 2, 104, 5),
(3, '2025-05-10', 3, 101, 6),
(4, '2026-02-01', 4, 104, 3),
(5, '2024-11-12', 1, 101, 4);

-- fecha_fin = fecha de venta + periodo_dias de la categoria.
-- El estado se deja como referencia: el programa lo recalcula al iniciar.
INSERT INTO garantias (id_garantia, numero_venta, fecha_inicio, fecha_fin, estado) VALUES
(1, 1, '2026-09-22', '2027-09-22', 'Vigente'),
(2, 2, '2026-08-15', '2026-11-13', 'Vigente'),
(3, 3, '2025-05-10', '2025-08-08', 'Vencida'),
(4, 4, '2026-02-01', '2026-07-31', 'Vencida'),
(5, 5, '2024-11-12', '2025-02-10', 'Vencida');

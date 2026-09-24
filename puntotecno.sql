-- ============================================================
-- PuntoTecno - Sistema de Gestion de Ventas y Garantias
-- Trabajo Practico N2 - Seminario de Practica de Informatica
-- Alumno: Beber Feil Thiago
-- ============================================================

-- ============================================================
-- 1. CREACION DE LA BASE DE DATOS
-- ============================================================
CREATE DATABASE IF NOT EXISTS puntotecno;
USE puntotecno;

-- ============================================================
-- 2. CREACION DE TABLAS
-- ============================================================

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
    numero_venta INT,
    fecha_inicio DATE,
    fecha_fin DATE,
    estado VARCHAR(50),
    FOREIGN KEY (numero_venta) REFERENCES ventas(numero_venta)
);

-- ============================================================
-- 3. INSERCION DE DATOS DE PRUEBA
-- ============================================================

-- Categorias y su periodo de garantia en dias
INSERT INTO categorias (id_categoria, nombre, periodo_dias) VALUES
(1, 'Linea blanca', 365),
(2, 'Pequenos electrodomesticos', 180),
(3, 'Tecnologia del hogar', 90);

-- Productos
INSERT INTO productos (id_producto, nombre, precio, id_categoria) VALUES
(1, 'Heladera XYZ', 850000.00, 1),
(2, 'Lavarropas Ultra', 620000.00, 1),
(3, 'Microondas Quick', 180000.00, 2),
(4, 'Auriculares BT', 45000.00, 3),
(5, 'Notebook Pro 15', 1200000.00, 3),
(6, 'Smart TV 55 4K', 950000.00, 3);

-- Clientes
INSERT INTO clientes (id_cliente, nombre, contacto) VALUES
(1, 'Juan Perez', '3564-111222'),
(2, 'Maria Gomez', '3564-222333'),
(3, 'Carlos Lopez', '3564-333444'),
(4, 'Ana Rodriguez', '3564-444555');

-- Vendedores
INSERT INTO vendedores (legajo, nombre) VALUES
(101, 'Ana Martinez'),
(104, 'Roberto Fernandez');

-- Ventas
INSERT INTO ventas (numero_venta, fecha, id_cliente, legajo, id_producto) VALUES
(1, '2026-09-22', 1, 101, 1),   -- Heladera, venta reciente (vigente)
(2, '2026-08-15', 2, 104, 5),   -- Notebook, venta reciente (vigente)
(3, '2025-05-10', 3, 101, 6),   -- Smart TV, venta vieja (vencida, garantia 90 dias)
(4, '2026-02-01', 4, 104, 3),   -- Microondas, garantia 180 dias (vigente)
(5, '2024-11-12', 1, 101, 4);   -- Auriculares, venta vieja (vencida)

-- Garantias (fecha_fin = fecha_venta + periodo_dias segun categoria)
INSERT INTO garantias (id_garantia, numero_venta, fecha_inicio, fecha_fin, estado) VALUES
(1, 1, '2026-09-22', '2027-09-22', 'Vigente'),
(2, 2, '2026-08-15', '2027-08-15', 'Vigente'),
(3, 3, '2025-05-10', '2025-08-08', 'Vencida'),
(4, 4, '2026-02-01', '2026-07-30', 'Vigente'),
(5, 5, '2024-11-12', '2025-02-10', 'Vencida');

-- ============================================================
-- 4. CONSULTAS SELECT
-- ============================================================

-- 4.1 Consulta simple: listado de productos
SELECT * FROM productos;

-- 4.2 Consulta con vinculacion entre tablas (JOIN):
-- historial completo de ventas con datos de cliente, vendedor, producto y garantia
SELECT
    v.numero_venta AS 'N Venta',
    c.nombre AS 'Cliente',
    p.nombre AS 'Producto',
    ve.nombre AS 'Vendedor',
    v.fecha AS 'Fecha de venta',
    g.fecha_fin AS 'Vencimiento garantia',
    g.estado AS 'Estado'
FROM ventas v
JOIN clientes c ON v.id_cliente = c.id_cliente
JOIN vendedores ve ON v.legajo = ve.legajo
JOIN productos p ON v.id_producto = p.id_producto
JOIN garantias g ON v.numero_venta = g.numero_venta
ORDER BY v.fecha DESC;

-- 4.3 Consulta de evaluacion de un dato: determinar en tiempo real
-- si la garantia de una venta especifica sigue vigente o no,
-- comparando la fecha actual con la fecha de vencimiento
SELECT
    v.numero_venta AS 'N Venta',
    c.nombre AS 'Cliente',
    p.nombre AS 'Producto',
    g.fecha_fin AS 'Vencimiento',
    CASE
        WHEN g.fecha_fin >= CURDATE() THEN 'Vigente'
        ELSE 'Vencida'
    END AS 'Estado actual (calculado)'
FROM ventas v
JOIN clientes c ON v.id_cliente = c.id_cliente
JOIN productos p ON v.id_producto = p.id_producto
JOIN garantias g ON v.numero_venta = g.numero_venta
WHERE v.numero_venta = 3;

-- 4.4 Consulta filtrando por categoria de producto (linea blanca)
SELECT p.nombre AS 'Producto', p.precio, cat.nombre AS 'Categoria', cat.periodo_dias AS 'Dias de garantia'
FROM productos p
JOIN categorias cat ON p.id_categoria = cat.id_categoria
WHERE cat.nombre = 'Linea blanca';

-- 4.5 Consulta de historial de ventas de un cliente puntual
SELECT v.numero_venta, p.nombre AS 'Producto', v.fecha, g.estado
FROM ventas v
JOIN productos p ON v.id_producto = p.id_producto
JOIN garantias g ON v.numero_venta = g.numero_venta
WHERE v.id_cliente = 1;

-- ============================================================
-- 5. ACTUALIZACION DE REGISTROS (UPDATE)
-- ============================================================

-- Actualizar el contacto de un cliente
UPDATE clientes
SET contacto = '3564-999888'
WHERE id_cliente = 1;

-- Verificacion del update
SELECT * FROM clientes WHERE id_cliente = 1;

-- ============================================================
-- 6. BORRADO DE REGISTROS (DELETE)
-- ============================================================

-- Insertamos un registro de prueba para luego eliminarlo (evidenciar borrado)
INSERT INTO clientes (id_cliente, nombre, contacto) VALUES (99, 'Cliente de prueba', '000-000000');
SELECT * FROM clientes WHERE id_cliente = 99;

DELETE FROM clientes WHERE id_cliente = 99;

-- Verificacion del borrado (no debe devolver filas)
SELECT * FROM clientes WHERE id_cliente = 99;

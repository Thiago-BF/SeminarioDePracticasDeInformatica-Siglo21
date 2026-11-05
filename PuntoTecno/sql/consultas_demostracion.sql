-- ============================================================
-- PuntoTecno - TP4 - Consultas para verificar en MySQL lo que se hace desde el programa
-- Ejecutar en MySQL Workbench DESPUES de usar la aplicacion.
-- ============================================================
USE puntotecno;

-- 1) Listado completo de ventas con su garantia (el mismo dato que muestra "Listar ventas")
SELECT v.numero_venta AS 'N Venta', c.nombre AS 'Cliente', p.nombre AS 'Producto',
       ve.nombre AS 'Vendedor', v.fecha AS 'Fecha', g.fecha_fin AS 'Vencimiento', g.estado AS 'Estado guardado'
FROM ventas v
JOIN clientes c    ON v.id_cliente = c.id_cliente
JOIN vendedores ve ON v.legajo = ve.legajo
JOIN productos p   ON v.id_producto = p.id_producto
JOIN garantias g   ON v.numero_venta = g.numero_venta
ORDER BY v.numero_venta;

-- 2) Clientes (verifica "Dar de alta cliente" y "Modificar cliente")
SELECT * FROM clientes ORDER BY id_cliente;

-- 3) Productos con su categoria (verifica "Dar de alta producto")
SELECT p.id_producto, p.nombre, p.precio, c.nombre AS categoria, c.periodo_dias
FROM productos p JOIN categorias c ON p.id_categoria = c.id_categoria
ORDER BY p.id_producto;

-- 4) Ultima venta registrada y su garantia (verifica "Registrar venta": se guardan las DOS filas)
SELECT v.numero_venta, v.fecha, g.id_garantia, g.fecha_inicio, g.fecha_fin, g.estado
FROM ventas v JOIN garantias g ON v.numero_venta = g.numero_venta
ORDER BY v.numero_venta DESC LIMIT 1;

-- 5) Para probar la actualizacion de estados al iniciar:
--    a) dejar un estado desactualizado a proposito
--       UPDATE garantias SET estado = 'Vigente' WHERE numero_venta = 5;
--    b) abrir el programa (en la consola aparece "Garantias con estado corregido: 1")
--    c) volver a consultar:
SELECT numero_venta, fecha_fin, estado FROM garantias ORDER BY numero_venta;

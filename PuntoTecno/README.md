# PuntoTecno - Sistema de Gestión de Ventas y Garantías

Trabajo Práctico N° 3 - Seminario de Práctica de Informática (Universidad Siglo 21)
Alumno: Beber Feil Thiago

Prototipo en Java (Swing) con datos en memoria (ArrayList). La conexión a MySQL con JDBC corresponde al TP4.

## Estructura
- `src/puntotecno/` : código fuente (paquete `puntotecno`)
- `compilar.bat`    : compila el proyecto en la carpeta `bin`
- `ejecutar.bat`    : compila y ejecuta el sistema (doble clic)

## Requisitos
JDK 11 o superior.

## Ejecutar desde consola
```
javac -encoding UTF-8 -d bin src/puntotecno/*.java
java -cp bin puntotecno.PuntoTecnoApp
```

## Ejecutar desde un IDE (NetBeans / IntelliJ / Eclipse)
Crear un proyecto Java, copiar la carpeta `puntotecno` dentro de `src` y ejecutar la clase `PuntoTecnoApp`.

## Clases principales
- Modelo: `Producto` (abstracta), `LineaBlanca`, `PequenoElectrodomestico`, `TecnologiaHogar`, `Cliente`, `Vendedor`, `Venta`, `Garantia`, `GarantiaException`
- Gestión: `PuntoTecno` (listas, búsqueda binaria, ordenación por inserción)
- Interfaz: `MenuPrincipal`, `Ventana*`, `PanelFormulario`, `PanelTablaVentas`, `Formato`
- Arranque: `PuntoTecnoApp`

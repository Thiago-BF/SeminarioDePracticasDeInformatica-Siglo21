# PuntoTecno - Sistema de Gestión de Ventas y Garantías

Trabajo Práctico N° 4 - Seminario de Práctica de Informática (Universidad Siglo 21)
Alumno: Beber Feil Thiago

Aplicación de escritorio en Java (Swing) con persistencia en MySQL mediante JDBC, organizada con el patrón MVC.

## Estructura
- `src/puntotecno/`              : modelo (entidades, `PuntoTecno`) y vista (menú y pantallas)
- `src/puntotecno/controlador/`  : `ControladorPuntoTecno`
- `src/puntotecno/persistencia/` : conexión y acceso a MySQL (`ConexionBD`, `RepositorioPuntoTecno`, `ModeloMySQL`, excepciones)
- `sql/puntotecno_tp4.sql`       : crea la base `puntotecno`, las tablas y los datos de prueba
- `sql/consultas_demostracion.sql` : consultas para verificar en MySQL lo que hace el programa
- `config/conexion.properties.example` : plantilla de configuración de la conexión
- `lib/`                         : aquí va el driver JDBC de MySQL (ver `lib/LEEME.txt`)
- `pruebas/PruebaIntegracion.java` : prueba por consola del acceso a MySQL (opcional)
- `sql/puntotecno.sql`           : script original del TP2 (se conserva como referencia)

## Puesta en marcha
1. **MySQL**: iniciar MySQL (por ejemplo desde XAMPP) e importar `sql/puntotecno_tp4.sql`
   (MySQL Workbench: File > Open SQL Script > ejecutar todo). ATENCIÓN: recrea la base `puntotecno`.
2. **Driver**: descargar MySQL Connector/J y copiar el `.jar` en `lib/` (ver `lib/LEEME.txt`).
3. **Configuración**: copiar `config/conexion.properties.example` como `config/conexion.properties`
   y completar `db.usuario` y `db.clave`. Este archivo no se sube a GitHub.
4. **Ejecutar**: doble clic en `ejecutar.bat`, o desde la carpeta del proyecto:
```
javac -encoding UTF-8 -cp "lib/*" -d bin src/puntotecno/*.java src/puntotecno/persistencia/*.java src/puntotecno/controlador/*.java
java -cp "bin;lib/*" puntotecno.PuntoTecnoApp
```
(en Linux/macOS el separador del classpath es `:` en lugar de `;`).

En IntelliJ: abrir la carpeta, marcar `src` como Sources Root, agregar `lib` como biblioteca
(File > Project Structure > Modules > Dependencies > +) y ejecutar `PuntoTecnoApp`.

## Prueba por consola (opcional)
Con la base recién importada y `config/conexion.properties` listo, desde la carpeta del proyecto:
```
javac -encoding UTF-8 -cp "lib/*" -d bin src/puntotecno/*.java src/puntotecno/persistencia/*.java src/puntotecno/controlador/*.java pruebas/PruebaIntegracion.java
java -cp "bin;lib/*" PruebaIntegracion
```
Agrega registros de prueba: para repetirla, volver a importar `sql/puntotecno_tp4.sql`.

## Requisitos
JDK 11 o superior y MySQL 8 (o compatible).

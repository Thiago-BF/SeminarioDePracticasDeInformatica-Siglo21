@echo off
chcp 65001 >nul
if not exist bin mkdir bin
if not exist lib\*.jar (
    echo.
    echo ERROR: falta el driver JDBC de MySQL. Coloque el .jar en la carpeta lib ^(ver lib\LEEME.txt^).
    pause
    exit /b 1
)
if not exist config\conexion.properties (
    echo.
    echo ERROR: falta config\conexion.properties. Copie config\conexion.properties.example con ese nombre
    echo y complete su usuario y contrasena de MySQL.
    pause
    exit /b 1
)
echo Compilando...
javac -encoding UTF-8 -cp "lib\*" -d bin src\puntotecno\*.java src\puntotecno\persistencia\*.java src\puntotecno\controlador\*.java
if errorlevel 1 (
    echo.
    echo ERROR: no se pudo compilar. Verifique que el JDK este instalado ^(comando javac^).
    pause
    exit /b 1
)
echo Iniciando PuntoTecno...
java -cp "bin;lib\*" puntotecno.PuntoTecnoApp

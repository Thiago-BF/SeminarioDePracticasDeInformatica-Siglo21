@echo off
chcp 65001 >nul
echo Compilando PuntoTecno...
if not exist bin mkdir bin
if not exist lib\*.jar (
    echo.
    echo ERROR: falta el driver JDBC de MySQL. Coloque el .jar en la carpeta lib ^(ver lib\LEEME.txt^).
    pause
    exit /b 1
)
javac -encoding UTF-8 -cp "lib\*" -d bin src\puntotecno\*.java src\puntotecno\persistencia\*.java src\puntotecno\controlador\*.java
if errorlevel 1 (
    echo.
    echo ERROR: no se pudo compilar. Verifique que el JDK este instalado ^(comando javac^).
    pause
    exit /b 1
)
echo Compilacion exitosa.
pause

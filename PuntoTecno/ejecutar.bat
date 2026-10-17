@echo off
chcp 65001 >nul
if not exist bin mkdir bin
echo Compilando...
javac -encoding UTF-8 -d bin src\puntotecno\*.java
if errorlevel 1 (
    echo.
    echo ERROR: no se pudo compilar. Verifique que el JDK este instalado ^(comando javac^).
    pause
    exit /b 1
)
echo Iniciando PuntoTecno...
java -cp bin puntotecno.PuntoTecnoApp

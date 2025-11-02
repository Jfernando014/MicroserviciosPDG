@echo off
echo ========================================
echo   Sistema de Gestion de Trabajos de Grado
echo   Iniciando arquitectura escalable...
echo ========================================
echo.

REM Iniciar RabbitMQ (si no está corriendo)
echo [1/5] Verificando RabbitMQ...
echo RabbitMQ debe estar corriendo en localhost:5672
echo.

REM Iniciar 2 instancias de user-microservice
echo [2/5] Iniciando user-microservice (2 instancias)...
start "User-Service-8081" java -jar user-microservice/target/user-microservice-0.0.1-SNAPSHOT.jar --server.port=8081
timeout /t 3 /nobreak >nul
start "User-Service-8091" java -jar user-microservice/target/user-microservice-0.0.1-SNAPSHOT.jar --server.port=8091
echo   - Instancia 1: http://localhost:8081
echo   - Instancia 2: http://localhost:8091
echo.

REM Iniciar 2 instancias de project-microservice
echo [3/5] Iniciando project-microservice (2 instancias)...
timeout /t 5 /nobreak >nul
start "Project-Service-8082" java -jar project-microservice/target/project-microservice-0.0.1-SNAPSHOT.jar --server.port=8082
timeout /t 3 /nobreak >nul
start "Project-Service-8092" java -jar project-microservice/target/project-microservice-0.0.1-SNAPSHOT.jar --server.port=8092
echo   - Instancia 1: http://localhost:8082
echo   - Instancia 2: http://localhost:8092
echo.

REM Iniciar 2 instancias de document-microservice
echo [4/5] Iniciando document-microservice (2 instancias)...
timeout /t 5 /nobreak >nul
start "Document-Service-8083" java -jar document-microservice/target/document-microservice-0.0.1-SNAPSHOT.jar --server.port=8083
timeout /t 3 /nobreak >nul
start "Document-Service-8093" java -jar document-microservice/target/document-microservice-0.0.1-SNAPSHOT.jar --server.port=8093
echo   - Instancia 1: http://localhost:8083
echo   - Instancia 2: http://localhost:8093
echo.

REM Iniciar 1 instancia de notification-microservice
echo [5/5] Iniciando notification-microservice...
timeout /t 5 /nobreak >nul
start "Notification-Service-8084" java -jar notification-microservice/target/notification-microservice-0.0.1-SNAPSHOT.jar --server.port=8084
echo   - Instancia 1: http://localhost:8084
echo.

echo ========================================
echo   SISTEMA INICIADO CORRECTAMENTE
echo ========================================
echo.
echo Microservicios activos:
echo   - user-microservice:         8081, 8091
echo   - project-microservice:      8082, 8092
echo   - document-microservice:     8083, 8093
echo   - notification-microservice: 8084
echo.
echo Total de instancias: 7
echo RabbitMQ: localhost:5672
echo.
echo Presiona cualquier tecla para cerrar todas las instancias...
pause >nul

REM Cerrar todas las instancias de Java
taskkill /F /FI "WINDOWTITLE eq User-Service*" /T
taskkill /F /FI "WINDOWTITLE eq Project-Service*" /T
taskkill /F /FI "WINDOWTITLE eq Document-Service*" /T
taskkill /F /FI "WINDOWTITLE eq Notification-Service*" /T

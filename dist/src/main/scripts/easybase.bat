@echo off
REM EasyBase command-line interface

set SCRIPT_DIR=%~dp0
set LIB_DIR=%SCRIPT_DIR%lib
set CONFIG_DIR=%SCRIPT_DIR%config

for /f %%i in ('dir /b "%LIB_DIR%\easybase-core-*.jar"') do set MAIN_JAR=%LIB_DIR%\%%i

if "%1" == "serve" (
    java -cp "%MAIN_JAR%;%LIB_DIR%\*" -Dlogging.config="%CONFIG_DIR%\logback.xml" com.easybase.core.Application serve
) else if "%1" == "superuser" (
    if "%2" == "create" (
        java -cp "%MAIN_JAR%;%LIB_DIR%\*" com.easybase.core.Application superuser create %3 %4
    )
) else if "%1" == "generate" (
    java -cp "%MAIN_JAR%;%LIB_DIR%\*" com.easybase.generator.GeneratorMain %2
) else (
    echo Usage:
    echo   %0 serve                     # Start the EasyBase server
    echo   %0 superuser create EMAIL PASSWORD # Create a superuser account
    echo   %0 generate entity.yml       # Generate code from YAML definition
)

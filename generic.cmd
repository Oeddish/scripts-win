@echo off
cls

rem sets common vars
call %1\baseenv.cmd

rem sets common vars
SET HOME=%SRC%
SET SCRIPT_HOME=%DEV%\scripts
SET PROJ_ROOT=%SRC%\git

rem clear classpath - can cause negative reaction with IDEs and other tools
SET CLASSPATH=

rem Work-around because some user directories may be moved but system environment
rem vars aren't set by windows 10 to reflect it.  Otherwise would have used 
rem %HOMEDRIVE% and everything relative to it

rem Most things were moved but not all of them
set DESKTOP=%USERPROFILE%\Desktop
set DOCUMENTS=%USERPROFILE%\Documents
set DOWNLOADS=%USERPROFILE%\Downloads
set APPDATA=%USERPROFILE%\AppData

rem project variables
SET SCRIPT_NAME=generic.cmd

rem 
rem JAVA STUFF
rem

SET JAVA_HOME=D:\dev\jdk\1.8.0_20
rem mostly java opts from multicache
SET JAVA_OPTS=-Xms4G -Xmx4G -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -server 
SET PATH=%PATH%;%JAVA_HOME%\bin

rem
rem ALIASES
rem

doskey np        = notepad++ $1
doskey proj      = pushd %PROJ_ROOT%
doskey env       = call  %SCRIPT_HOME%\%SCRIPT_NAME% %SCRIPT_HOME%
doskey eenv      = notepad++ %SCRIPT_HOME%\%SCRIPT_NAME%
doskey ebase     = notepad++ %SCRIPT_HOME%\baseenv.cmd
doskey scripts   = pushd %SCRIPT_HOME%
doskey ll        = ls -l
doskey intellij  = start %DEV%\IntelliJ-2019.2\bin\idea64.exe
doskey glue      = pushd %PROJ_ROOT%\glue\tube

doskey desktop   = pushd %DESKTOP%
doskey docs      = pushd %DOCUMENTS%
doskey download  = pushd %DOWNLOADS%
doskey appdata   = pushd %APPDATA%
doskey user      = pushd %USERPROFILE%

TITLE Generic Dev Console
@echo Environment    : %SCRIPT_HOME%\%SCRIPT_NAME%
@ECHO.
@ECHO SRC            : %SRC%
@ECHO DEV            : %DEV%
@ECHO.
pushd %PROJ_ROOT%


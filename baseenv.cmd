
rem all install-specific drive letters should be isolated here!
SET SRC=d:\source
SET DEV=d:\dev

SET SCRIPT_HOME=%DEV%\scripts
SET PATH=%PATH%;%DEV%\cygwin\bin;%SCRIPT_HOME%;%DEV%\groovy-2.1.2\bin;%DEV%\bin
set PATH=%PATH%;%ProgramFiles%\Notepad++

rem clear classpath - can cause negative reaction with IDEs and other tools
SET CLASSPATH=

rem
rem ALIASES
rem 

rem puts current directory on the clipboard
doskey cc=echo^|set /p=%%cd%%^|clip
doskey cs= pushd $1
doskey cr= popd

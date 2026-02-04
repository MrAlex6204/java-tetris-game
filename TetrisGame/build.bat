echo off
cls
setlocal
set main_file=src/com/game/tetris/Launcher.java
set PATH=%PATH%;C:\Program Files\Java\jdk-25\bin;

echo Main file : %main_file%
echo Compiling source code..
javac -d . -sourcepath src %main_file% src/com/game/tetris/components/*.java
echo Building jar file..
echo Cleaning old jar file..
del bin/tetris.jar
jar cfm bin/tetris.jar META-INF/MANIFEST.MF -C . com/
echo Build complete.


@REM pause

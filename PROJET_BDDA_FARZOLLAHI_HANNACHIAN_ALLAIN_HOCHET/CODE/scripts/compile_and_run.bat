@echo off
set "src_dir=C:\Users\User\Desktop\PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET\CODE\src"
set "out_dir=C:\Users\User\Desktop\PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET\DB"
set "main_class=TestDB"


rem Compile the Java source files in the source directory
javac -d "%out_dir%" "%src_dir%\*.java"


rem Run the Java program
cd "%out_dir%"
java %main_class%


rem Return to the project root directory (if this is the correct path)
cd "..\..\.."
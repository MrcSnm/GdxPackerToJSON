@echo off
echo Starting atlas to json process
set /p inputFile="Set your input file before compiling: "
java AtlasToJSON %inputFile%
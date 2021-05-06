@echo off
setlocal EnableDelayedExpansion

set mvn_args=%*
set current_dir=%cd%
set error_code=0

for /f "delims=," %%a in (%current_dir%\build.txt) do (
  call :mvn "%%a"
  if !errorlevel! GTR 0 goto :eof
)
goto :eof

:mvn
  setlocal
  cd "%current_dir%\%1"
  call mvn %mvn_args%  
  exit /b %errorlevel%

:eof
cd %current_dir%
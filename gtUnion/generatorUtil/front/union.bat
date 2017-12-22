@echo off

set all=0

::choice的errorlevel必须倒序
choice /C abcw /M "编译内容：A|a(所有) B|b(佣金平台) C|c(联盟卡粉丝端) W|w(联盟后台)"
if errorlevel 4 goto web
if errorlevel 3 goto card
if errorlevel 2 goto brokerage
if errorlevel 1 goto all

::当前盘符：%~d0
::当前路径：%cd%
::当前执行命令行：%0
::当前bat文件路径：%~dp0
::当前bat文件短路径：%~sdp0

:all
set all=1
goto :brokerage

rem -------------------------------------------------------------------------------------
:brokerage
echo --------开始进行佣金平台编译--------

::检查依赖包
call %~dp0brokerageInstall.bat

::开始编译
call %~dp0brokerageBuild.bat

::移动文件夹
if not exist %~dp0..\..\src\main\webapp\brokeragePhone\static\css\static (
  md %~dp0..\..\src\main\webapp\brokeragePhone\static\css\static
)
move /Y %~dp0..\..\src\main\webapp\brokeragePhone\static\img %~dp0..\..\src\main\webapp\brokeragePhone\static\css\static\img

echo --------佣金平台编译结束--------

::继续或退出
if %all%==1 goto card
goto end

rem -------------------------------------------------------------------------------------
:card
echo --------开始进行联盟卡粉丝端编译--------

::检查依赖包
call %~dp0cardInstall.bat

::开始编译
call %~dp0cardBuild.bat

::移动文件夹
if not exist %~dp0..\..\src\main\webapp\cardPhone\static\css\static (
  md %~dp0..\..\src\main\webapp\cardPhone\static\css\static
)
move /Y %~dp0..\..\src\main\webapp\cardPhone\static\img %~dp0..\..\src\main\webapp\cardPhone\static\css\static\img

echo --------联盟卡粉丝段编译结束--------

::继续或退出
if %all%==1 goto web
goto end

rem -------------------------------------------------------------------------------------
:web
echo --------开始进行联盟后台编译--------

::检查依赖包
call %~dp0webInstall.bat

::开始编译
call %~dp0webBuild.bat

echo --------联盟后台编译结束--------

::退出
goto end

rem -------------------------------------------------------------------------------------
:end
echo 编译结束
pause
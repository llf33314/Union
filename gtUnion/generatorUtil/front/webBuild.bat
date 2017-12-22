@echo off

::当前盘符：%~d0
::当前路径：%cd%
::当前执行命令行：%0
::当前bat文件路径：%~dp0
::当前bat文件短路径：%~sdp0

::切换路径
cd %~dp0..\..\..\cli\unionWeb

::编译
echo ------------开始编译------------
cnpm run build
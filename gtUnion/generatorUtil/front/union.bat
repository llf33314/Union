@echo off

set all=0

::choice��errorlevel���뵹��
choice /C abcw /M "�������ݣ�A|a(����) B|b(Ӷ��ƽ̨) C|c(���˿���˿��) W|w(���˺�̨)"
if errorlevel 4 goto web
if errorlevel 3 goto card
if errorlevel 2 goto brokerage
if errorlevel 1 goto all

::��ǰ�̷���%~d0
::��ǰ·����%cd%
::��ǰִ�������У�%0
::��ǰbat�ļ�·����%~dp0
::��ǰbat�ļ���·����%~sdp0

:all
set all=1
goto :brokerage

rem -------------------------------------------------------------------------------------
:brokerage
echo --------��ʼ����Ӷ��ƽ̨����--------

::���������
call %~dp0brokerageInstall.bat

::��ʼ����
call %~dp0brokerageBuild.bat

::�ƶ��ļ���
if not exist %~dp0..\..\src\main\webapp\brokeragePhone\static\css\static (
  md %~dp0..\..\src\main\webapp\brokeragePhone\static\css\static
)
move /Y %~dp0..\..\src\main\webapp\brokeragePhone\static\img %~dp0..\..\src\main\webapp\brokeragePhone\static\css\static\img

echo --------Ӷ��ƽ̨�������--------

::�������˳�
if %all%==1 goto card
goto end

rem -------------------------------------------------------------------------------------
:card
echo --------��ʼ�������˿���˿�˱���--------

::���������
call %~dp0cardInstall.bat

::��ʼ����
call %~dp0cardBuild.bat

::�ƶ��ļ���
if not exist %~dp0..\..\src\main\webapp\cardPhone\static\css\static (
  md %~dp0..\..\src\main\webapp\cardPhone\static\css\static
)
move /Y %~dp0..\..\src\main\webapp\cardPhone\static\img %~dp0..\..\src\main\webapp\cardPhone\static\css\static\img

echo --------���˿���˿�α������--------

::�������˳�
if %all%==1 goto web
goto end

rem -------------------------------------------------------------------------------------
:web
echo --------��ʼ�������˺�̨����--------

::���������
call %~dp0webInstall.bat

::��ʼ����
call %~dp0webBuild.bat

echo --------���˺�̨�������--------

::�˳�
goto end

rem -------------------------------------------------------------------------------------
:end
echo �������
pause
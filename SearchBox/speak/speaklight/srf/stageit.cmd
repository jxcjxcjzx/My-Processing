rem echo off

c:
cd \Source\speakright\staging
md srf
cd srf

xcopy C:\Source\speakright\srf  .  /i
xcopy C:\Source\speakright\srf\.settings    .\.settings /i /s
xcopy C:\Source\speakright\srf\demos        .\demos /i /s
xcopy C:\Source\speakright\srf\src          .\src /i /s
xcopy C:\Source\speakright\srf\test         .\test /i /s
xcopy C:\Source\speakright\srf\libs         .\libs /i /s

cd ..
md SimpsonsDemoServlet
cd SimpsonsDemoServlet

xcopy C:\Source\speakright\SimpsonsDemoServlet . /i /s

cd ..
copy C:\Source\speakright\log4j.properties .

c:
cd \Source\speakright\staging
md doc
cd doc
xcopy C:\Source\speakright\srf\doc . /i /s


dir

pause
 
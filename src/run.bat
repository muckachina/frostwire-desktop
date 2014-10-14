@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
SET LIBPATH=..\lib\native
SET PATH=%PATH%;%LIBPATH%
SET CLASSPATH=..\build\libs\frostwire.jar

java -Xms32m -Xmx512m -Ddebug=1 -Djava.net.preferIPV6Addresses=false -ea -Djava.net.preferIPv4stack=true -Dcom.sun.management.jmxremote.port=9595 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=127.0.0.1 -Djava.library.path=%LIBPATH% com.limegroup.gnutella.gui.Main

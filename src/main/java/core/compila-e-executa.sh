#!/bin/bash
#

#CONFIG="-Djgroups.bind_addr=172.16.2.222 -Djava.net.preferIPv4Stack=true"
CONFIG="-Djava.net.preferIPv4Stack=true"

#LIBS=../lib/jgroups-3.6.4.Final.jar:./
#export CLASSPATH=$CLASSPATH:$LIBS

javac App.java
#javac TiposDeCast2.java

#RUN_CMD="java $CONFIG -cp $LIBS TiposDeCast 2>/dev/null"
RUN_CMD="java $CONFIG -cp App 2>/dev/null"
echo "$RUN_CMD"
#RUN_CMD2="java $CONFIG -cp $LIBS TiposDeCast2 2>/dev/null"
#echo "$RUN_CMD2"

## executar 1 processo neste computador
${RUN_CMD} &
sleep 1

#${RUN_CMD2} &
#sleep 1

## executar outras 3 janelas neste computador
#xterm -hold -e "$RUN_CMD2"  &
#xterm -hold -e "$RUN_CMD2"  &

xterm -hold -e "$RUN_CMD" -c  &
xterm -hold -e "$RUN_CMD" -v  &
xterm -hold -e "$RUN_CMD" -m  &


## OBS.: se nao tiver o xterm instalado, use o comando abaixo:
#x-terminal-emulator -e "$RUN_CMD"  &
#x-terminal-emulator -e "$RUN_CMD"  &
#x-terminal-emulator -e "$RUN_CMD"  &

**Clone this repository**

> git init

> git clone https://github.com/Lewis-G/MQU-3100.git


**Run the ds-server as a simulation, options can be configured**

_Run ds-server_

> ./MQU-3100/distsys-MQ/ds-sim/src/pre-compiled/ds-server -n j 1

_Compile and run ds-client in another terminal window_

> javac /MQU-3100/src/MyClient.java

> java /MQU-3100/src/MyClient


**Run a test script**

_In files application, move the zipped test script to the pre-compiled folder_

_Unzip the zipped test script_

> tar xvf MQU-3100/distsys-MQ/ds-sim/src/pre-compiled/week06.tar

_Compile ds-client_

> javac /MQU-3100/src/MyClient.java

_Run the test script, the script name will be given prior to the tutorial_

> ./MQU-3100/distsys-MQ/ds-sim/src/pre-compiled/**Script.sh** /MQU-3100/src/MyClient.class -n

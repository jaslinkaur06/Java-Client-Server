####### CLIENT PROGRAM #########
The Client program monitors a directory for new Java properties file using WatchService. When a new properties file appears in the monitored directory, it processes the properties file as follows:

• It reads the file into a HashMap.
• It applies a regular expression pattern filter for the keys (i.e., remove key/value mappings
  where keys do not match a configurable regular expression pattern).
• The filtered mappings are then relayed to a server program using a socket connection.
• After the file is successfully sent to the server, the file is deleted from the directory.

The client program’s main method accepts an argument specifying a config file path. I have added the sample config properties file (ClientConfig.properties) in the project source directory and provided this config file's path as program argument in order to run the program. 

The client config file contains values defining :
• 'monitoredDirectory' = the directory path that is monitored
• 'keyFilterPattern' = the key filtering pattern that is applied on the map keys.
• 'serverAddress' = the address of the corresponding server program.
• 'serverPort' = any other configurale value - server port.

In case you want to create a new config file and provide your own user defined values, please use the below format for the property keys:

monitoredDirectory=/path/to/monitored/directory
keyFilterPattern=regex-pattern
serverAddress=localhost:8080
serverPort=8080



####### SERVER PROGRAM #########
The server program uses a ServerSocket to accept connections from multiple clients. For each incoming client request, a new CleintRequestHandler thread is spawned to process and complete that specific request. The CleintRequestHandler reads the filtered properties map from the client, reconstructs the filtered properties file using the original filename and writes to the intended output directory.

The server program’s main method accepts an argument specifying a config file path.I have added the sample config properties file (ServerConfig.properties) in the project source directory and provided this config file's path as program argument in order to run the program.

The server config file contains values defining:
• 'writeDirectory' = the location of the directory to which to write the files
• 'port' = what port to listen on


In case you want to create a new config file and provide your own user defined values, please use the below format for the property keys:

writeDirectory=/path/to/output/directory
port=8080

The above created file's path is then required to be provided as the program argument in order to run the program.



######## TO BUILD AND RUN THE CLIENT PROGRAM ######
 
 1) Compile Client program using, run:
 
    javac DirectoryMonitorService.java
 
 2) To run the program, provide the configuration file path (here ClientConfig.properties) as program argument. For example:
         
    java DirectoryMonitorService "C:/Users/Jaslin Lehal/Documents/ARCTIC WOLF/Java-Client-Server/src/ClientConfig.properties"


 
######## TO BUILD AND RUN THE SERVER PROGRAM ######

 1) Compile Client program using, run:
 
    javac ServerProgram.java
 
 2) To run the program, provide the configuration file path (here ClientConfig.properties) as program argument. For example:
         
    java ServerProgram ""C:/Users/Jaslin Lehal/Documents/ARCTIC WOLF/Java-Client-Server/src/ServerConfig.properties""

package handler;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ClientRequestHandler extends Thread {
        
	private Socket clientSocket;
	private String directoryPath;
	
	public ClientRequestHandler(Socket clientSocket, String directoryPath) {
        this.clientSocket = clientSocket;
        this.directoryPath = directoryPath;
	 }
	
	@SuppressWarnings("unchecked")
	@Override
    public void run() {
		
		 try(ObjectInputStream object = new ObjectInputStream(clientSocket.getInputStream()); 
		 PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream())){
			 Map<String,String> propertiesMap;
			 Object readObject;
			 try {
					//while ((readObject = object.readObject()) != null) {
					while(true) {
						readObject = object.readObject();
						if (readObject instanceof String && "End of file".equals(readObject)) {
				            // End of data reached
				            break;
				        }
						if (readObject instanceof Map) {
							propertiesMap = (Map<String, String>) readObject;
							String originalFile = propertiesMap.get("filename");
							propertiesMap.remove("filename");
							
							Path directory = Paths.get(directoryPath);
							Path filePath = directory.resolve(Paths.get(originalFile));

							createPropAndWriteToFile(propertiesMap, filePath, printWriter);
						} else {
							// Handle unexpected object type if needed
							System.out.println("Unexpected object type: " + readObject.getClass().getName());
						}
					}
			      }
			 catch (EOFException e) {
                 // Handle EOFException appropriately
                 System.err.println("End of stream reached for client: " + clientSocket.getInetAddress());
             }
			 catch (FileNotFoundException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		     catch (IOException e) {
             e.printStackTrace();
             }
	     }

	private void createPropAndWriteToFile(Map<String, String> propertiesMap, Path filePath, PrintWriter printWriter) {
		Properties properties = new Properties();
		properties.putAll(propertiesMap);
		// Writing map object to file
		try(FileOutputStream file = new FileOutputStream(filePath.toString());
		OutputStreamWriter writer = new OutputStreamWriter(file, StandardCharsets.UTF_8)){

		properties.store(writer, "Comments (if any)");

		// send response to client program
		printWriter.write("Map received succesfully");

               }catch (IOException e) {
		   e.printStackTrace();
                     }
	}
	}

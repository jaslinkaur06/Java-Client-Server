package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import config.ConfigLoader;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryMonitorService {
		
	public static void main(String args[]) {
		
		String configFilePath = args[0]; //"C:\\Users\\Jaslin Lehal\\Documents\\ARCTIC WOLF\\Java-Client-Server\\src\\ClientFile.properties"; 
		ConfigLoader configLoader = new ConfigLoader(configFilePath);

		try {
			watchDirectory(configLoader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static void watchDirectory(ConfigLoader configLoader) throws IOException {


		String monitoredDirectoryPath = configLoader.getProperty("monitoredDirectory");
 
		Path dir = Paths.get(monitoredDirectoryPath);
		try(WatchService watcher = FileSystems.getDefault().newWatchService()){
		WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		
		for(;;) {

		     try {
		    	 
		    	 key = watcher.take();
		     }
		     catch(InterruptedException x) {
		    	 return;
		     }
		     
		for(WatchEvent<?> event: key.pollEvents()) {
			
			WatchEvent.Kind<?> kind = event.kind();
			
			if (kind == ENTRY_CREATE) {
				
			WatchEvent<Path> ev = (WatchEvent<Path>)event;
			Path createdFile = ev.context();
			
			// Resolve the filename against the directory.
            // If the filename is "test" and the directory is "sample",
            // the resolved name is "sample/test".
			Path filePath = dir.resolve(createdFile);
			// Verify that the new
	        //  file is a properties file.
		
	            if (createdFile.toString().endsWith(".properties")) {
	            	//Process the file further
	                processPropertiesFile(filePath.toString(),configLoader);
	                
	                //Delete the file
	                Files.delete(filePath);
	            }
			
			}}
		key.reset();
		}}
		catch (IOException x) {
            System.err.println(x);
            //continue;
        }
			
	}
	
	
	public static void processPropertiesFile(String path, ConfigLoader configLoader) throws IOException {
		
        String keyFilterPattern = configLoader.getProperty("keyFilterPattern");
        String serverAddress = configLoader.getProperty("serverAddress");
        String serverPort = configLoader.getProperty("serverPort");
		
		try {
		    Properties properties = new Properties();
			FileInputStream fileInputStream = new FileInputStream(path);
			properties.load(fileInputStream);
			
			//displaying properties before filtering
			properties.forEach((key,value)->System.out.println(key+" "+value));
			
			// Apply the regular expression pattern filter
            Map<String, String> filteredProperties = filterProperties(properties, keyFilterPattern);
	
            String originalFileName = Paths.get(path).getFileName().toString();
            
			//storing filename as key-value pair to send to server
            filteredProperties.put("filename", originalFileName);
            
            //After filtering displaying properties:
            filteredProperties.forEach((key,value)->System.out.println(key+" "+value));
            
            fileInputStream.close();
            //relay the filtered map to server program
            sendToServer(filteredProperties, serverAddress, serverPort);
 
		 }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static Map<String,String> filterProperties(Properties properties, String regexPattern) {
		Map<String, String> filteredProperties = new HashMap<>();
		Pattern pattern = Pattern.compile(regexPattern);
		properties.forEach((key, value)-> {
			Matcher matcher = pattern.matcher((String) key);
			if(matcher.matches()) {
				filteredProperties.put((String) key, (String) value);
			}}
			);
		return filteredProperties;
	}
	
	
	public static void sendToServer(Map<String, String> map, String serverAddress, String serverPort) {
		try (Socket socket = new Socket(serverAddress, Integer.parseInt(serverPort));
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

			// send map to server
			objectOutputStream.writeObject(map);
			objectOutputStream.writeObject("End of file");
			System.out.println("Map sent to server successfully");

			// objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}}

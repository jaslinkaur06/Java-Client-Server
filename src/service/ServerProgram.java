package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import config.ConfigLoader;
import handler.ClientRequestHandler;

public class ServerProgram {

	public static void main(String args[]) throws IOException {

		String configFilePath = args[0];
		ConfigLoader config = new ConfigLoader(configFilePath);
		// acceptServerMessages(config);
		String directoryPath = config.getProperty("writeDirectory");
		String portNumber = config.getProperty("port");
		
		try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(portNumber))) {
			System.out.println("Server listening on port:" + portNumber);

			while (true) {
				Socket clientSocket = serverSocket.accept();
				new ClientRequestHandler(clientSocket, directoryPath).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

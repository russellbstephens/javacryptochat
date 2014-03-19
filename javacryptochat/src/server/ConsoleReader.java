package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import aes.AES;

public class ConsoleReader implements Runnable {
	private Socket socket = null;
	private Thread clientThread = null;
	private BufferedReader console = null;
	private DataOutputStream streamOut = null;
	private ChatReaderThread client = null;

	public ConsoleReader() {

		try {
			start();
			run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void connectToClientAtAddress(String serverName, int serverPort) {
		System.out.println("Establishing connection. Please wait ...");
		try {
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket);
			start();
			streamOut = new DataOutputStream(socket.getOutputStream());
			if (clientThread == null) {
				client = new ChatReaderThread(this, socket);
				clientThread = new Thread(this);
				clientThread.start();
			}
		} catch (UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage());
		} catch (IOException ioe) {
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
		
	}

	public String handleConsoleInput() {
		String consoleInput = "";
		try {
			consoleInput = console.readLine();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		//read args
		final int serverNameLength = 9;// localhost
		final int portNumberLength = 4; //xxxx
		String serverName = consoleInput.substring(0, serverNameLength);
		int portNumber = Integer.valueOf(consoleInput.substring(serverNameLength, serverNameLength+portNumberLength));
		//connect to client
		this.connectToClientAtAddress(serverName, portNumber);
		//encrypt msg to send
		String encryptedMsg = "";
		try {
			encryptedMsg = new String(AES.encrypt(consoleInput.substring(serverNameLength+portNumberLength)));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return encryptedMsg;
	}

	public void run() {
		//while (clientThread != null) {

			String encryptedText;
			try {
				encryptedText = this.handleConsoleInput();
				streamOut.writeUTF(encryptedText);
				streamOut.flush();
			} catch (IOException ioe) {
				System.out.println("Sending error: " + ioe.getMessage());
				stop();
			}

		//}
	}

	public void handle(String msg) {

		try {
			String decryptedInput = AES.decrypt(msg.getBytes());

			if (decryptedInput.equals(".bye")) {
				System.out.println("Good bye. Press RETURN to exit ...");
				stop();
			} else {
				System.out.println(decryptedInput);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void start() throws IOException {
		console = new BufferedReader(new InputStreamReader(System.in));
		
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		if (clientThread != null) {
			clientThread.stop();
			clientThread = null;
		}
		try {
			if (console != null)
				console.close();
			if (streamOut != null)
				streamOut.close();
			if (socket != null)
				socket.close();
		} catch (IOException ioe) {
			System.out.println("Error closing ...");
		}
		client.close();
		client.stop();
	}

	public static void main(String args[]) {
		@SuppressWarnings("unused")
		ConsoleReader client = new ConsoleReader();
		 
	}
}
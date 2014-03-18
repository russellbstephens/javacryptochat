package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import aes.AES;

public class ChatClient implements Runnable {
	private Socket socket = null;
	private Thread thread = null;
	private BufferedReader console = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;
	private final String AES_KEY = "87A5CF97F3B6ABCDA92A4B3CE0994DC3C68858798381ED1DFA2A1BFCCAE804C3";

	public ChatClient(String serverName, int serverPort) {
		System.out.println("Establishing connection. Please wait ...");
		try {
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket);
			start();
		} catch (UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage());
		} catch (IOException ioe) {
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
	}

	public void run() {
		while (thread != null) {
			try {
				String encryptedText;
				try {
					encryptedText = new String(AES.encrypt(console.readLine(), this.getAESKeyBytes()));
					streamOut.writeUTF(encryptedText);
					streamOut.flush();
				} catch (IOException ioe) {
					System.out.println("Sending error: " + ioe.getMessage());
					stop();
				}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		}
	}

	public void handle(String msg) {
		
		try {
			String decryptedInput = AES.decrypt(msg.getBytes(), this.getAESKeyBytes());
			
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
		streamOut = new DataOutputStream(socket.getOutputStream());
		if (thread == null) {
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			thread.stop();
			thread = null;
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

	public byte[] getAESKeyBytes() {
		return this.AES_KEY.getBytes();
	}
	
	public static void main(String args[]) {
		ChatClient client = new ChatClient("localhost", 7891);
	}
}

class ChatClientThread extends Thread {
	private Socket socket = null;
	private ChatClient client = null;
	private DataInputStream streamIn = null;

	public ChatClientThread(ChatClient _client, Socket _socket) {
		client = _client;
		socket = _socket;
		open();
		start();
	}

	public void open() {
		try {
			streamIn = new DataInputStream(socket.getInputStream());
		} catch (IOException ioe) {
			System.out.println("Error getting input stream: " + ioe);
			client.stop();
		}
	}

	public void close() {
		try {
			if (streamIn != null)
				streamIn.close();
		} catch (IOException ioe) {
			System.out.println("Error closing input stream: " + ioe);
		}
	}

	public void run() {
		while (true) {
			try {
				client.handle(streamIn.readUTF());
			} catch (IOException ioe) {
				System.out.println("Listening error: " + ioe.getMessage());
				client.stop();
			}
		}
	}
}
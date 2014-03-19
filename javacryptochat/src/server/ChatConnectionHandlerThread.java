package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import aes.AES;

public class ChatConnectionHandlerThread extends Thread {

	private ChatClient server = null;
	private Socket socket = null;
	private int ID = -1;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;

	public ChatConnectionHandlerThread(ChatClient _server, Socket _socket) {
		super();
		server = _server;
		socket = _socket;
		ID = socket.getPort();
	}

	public int getID() {
		return ID;
	}

	public synchronized void handle(int ID, String input) {
		try {
			String decryptedInput = AES.decrypt(input.getBytes());
			final int IDENTIFIER_LENGTH = 10;
			
			String identifier = decryptedInput.substring(0, IDENTIFIER_LENGTH);
			String msg = decryptedInput.substring(IDENTIFIER_LENGTH);
			System.out.println(identifier + ": " + msg);

		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void run() {
		System.out.println("Chat Connection Thread " + ID + " running.");
		while (true) {
			try {
				handle(ID, streamIn.readUTF());
			} catch (IOException ioe) {
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}

	public void open() throws IOException {
		streamIn = new DataInputStream(new BufferedInputStream(
				socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(
				socket.getOutputStream()));
	}

	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (streamIn != null)
			streamIn.close();
		if (streamOut != null)
			streamOut.close();
	}
}
package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer implements Runnable

{
	private Socket socket = null;
	private Thread clientThread = null;
	private BufferedReader console = null;
	private DataOutputStream streamOut = null;
	private ChatConnectionHandlerThread client = null;
	public ChatConnectionHandlerThread clients[] = new ChatConnectionHandlerThread[50];
	private ServerSocket server = null;
	private Thread serverThread = null;
	public int clientCount = 0;
	private final String AES_KEY = "87A5CF97F3B6ABCDA92A4B3CE0994DC3C68858798381ED1DFA2A1BFCCAE804C3";

	public ChatServer(int port) {
		try {
			System.out
					.println("Binding to port " + port + ", please wait  ...");
			server = new ServerSocket(port);
			System.out.println("Server started: " + server);
			start();
		} catch (IOException ioe) {
			System.out.println("Can not bind to port " + port + ": "
					+ ioe.getMessage());
		}
	}

	public void run() {
		while (serverThread != null) {
			try {
				System.out.println("Waiting for a client ...");
				addThread(server.accept());
			} catch (IOException ioe) {
				System.out.println("Server accept error: " + ioe);
				stop();
			}
		}
	}

	public void start() {
		if (serverThread == null) {
			serverThread = new Thread(this);
			serverThread.start();
		}
	}

	public void stop() {
		if (serverThread != null) {
			serverThread.stop();
			serverThread = null;
		}
	}

	public int findClient(int ID) {
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getID() == ID)
				return i;
		return -1;
	}

	public synchronized void remove(int ID) {
		int pos = findClient(ID);
		if (pos >= 0) {
			ChatConnectionHandlerThread toTerminate = clients[pos];
			System.out.println("Removing client thread " + ID + " at " + pos);
			if (pos < clientCount - 1)
				for (int i = pos + 1; i < clientCount; i++)
					clients[i - 1] = clients[i];
			clientCount--;
			try {
				toTerminate.close();
			} catch (IOException ioe) {
				System.out.println("Error closing thread: " + ioe);
			}
			toTerminate.stop();
		}
	}

	private void addThread(Socket socket) {
		if (clientCount < clients.length) {
			System.out.println("Client accepted: " + socket);
			clients[clientCount] = new ChatConnectionHandlerThread(this, socket);
			try {
				clients[clientCount].open();
				clients[clientCount].start();
				clientCount++;
			} catch (IOException ioe) {
				System.out.println("Error opening thread: " + ioe);
			}
		} else
			System.out.println("Client refused: maximum " + clients.length
					+ " reached.");
	}

	public static void main(String args[]) {
		ChatServer server = new ChatServer(7891);
	}
	public byte[] getAESKeyBytes() {
		return this.AES_KEY.getBytes();
	}
}
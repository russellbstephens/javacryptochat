package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatClient implements Runnable

{
	@SuppressWarnings("unused")
	private Thread consoleReader;
	public ChatConnectionHandlerThread clients[] = new ChatConnectionHandlerThread[50];
	private ServerSocket server = null;
	private Thread serverThread = null;
	public int clientCount = 0;

	public ChatClient(int port) {
		this.removeCryptographyRestrictions();
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
		
		this.consoleReader = new Thread(new ConsoleReader());
	}

	private void removeCryptographyRestrictions() {
	    if (!isRestrictedCryptography()) {
	        return;
	    }
	    try {
	        java.lang.reflect.Field isRestricted;
	        try {
	            final Class<?> c = Class.forName("javax.crypto.JceSecurity");
	            isRestricted = c.getDeclaredField("isRestricted");
	        } catch (final ClassNotFoundException e) {
	            try {
	                // Java 6 has obfuscated JCE classes
	                final Class<?> c = Class.forName("javax.crypto.SunJCE_b");
	                isRestricted = c.getDeclaredField("g");
	            } catch (final ClassNotFoundException e2) {
	                throw e;
	            }
	        }
	        isRestricted.setAccessible(true);
	        isRestricted.set(null, false);
	    } catch (final Throwable e) {
	        System.err.println(
	                "Failed to remove cryptography restrictions"+ e.getMessage());
	    }
	}

	private static boolean isRestrictedCryptography() {
	    return "Java(TM) SE Runtime Environment"
	            .equals(System.getProperty("java.runtime.name"));
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

	@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
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
		@SuppressWarnings("unused")
		ChatClient server = new ChatClient(7891);
	}

}
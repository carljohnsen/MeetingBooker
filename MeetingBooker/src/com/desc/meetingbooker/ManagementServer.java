package com.desc.meetingbooker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.util.Log;

/**
 * A class, that defines a socket server, that listens on port 5000. 
 * 
 * @author Carl Johnsen
 * @version 1.6
 * @since 03-07-2014
 */
public class ManagementServer {
	
	protected int port;
	protected ServerSocket serverSocket;
	private final static String TAG = ManagementServer.class.getSimpleName();
	
	/**
	 * The constructor for a new ManagementServer object.
	 * 
	 * @param port The port that the server accepts connections on.
	 */
	public ManagementServer(final int port) {
		this.port = port;
	}
	
	/**
	 * Registers the tablet on the logging server, and states its name and listening port.
	 */
	private void register() {
		try {
			Socket socket = new Socket(StatMeth.logServer, 5000);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.write("SERVER\r\n");
			out.write(MainActivity.roomName + "\r\n");
			out.write(port + "\r\n");
			out.write("\r\n");
			out.flush();
			
			String response = in.readLine();
			if (!response.equals("OK") || in.readLine().equals("")) {
				throw new IOException();
			}
			
			// Close the streams and the socket
			in.close();
			out.close();
			socket.close();
		} catch (IOException ioe) {
			Log.e(TAG, "IOException in register()!" + ioe.getMessage());
		}
	}
	
	/**
	 * The method, which starts the server loop, i.e. keeps accepting connections, 
	 * until the application closes. 
	 */
	protected void serverLoop() {
		if (StatMeth.logServer != null || !StatMeth.logServer.equals("not_set")) {
			register();
		} else {
			return;
		}
		try {
			serverSocket = new ServerSocket(port); 
			while (true) {
				Socket socket = serverSocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				new ServerThread(socket, in, out).start();
			}
		} catch (IOException ioe) {
			Log.e(TAG, "IOException in serverLoop!" + ioe.getMessage());
		}
	}

	/**
	 * A thread container, which handles the interpretation of data from a socket,
	 * and the response to a socket
	 * 
	 * @author Carl Johnsen
	 * @version 1.6
	 * @since 03-07-2014
	 */
	private class ServerThread extends Thread {
		
		private final BufferedReader in;
		private final PrintWriter out;
		private final Socket socket;
		
		/**
		 * The constructor for a ServerThread Object.
		 * Assumes that the connection have already been established.
		 * 
		 * @param socket The socket to the client
		 * @param in The input stream from the socket
		 * @param out The output stream from the socket
		 */
		public ServerThread(final Socket socket, final BufferedReader in, final PrintWriter out) {
			this.in = in;
			this.out = out;
			this.socket = socket;
		}
		
		/**
		 * Interprets data from the input stream.
		 * Input is interpreted as follows: first a greeting "HELLO " + tablet name + "\r\n",
		 * so that the application is sure, that the connection is from the logging server.
		 * Then a request in the form request + "\r\n" (for example "get\r\n" to indicate a get request)
		 * Then, if required, comes the element in the format "element\r\n". 
		 * Input closure is made in a blank line, with "\r\n".
		 * 
		 * @param in The input stream from the socket
		 * @param out The output stream from the socket
		 * @throws IOException Assumes the caller of this method, handles the exception
		 */
		private void interpret(final BufferedReader in, final PrintWriter out) throws IOException {
			String line = in.readLine();
			if (line.equals("HELLO " + MainActivity.roomName)) {
				String action = in.readLine();
				String element = in.readLine();
				if (action.equals("alive") && element.equals("")) {
					respondAlive(out);
					return;
				}
				if (action.equals("get") && !element.equals("") && in.readLine().equals("")) {
					respondGet(out, element);
					return;
				}
			}
		}
		
		/**
		 * Write an response to an 'alive' request
		 * 
		 * @param out The output stream from the socket
		 */
		private void respondAlive(final PrintWriter out) {
			out.write("yes\r\n");
			out.write("\r\n");
			out.flush();
		}
		
		/**
		 * Writes an response to an 'get' request
		 * 
		 * @param out The output stream from the socket
		 * @param element The requested element (for example "config" for the configuration file)
		 */
		private void respondGet(final PrintWriter out, final String element) {
			if (element.equals("config")) {
				ArrayList<Setting> settings = StatMeth.readConfig();
				for (Setting setting : settings) {
					out.write(setting.name + " " + setting.value + "\r\n");
				}
				out.write("\r\n");
				out.flush();
			}
		}
		
		/**
		 * The method that will run, when the thread is started.
		 * It calls interpret(), and closes all of the streams, and the socket upon return.
		 */
		public void run() {
			try {
				interpret(in, out);
				in.close();
				out.close();
				socket.close();
			} catch (IOException ioe) {
				Log.e(TAG, "IOException in run()!" + ioe.getMessage());
			}
		}
		
	}
}
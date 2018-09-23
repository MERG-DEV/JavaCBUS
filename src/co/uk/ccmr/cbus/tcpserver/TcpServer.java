/*
 * (c) Ian Hogg 2017
 */
/* This work is licensed under the:
      Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
   To view a copy of this license, visit:
      http://creativecommons.org/licenses/by-nc-sa/4.0/
   or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.

   License summary:
    You are free to:
      Share, copy and redistribute the material in any medium or format
      Adapt, remix, transform, and build upon the material

    The licensor cannot revoke these freedoms as long as you follow the license terms.

    Attribution : You must give appropriate credit, provide a link to the license,
                   and indicate if changes were made. You may do so in any reasonable manner,
                   but not in any way that suggests the licensor endorses you or your use.

    NonCommercial : You may not use the material for commercial purposes. **(see note below)

    ShareAlike : If you remix, transform, or build upon the material, you must distribute
                  your contributions under the same license as the original.

    No additional restrictions : You may not apply legal terms or technological measures that
                                  legally restrict others from doing anything the license permits.

   ** For commercial use, please contact the original copyright holder(s) to agree licensing terms

    This software is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE

**************************************************************************************************************
  Note:   This source code has been written using a tab stop and indentation setting
          of 4 characters. To see everything lined up correctly, please set your
          IDE or text editor to the same settings.
******************************************************************************************************
*/
package co.uk.ccmr.cbus.tcpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.driver.CbusDriver;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.sniffer.InvalidEventException;

/**
 * Implements a TCP server to accept in-bound TCP connections.
 * 
 * @author ianh
 *
 */
public class TcpServer implements Runnable, CbusReceiveListener {
	public static final int BUFSIZE = 1000;

	private static TcpServer me = null;
	
	private ServerSocket serverSocket = null;
	private static Thread listenerThread;
	private List<Socket> clients;

	private static CbusDriver theDriver;
	
	/**
	 * Create a new TCP server.
	 * 
	 * @param port the port on which to accept connections
	 * @throws IOException if there is a communications failure
	 */
	private TcpServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		clients = new ArrayList<Socket>();
	}
	
	/**
	 * Obtain the singleton of the TCP server. One is created if the singleton does not already exist.
	 * The singleton is connected to the specifed CbusDriver so that messages received by the driver are 
	 * sent to all the TCP client connections.
	 * 
	 * @param handler the Driver connected to the physical CBUS
	 * @param port the port on which to accept connections
	 * @return the singleton instance
	 * @throws IOException if there is a communications failure
	 */
	public static TcpServer getInstance(CbusDriver handler, int port) throws IOException {
		if (me == null) {
			theDriver = handler;
			me = new TcpServer(port);
			// start the thread
			listenerThread = new Thread(me);
			listenerThread.start();
			theDriver.addListener(me);
		}
		return me;
	}

	/**
	 * Run this TCP server so that in-bount connects are accpeted.
	 */
	@Override
	public void run() {
		System.out.println("accepting on server socket port="+serverSocket.getLocalPort());
		while (true) {
			try {
				Socket s = serverSocket.accept();
				System.out.println("Socket connected from:"+s.getRemoteSocketAddress());
				clients.add(s);
				Thread t = new Thread(new ServerSocketReader(s));
				t.start();
				// create the reader/writer threads
			} catch (IOException e) {
				break;
			}
		}
		
	}
	
	/**
	 * Called by the CbusDriver when a CBUS message is received.
	 * Performs no action.
	 * 
	 * @param ce the CBUS message
	 */
	@Override
	public void receiveMessage(CbusEvent ce) {
		
	}

	/**
	 * Called by the CbusDriver when a CBUS message is received. 
	 * Passes the message to all the client TCP connections.
	 * 
	 * @param input CBUS message
	 */
	@Override
	public void receiveString(String input) {
		System.out.println("TcpServer - got a message from CAN:"+input);
		List<Socket> toRemove = new ArrayList<Socket>();
		for (Socket s : clients) {
			try {
				OutputStream os = s.getOutputStream();
				
				os.write(input.getBytes());
			} catch (IOException e) {
				// remove from clients
				toRemove.add(s);
			}
		}
		for (Socket s : toRemove) {
			clients.remove(s);
		}
	}
	
	/**
	 * Close the server socket.
	 * Closes the TCP connections to all the clients.
	 */
	public void close() {
		if (serverSocket != null)
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		for (Socket c : clients) {
			try {
				c.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reads a TCP client socket and sends any CBUS messages to the CbusDriver.
	 * 
	 * @author ianh
	 *
	 */
	private class ServerSocketReader implements Runnable {
		private InputStream is;
		
		/**
		 * Create a ServerSocketReader.
		 * @param s the socket to be read
		 * @throws IOException if there are any communications failures
		 */
		public ServerSocketReader(Socket s) throws IOException {
			is = s.getInputStream();
		}

		/**
		 * Reads the socket and queues and CBUS messages received on the out-bound CbusDriver.
		 */
		@Override
		public void run() {
			byte [] buffer = new byte[BUFSIZE];
			try {
				while (true) {
					// read from socket	
					int cc = is.read(buffer, 0, BUFSIZE);
					if (cc <= 0) break;
					// create a Message
					String message = new String(buffer, 0, cc);

					CbusEvent ce = null;
					try {
						ce = new CbusEvent(message);
	System.out.println("Got a message from the server socket so queuing for transmit");
						theDriver.queueForTransmit(ce);
					} catch (InvalidEventException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// socket reset probably
			}
		}
	}
}

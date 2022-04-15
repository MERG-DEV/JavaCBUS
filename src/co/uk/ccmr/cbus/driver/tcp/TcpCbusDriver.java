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
package co.uk.ccmr.cbus.driver.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.driver.CbusCommsState;
import co.uk.ccmr.cbus.driver.CbusCommsStateListener;
import co.uk.ccmr.cbus.driver.CbusDriver;
import co.uk.ccmr.cbus.driver.CbusDriverException;
import co.uk.ccmr.cbus.driver.jsscSerial.ReaderThread;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.util.Options;

/**
 * The CBUS TCP driver used to communicate with another application software 
 * capable of accepting CBUS TCP connections such as TcpServer or the CANETHER module.
 * 
 * @author ianh
 *
 */
public class TcpCbusDriver implements CbusDriver {
	private final static int DEFAULT_PORT = 5555;
	private int port;
	private InetAddress ip;
	private Socket client;
	private OutputStream os;
	private Set<CbusReceiveListener> listeners;
	private Options options;
	private CbusCommsState cbusCommsState;
	private HashSet<CbusCommsStateListener> commsStateListeners;
	private SocketReader reader;
	private static final  Logger LOGGER = Logger.getLogger(ReaderThread.class.getName());
	private static final  Logger DRIVER_LOGGER = Logger.getLogger("Driver");
	
	
	/**
	 * Create an instance of the TCP driver.
	 */
	public TcpCbusDriver() {
		port = DEFAULT_PORT;
		cbusCommsState = CbusCommsState.DISCONNECTED;
		listeners = new HashSet<CbusReceiveListener>();
		commsStateListeners = new HashSet<CbusCommsStateListener>();		
	}
	
	/**
	 * Initialise the driver.
	 * 
	 * @param bus the bus number associated with this driver
	 * @param _log the StyledDocument to be used for logging
	 * @o the options
	 */
	@Override
	public void init(int bus, Options o) {
		options = o;
	}

	/**
	 * Connect to the specified TCP address. The portname mat be in any of the following formats:
	 * <OL>
	 * <LI>hostname:port</LI>
	 * <LI>IP address:port</LI>
	 * </OL>
	 * Note the normal port is 5550.
	 */
	@Override
	public void connect(String portName) throws CbusDriverException {
		String host = portName;
		port = DEFAULT_PORT;
		if (portName.contains(":")) {
			String [] parts = portName.split(":");
			try {
				ip = InetAddress.getByName(parts[0]);
			} catch (UnknownHostException e1) {
				throw new CbusDriverException(e1);
			}
			port = Integer.parseInt(parts[1]);
		} else {
			try {
				ip = InetAddress.getByName(host);
			} catch (UnknownHostException e) {
				throw new CbusDriverException(e);
			}
		}
		DRIVER_LOGGER.info("Opening port "+portName);
		
		try {
			client = new Socket(ip, port);
			os = client.getOutputStream();
		} catch (IOException e) {
			throw new CbusDriverException(e);
		}
		if (client == null) {
			System.out.println("Failed to create Client socket");
			throw new CbusDriverException("Failed to create Client socket");
		}
		if (os == null) {
			System.out.println("Failed to get OutputStream of Client socket");
			throw new CbusDriverException("Failed to get OutputStream of Client socket");
		}
		System.out.println("Socket connected to:"+portName);
		// create the reader Thread
		try {
			reader = new SocketReader(this, client, listeners, options);
		} catch (IOException e) {
			throw new CbusDriverException(e);
		}
		reader.start();
		setCbusCommsState(CbusCommsState.CONNECTED);
		DRIVER_LOGGER.info("Connectted to "+portName);
	}

	/**
	 * Close the TCP CBUS connection.
	 */
	@Override
	public void close() {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
			}
		}
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
			}
		}
		setCbusCommsState(CbusCommsState.DISCONNECTED);
		DRIVER_LOGGER.info("Disconnected TCP client connection");		
	}

	/**
	 * Add a CbusReceiveLisrtener.
	 * 
	 * @param crl the listener
	 */
	@Override
	public void addListener(CbusReceiveListener crl) {
		listeners.add(crl);
	}

	/**
	 * Remove a CbusReceiveLisrtener.
	 * 
	 * @param crl the listener
	 */
	@Override
	public void removeListener(CbusReceiveListener crl) {
		listeners.remove(crl);
	}

	/**
	 * Queue a CBUS event message for transmission. We don't actually queue it but try to send immediately.
	 * 
	 * @param ce the CBUS event message
	 */
	@Override
	public void queueForTransmit(CbusEvent ce) {
		System.out.println("TCP> "+ce.toString());
		System.out.println("TCP> "+ce.dump(16));
		if (getCbusCommsState() != CbusCommsState.CONNECTED) {
			DRIVER_LOGGER.info("> "+ce.toString());
			DRIVER_LOGGER.info("> "+ce.dump(options.getBase()));
			return;
		}
		try {
			DRIVER_LOGGER.info("> "+ce.toString());
			DRIVER_LOGGER.info("> "+ce.dump(options.getBase()));			
			os.write(ce.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			setCbusCommsState(CbusCommsState.DISCONNECTED);
		}
	}

	/**
	 * Obtain a list of port names to be offered as a choice to the user.
	 * These are obtained from the Options.
	 */
	@Override
	public String[] getPortNames() {
		return options.getPorts();
	}

	/* COMMS STATE */

	@Override
	public CbusCommsState getCbusCommsState() {
		return cbusCommsState;
	}

	public void setCbusCommsState(CbusCommsState cs) {
		if (cbusCommsState != cs) {
			cbusCommsState = cs;
			for (CbusCommsStateListener ccsl : commsStateListeners) {
				ccsl.cbusCommsStateChanged(cs);
			}
		}
	}

	@Override
	public void addCbusCommsStateListener(CbusCommsStateListener ccsl) {
		commsStateListeners.add(ccsl);
	}

	@Override
	public void removeCbusCommsStateListener(CbusCommsStateListener ccsl) {
		commsStateListeners.remove(ccsl);
	}

	/* STATISTICS */
	@Override
	public int getReaderQueueSize() {
		return reader != null ? reader.getQueueSize() : 0;
	}

	@Override
	public int getWriterQueueSize() {
		// There is no write queue
		return 0;
	}

}

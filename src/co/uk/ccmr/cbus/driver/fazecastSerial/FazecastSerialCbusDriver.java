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
package co.uk.ccmr.cbus.driver.fazecastSerial;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.fazecast.jSerialComm.SerialPort;

import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.driver.CbusCommsState;
import co.uk.ccmr.cbus.driver.CbusCommsStateListener;
import co.uk.ccmr.cbus.driver.CbusDriver;
import co.uk.ccmr.cbus.driver.CbusDriverException;

import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.util.Options;

/**
 * The CbusDriver implementation for serial ports. Interfaces to a RS232 or usb serial port and handles CBUS 
 * communications. The Fazecast serial driver is used.
 * 
 * @author ianh
 *
 */
public class FazecastSerialCbusDriver implements CbusDriver {
	private BlockingQueue<CbusEvent> writeQueue;
	private SerialPort serialPort;
	private ReaderThread reader;
	private WriterThread writer;
	private List<CbusReceiveListener> listeners;
	private Options options;
	private CbusCommsState cbusCommsState;
	private Set<CbusCommsStateListener> commsStateListeners;
	private static final  Logger LOGGER = Logger.getLogger(ReaderThread.class.getName());
	private static final  Logger DRIVER_LOGGER = Logger.getLogger("Driver");
	
	/**
	 * Creates the serial port driver.
	 */
	public FazecastSerialCbusDriver() {
		cbusCommsState = CbusCommsState.DISCONNECTED;
		commsStateListeners = new HashSet<CbusCommsStateListener>();
		writeQueue = new LinkedBlockingQueue<CbusEvent>();
		listeners = new CopyOnWriteArrayList<CbusReceiveListener>();
	}
	
	/**
	 * Initialise the driver. Creates the reader and writer threads.
	 * 
	 * @param bus the bus number this driver is associated with
	 * @param _log the StyledDocument to be used for logging
	 * @param o the options
	 */
	public void init (int bus, Options o) {
		options = o;
		// create the threads
		reader = new ReaderThread(this, serialPort, listeners);
		reader.start();
		writer = new WriterThread(this, serialPort, writeQueue, options);
		writer.start();
	}
	
	/**
	 * Connect to the specified port.
	 * 
	 * @param portName the name of the port to connect to
	 * @throws CbusDriverException if the port cannot be opened
	 */
	public void connect(String name) throws CbusDriverException {
		
		if (serialPort != null) {
			serialPort.closePort();
			serialPort = null;
			setCommsState(CbusCommsState.DISCONNECTED);
		}
		if (reader != null) {
			reader.terminate();
			reader.interrupt();
			reader = null;
		}
		if (writer != null) {
			writer.terminate();
			writer.interrupt();
			writer = null;
		}
		DRIVER_LOGGER.info("Opening port "+name);
		System.out.println("CONNECT="+name);
		serialPort = SerialPort.getCommPort(name);	

		if (! serialPort.openPort()) {
			System.out.println("Failed to open port");
			throw new CbusDriverException("Failed to open port");
		}
		// set for blocking
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
 
		// create the threads
		reader = new ReaderThread(this, serialPort, listeners);
		reader.start();
		writer = new WriterThread(this, serialPort, writeQueue, options);
		writer.start();
		setCommsState(CbusCommsState.CONNECTED);
	}
	
	/**
	 * Close the port.
	 */
	public void close() {
		if (serialPort != null) {
			serialPort.closePort();
			serialPort = null;
		}
		if (reader != null) {
			reader.terminate();
			reader = null;
		}
		if (writer != null) {
			writer.terminate();
			writer = null;
		}
		setCommsState(CbusCommsState.DISCONNECTED);
	}
	
	/**
	 * Add a CBUS receive message listener.
	 * 
	 * @param crl the listener
	 */
	public void addListener(CbusReceiveListener crl) {
		listeners.add(crl);
	}
	
	/**
	 * Remove a CBUS receive message listener.
	 * 
	 * @param crl the listener
	 */
	public void removeListener(CbusReceiveListener crl) {
		listeners.remove(crl);
	}
	
	/**
	 * Queue a CBUS event message to be transmitted.
	 * 
	 * @param ce the CBUS event message
	 */
	public void queueForTransmit(CbusEvent ce) {
		System.out.println("Serial > "+ce.dump(16));
		try {
			writeQueue.put(ce);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	

	/**
	 * Get the list port names. For a serial port this is the list of connected RS232 or usb ports.
	 */
	@Override
	public String[] getPortNames() {
		SerialPort [] ports = SerialPort.getCommPorts();
		String [] portNames = new String[ports.length];
		for (int i=0; i<ports.length; i++) {
			portNames[i] = ports[i].getSystemPortName();
		}
		return portNames;
	}
	
	/* COMMS STATE */
	@Override
	public CbusCommsState getCbusCommsState() {
		return cbusCommsState;
	}

	public void setCommsState(CbusCommsState cs) {
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
		// No reader queue
		return 0;
	}

	@Override
	public int getWriterQueueSize() {
		return writeQueue.size();
	}
}

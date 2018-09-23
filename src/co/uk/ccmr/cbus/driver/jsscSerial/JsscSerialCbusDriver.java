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
package co.uk.ccmr.cbus.driver.jsscSerial;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.driver.CbusCommsState;
import co.uk.ccmr.cbus.driver.CbusCommsStateListener;
import co.uk.ccmr.cbus.driver.CbusDriver;
import co.uk.ccmr.cbus.driver.CbusDriverException;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.util.Options;

/**
 * The CbusDriver implementation for serial ports. Interfaces to a RS232 or usb serial port and handles CBUS 
 * communications.
 * 
 * @author ianh
 *
 */
public class JsscSerialCbusDriver implements SerialPortEventListener, CbusDriver {
	private BlockingQueue<CbusEvent> writeQueue;
	private SerialPort serialPort;
	private StyledDocument log;
	private ReaderThread reader;
	private WriterThread writer;
	private List<CbusReceiveListener> listeners;
	private Options options;
	private AttributeSet redAset;
	private CbusCommsState cbusCommsState;
	private Set<CbusCommsStateListener> commsStateListeners;
	
	/**
	 * Creates the serial port driver.
	 */
	public JsscSerialCbusDriver() {
		cbusCommsState = CbusCommsState.DISCONNECTED;
		commsStateListeners = new HashSet<CbusCommsStateListener>();
		writeQueue = new LinkedBlockingQueue<CbusEvent>();
		listeners = new CopyOnWriteArrayList<CbusReceiveListener>();
		StyleContext sc = StyleContext.getDefaultStyleContext();
    	redAset = sc.addAttribute(SimpleAttributeSet.EMPTY,
    	                                        StyleConstants.Foreground, Color.red);
	}
	
	/**
	 * Initialise the driver. Creates the reader and writer threads.
	 * 
	 * @param bus the bus number this driver is associated with
	 * @param _log the StyledDocument to be used for logging
	 * @param o the options
	 */
	public void init (int bus, StyledDocument _log, Options o) {
		options = o;
		log = _log;
		// create the threads
		reader = new ReaderThread(this, serialPort, listeners, log);
		reader.start();
		writer = new WriterThread(this, serialPort, writeQueue, log, options);
		writer.start();
	}
	
	/**
	 * Connect to the specified port.
	 * 
	 * @param portName the name of the port to connect to
	 * @throws CbusDriverException if the port cannot be opened
	 */
	public void connect(String portName) throws CbusDriverException {
		
		if (serialPort != null) {
			try {
				serialPort.closePort();
			} catch (SerialPortException e) {
				throw new CbusDriverException(e);
			}
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
		try {
			if (log != null) log.insertString(0, "Opening port "+portName+"\n", redAset);
		} catch (BadLocationException e2) {
			e2.printStackTrace();
		}
		serialPort = new SerialPort(portName);	

		try {
			if (! serialPort.openPort()) {
				System.out.println("Failed to open port");
				System.exit(1);
			}
			serialPort.setEventsMask(SerialPort.MASK_BREAK | SerialPort.MASK_CTS | SerialPort.MASK_DSR | SerialPort.MASK_ERR |
					SerialPort.MASK_RING | SerialPort.MASK_RLSD | SerialPort.MASK_RXFLAG );
			serialPort.setDTR(true);
			serialPort.setRTS(true);
			
			serialPort.addEventListener(this);
		} catch (SerialPortException e) {
			throw new CbusDriverException(e);
		}
 
		// create the threads
		reader = new ReaderThread(this, serialPort, listeners, log);
		reader.start();
		writer = new WriterThread(this, serialPort, writeQueue, log, options);
		writer.start();
		setCommsState(CbusCommsState.CONNECTED);
	}
	
	/**
	 * Close the port.
	 */
	public void close() {
		if (serialPort != null) {
			try {
				serialPort.closePort();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
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
	 * Gets called if something went wrong with the serial port.
	 * Print the fault to the log in red.
	 * 
	 * @param event information about the serial port event
	 */
	@Override
    public void serialEvent(SerialPortEvent event) {
        String err = "EVENT:"+eventTypeToString(event.getEventType())+" Value="+event.getEventValue();
        try {
        	if (log != null) log.insertString(0, "> "+err+"\n", redAset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * Convert the integer event type to a string.
	 * 
	 * @param et event type
	 * @return string name of event type
	 */
	private String eventTypeToString(int et) {
		switch(et) {
		case SerialPortEvent.BREAK:
			return "BREAK";
		case SerialPortEvent.CTS:
			return "CTS";
		case SerialPortEvent.DSR:
			return "DSR";
		case SerialPortEvent.ERR:
			return "ERR";
		case SerialPortEvent.RING:
			return "RING";
		case SerialPortEvent.RLSD:
			return "RLSD";
		case SerialPortEvent.RXCHAR:
			return "RXCHAR";
		case SerialPortEvent.RXFLAG:
			return "RXFLAG";
		case SerialPortEvent.TXEMPTY:
			return "TXEMPTY";
		}
		return "Unknown";
	}

	/**
	 * Get the list port names. For a serial port this is the list of connected RS232 or usb ports.
	 */
	@Override
	public String[] getPortNames() {
		return SerialPortList.getPortNames();
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

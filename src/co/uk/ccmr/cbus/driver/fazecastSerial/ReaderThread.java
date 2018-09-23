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

import java.awt.Color;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.fazecast.jSerialComm.SerialPort;

import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.driver.CbusCommsState;
import co.uk.ccmr.cbus.driver.TerminatingThread;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.sniffer.InvalidEventException;


/**
 * Reads a Serial port calling the list of CbusReceiveListeners for each CBUS message received.
 * 
 * @author ianh
 *
 */
public class ReaderThread extends TerminatingThread {
	private SerialPort serialPort;
	private StyledDocument log;
	private List<CbusReceiveListener> listeners;
	private AttributeSet redAset;
	private FazecastSerialCbusDriver driver;

	/**
	 * Create a serial port ReaderThread. 
	 * 
	 * @param serialPort the serial port to be read.
	 * @param listeners the list of CbusReceiveListeners to be called when a CBUS message is received
	 * @param log the StyledDocument to be used for logging
	 */
	public ReaderThread(FazecastSerialCbusDriver driver, SerialPort serialPort, List<CbusReceiveListener> listeners, StyledDocument log) {
		this.serialPort = serialPort;
		this.listeners = listeners;
		this.log = log;
		this.driver = driver;
		StyleContext sc = StyleContext.getDefaultStyleContext();
    	redAset = sc.addAttribute(SimpleAttributeSet.EMPTY,
    	                                        StyleConstants.Foreground, Color.RED);
	}
	
	/**
	 * Run the ReaderThread.
	 */
	@Override
	public void run() {
		System.out.println("SerialPort.ReaderThread starting serialport="+serialPort);
		String input="";
    	try {

			if (serialPort != null) {
				if (log != null) log.insertString(0, "READING from "+serialPort.getSystemPortName()+"\n", redAset);
			} else {
				if (log != null) log.insertString(0, "READING from nowhere\n", redAset);
				System.out.println("SerialPort.ReaderThread terminating serialport="+serialPort);
				return;
			}
		} catch (BadLocationException e2) {
			e2.printStackTrace();
		}
		while (!terminate) {			
   			byte[] inb = new byte[1];
   			int cc = serialPort.readBytes(inb, 1);
   			if (cc != 1){
   				try {
   					if (log != null) log.insertString(0, "Error reading from Serial Port "+serialPort.getSystemPortName()+"\n", redAset);
   				} catch (BadLocationException e2) {
   					e2.printStackTrace();
   				}
   				break;
   			}
   			if (cc == 0) {
   				// shouldn't happen
   				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
   				continue;
   			}
				
   			String c = new String(inb);
   			System.out.println("Fazecast reader:"+c);
   			if (":".equals(c)) {
   				input = "";
   			}
   			input += c;
   			if (";".equals(c)) {
   				// Send event to the listeners
   				CbusEvent ce;
   				try {
   					ce = new CbusEvent(input);
   					for (CbusReceiveListener ml : listeners) {
   						ml.receiveMessage(ce);
   					}
   					System.out.println("< "+ce.dump(16));
   				} catch (InvalidEventException e) {
   					System.err.println("input="+input);
   					e.printStackTrace();
   					for (CbusReceiveListener ml : listeners) {
   						ml.receiveString(input);
   					}
   				}
   				input = "";
   			} 
    	}
		System.out.println("SerialPort.ReaderThread terminating serialport="+serialPort);
		driver.setCommsState(CbusCommsState.DISCONNECTED);
		if (serialPort != null) {
			try {
				if (log != null) log.insertString(0, "TERMINATED READING from "+serialPort.getSystemPortName()+"\n", redAset);
			} catch (BadLocationException e2) {
				e2.printStackTrace();
			}
		}
	}

}

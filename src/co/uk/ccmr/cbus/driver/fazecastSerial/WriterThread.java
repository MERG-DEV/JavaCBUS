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
import java.util.concurrent.BlockingQueue;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.fazecast.jSerialComm.SerialPort;

import co.uk.ccmr.cbus.driver.CbusCommsState;
import co.uk.ccmr.cbus.driver.TerminatingThread;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.util.Options;

/**
 * A WriterThread for writing queued messages to the physical serial port.
 * 
 * @author ianh
 *
 */
public class WriterThread extends TerminatingThread {
	private SerialPort serialPort;
	private BlockingQueue<CbusEvent> q;
	private StyledDocument log;
	private Options options;
	private AttributeSet redAset;
	private AttributeSet yellowAset;
	private FazecastSerialCbusDriver driver;

	/**
	 * Create the WriterThread.
	 * @param serialPort the physical serial port
	 * @param q the queue of messages to be transmitted
	 * @param log the StyledDocument to be used for logging
	 * @param o the Options
	 */
	public WriterThread(FazecastSerialCbusDriver driver, SerialPort serialPort, BlockingQueue<CbusEvent> q, StyledDocument log, Options o) {
		this.serialPort = serialPort;
		this.q = q;
		this.log = log;
		this.driver = driver;
		StyleContext sc = StyleContext.getDefaultStyleContext();
    	redAset = sc.addAttribute(SimpleAttributeSet.EMPTY,
    	                                        StyleConstants.Foreground, Color.red);
    	yellowAset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, Color.orange);
		options = o;
	}
	
	/**
	 * Run this Thread to get CBUS messages from the queue and write them to the serial port. 
	 */
	@Override
	public void run() {
		System.out.println("Writer thread running serialport="+serialPort);
		try {
			if (serialPort != null) {
				if (log != null) log.insertString(log.getLength(), "WRITING to "+serialPort.getSystemPortName()+"\n", redAset);
				System.out.println("WRITING to "+serialPort.getSystemPortName());
			} else {
				if (log != null) log.insertString(log.getLength(), "WRITING to nowhere\n", redAset);
				System.out.println("WRITING to nowhere");
			}
		} catch (BadLocationException e2) {
			e2.printStackTrace();
		}
		
		while (!terminate) {
			CbusEvent ce;
			try {
				ce = q.take();
				System.out.println("> "+ce.toString());
				System.out.println("> "+ce.dump(16));
				if (serialPort == null) {
					// not connected
					try {
						if (log != null) log.insertString(log.getLength(), "> "+ce.toString()+"\n", yellowAset);
						if (log != null) log.insertString(log.getLength(), "> "+ce.dump(options.getBase())+"\n", yellowAset);
						
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				} else {
					// connected
					try {
						if (log != null) log.insertString(log.getLength(), "> "+ce.toString()+"\n", null);
						if (log != null) log.insertString(log.getLength(), "> "+ce.dump(options.getBase())+"\n", null);
						
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					byte[] bytes = ce.toString().getBytes();
					serialPort.writeBytes(bytes, bytes.length);
				}
			} catch (Exception e) {
				if (! (e instanceof InterruptedException)) {
					e.printStackTrace();
				}
				break;
			}
		}
		System.out.println("SerialPort. WriterThread terminating serialport="+serialPort);
		driver.setCommsState(CbusCommsState.DISCONNECTED);
		terminate = true;
		if (serialPort != null) {
			try {
				if (log != null) log.insertString(log.getLength(), "TERMINATED WRITING to "+serialPort.getSystemPortName()+"\n", redAset);
			} catch (BadLocationException e2) {
				e2.printStackTrace();
			}
		}
	}
}

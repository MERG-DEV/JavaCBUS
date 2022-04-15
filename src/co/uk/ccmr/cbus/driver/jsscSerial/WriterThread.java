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

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import jssc.SerialPort;
import jssc.SerialPortException;
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
	private Options options;
	private JsscSerialCbusDriver driver;
	
	private static final  Logger LOGGER = Logger.getLogger(WriterThread.class.getName());
	private static final  Logger DRIVER_LOGGER = Logger.getLogger("Driver");

	/**
	 * Create the WriterThread.
	 * @param serialPort the physical serial port
	 * @param q the queue of messages to be transmitted
	 * @param log the StyledDocument to be used for logging
	 * @param o the Options
	 */
	public WriterThread(JsscSerialCbusDriver driver, SerialPort serialPort, BlockingQueue<CbusEvent> q, Options o) {
		this.serialPort = serialPort;
		this.q = q;
		this.driver = driver;
		options = o;
	}
	
	/**
	 * Run this Thread to get CBUS messages from the queue and write them to the serial port. 
	 */
	@Override
	public void run() {
		System.out.println("Writer thread running serialport="+serialPort);
		if (serialPort != null) {
			DRIVER_LOGGER.info("WRITING to "+serialPort.getPortName());
			System.out.println("WRITING to "+serialPort.getPortName());
		} else {
			DRIVER_LOGGER.info("WRITING to nowhere");
			System.out.println("WRITING to nowhere");
		}
		
		while (!terminate) {
			try {
				CbusEvent ce = q.take();
				System.out.println("> "+ce.toString());
				System.out.println("> "+ce.dump(16));
				try {
					if (serialPort == null) {
						DRIVER_LOGGER.info("> "+ce.toString());
						DRIVER_LOGGER.info("> "+ce.dump(options.getBase()));
					} else {
						// not connected
						DRIVER_LOGGER.info("> "+ce.toString());
						DRIVER_LOGGER.info("> "+ce.dump(options.getBase()));
						serialPort.writeString(ce.toString());
					}
				} catch (SerialPortException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			DRIVER_LOGGER.info("TERMINATED WRITING to "+serialPort.getPortName());
		}
	}
}

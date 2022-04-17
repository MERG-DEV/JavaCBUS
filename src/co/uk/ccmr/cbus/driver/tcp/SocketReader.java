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

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.driver.CbusCommsState;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.sniffer.InvalidEventException;
import co.uk.ccmr.cbus.util.Options;

/**
 * A Runnable to read CBUS messages from a TCP socket and call the registered CbusReceiveListeners.
 * 
 * @author ianh
 *
 */
public class SocketReader extends Thread  {
	private InputStream is;
	private Set<CbusReceiveListener> listeners;
	private TcpCbusDriver driver;
	private static final  Logger LOGGER = Logger.getLogger(SocketReader.class.getName());
	private static final  Logger DRIVER_LOGGER = Logger.getLogger("Driver");
	

	/**
	 * Create the SocketReader.
	 * 
	 * @param client the socket to be read
	 * @param listeners the CbusReceiveListeners to be notified when a CBUS message is received on the socket
	 * @throws IOException if there is a communications failure
	 */
	public SocketReader(TcpCbusDriver driver, Socket client, Set<CbusReceiveListener> listeners, Options o) throws IOException {
		is = client.getInputStream();
		this.listeners = listeners;
		this.driver = driver;
	}

	/**
	 * Run the thread to read from the socket and call the CbusReceiveListeners.
	 */
	@Override
	public void run() {
		byte [] buffer;
		buffer = new byte[1024];

		DRIVER_LOGGER.info("READING from TCP");
		
		while(true) {
			int cc = 0;
			try {
				cc = is.read(buffer, 0, 1024);
			} catch (IOException e) {
				break;
			}
			if (cc <= 0) break;
			String cmd = new String(buffer, 0, cc);
			CbusEvent ce = null;
			try {
				ce = new CbusEvent(cmd);
			} catch (InvalidEventException e) {
				e.printStackTrace();
			}
			DRIVER_LOGGER.log(Level.INFO, "<"+ce.dump(16), Color.GREEN);
			for (CbusReceiveListener l : listeners) {
				l.receiveMessage(ce);
			}
		}
		driver.setCbusCommsState(CbusCommsState.DISCONNECTED);
		DRIVER_LOGGER.log(Level.INFO, "Disconnected", Color.RED);
	}

	public int getQueueSize() {
		// No receive queue
		return 0;
	}

}

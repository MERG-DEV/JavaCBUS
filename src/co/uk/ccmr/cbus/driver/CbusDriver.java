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
package co.uk.ccmr.cbus.driver;

import javax.swing.text.StyledDocument;

import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.util.Options;

/**
 * A CbusDriver represents the definition of the interface of operations that can be done to 
 * set up a communications channel for CBUS.
 * The interface defines methods for querying the concrete implementation's port names,
 * to connect and close down the communications channel and to send and receive CBUS messages.
 */ 
public interface CbusDriver {
	/**
	 * Initialse this communications interface.
	 *
	 * @param bus the bus identifier for this communications channel. Allows the application software to
	 * support multiple physical and logical CBUS connections
	 * @param _log the StyledDocument used for logging
	 * @param o the set of options for this interface
	 */
	public void init (int bus, StyledDocument _log, Options o);
	/**
	 * Make a connection to the specified port.
	 *
	 * @param portName the name of the port to connect to. The set of allowed names is dependent upon
	 * the particular concrete implementation
	 * @throws CbusDriverException if a connection cannot be made or the partname is invalid
	 */
	public void connect(String portName) throws CbusDriverException;
	/**
	 * Close the communications channel.
	 */
	public void close();
	public CbusCommsState getCbusCommsState();
	public void addCbusCommsStateListener(CbusCommsStateListener ccsl);
	public void removeCbusCommsStateListener(CbusCommsStateListener ccsl);
	
	/**
	 * Add a receive message listener which is called upon a CBUS message being received on this
	 * communications channel.
	 *
	 * @param crl the CbusReceiveListener to receive the message
	 */
	public void addListener(CbusReceiveListener crl);
	/**
	 * Remove a previously registered receive message listener.
	 *
	 * @param crl the CbusReceiveListener to remove
	 */
	public void removeListener(CbusReceiveListener crl);
	
	/**
	 * Add the CbusEvent message onto the queue of messages to be transmitted. 
	 * This is a normally a non-blocking operation.
	 *
	 * @param ce the cbus message to be transmitted
	 */
	public void queueForTransmit(CbusEvent ce);
	
	/**
	 * Get a list of implementation specific port names that are available to be used. Not all implementations support
	 * this so an empty list may be obtained. Typically this is used for physical ports such as serial usb ports whereas 
	 * a list of logical TCP connectons cannot be determined.
	 * 
	 * @return a list of available port names
	 */
	public String[] getPortNames();
	
	/**
	 * Return the size of the Reader queue if one exists.
	 * @return reader queue size
	 */
	public int getReaderQueueSize();
	/**
	 * Return the size of the Writer queue if one exists.
	 * @return writer queue size
	 */
	public int getWriterQueueSize();

}

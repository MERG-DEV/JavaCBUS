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
package co.uk.ccmr.cbus.util;

/**
 * A set of CBUS application options.
 */
public interface Options {
	/**
	 * Get the base/radix used when logging numbers as strings. Typically this will be either 10 (decimal) or 16 (hexadecimal).
	 *
	 * @return the base in which to display numbers
	 */
	public int getBase();

	/**
	 * Get the name of the default driver.
	 *
	 * @return the name of the driver
	 */
	public String getDriver();

	/**
	 * Get the list of available ports to be available for selection.
	 *
	 * @return the list of port names
	 */
	public String [] getPorts();
	
	/**
	 * Get the part name to attempt to open automatically.
	 *
	 * @return the automatic port name
	 */
	public String getAutoConnect();
	
	/**
	 * Indicates if feedback has been globally disabled. Useful for testing without hardware.
	 */
	public boolean getNoFeedback();
	/**
	 * Indicates if sync has been globally disabled. Useful for testing without hardware.
	 */
	public boolean getNoSync();
	/**
	 * Indicates if detectors have been globally disabled. Useful for testing without hardware.
	 */
	public boolean getNoDetectors();
	/**
	 * Indicates if we should try to reconnect comms. Useful for testing without hardware.
	 */
	public boolean getNoReconnect();

	/**
	 * Return some help.
	 */
	public String usage();
}

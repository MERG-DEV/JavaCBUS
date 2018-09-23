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

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * Provides an implementation of the Options interface 
 * for loading options from a properties file in the user's home directory.
 */
public class OptionsImpl implements Options {
	private static final String CBUSIO_PROPERTIES = "/cbusioProperties.txt";
	private static OptionsImpl _instance = null;
	private Properties props;
	private String propertyFilename;
	private boolean noFeedback = false;
	private boolean noSync = false;
	private boolean noDetectors = false;
	private boolean noReconnect = false;

	/**
	 * Create an implementation of Options. Loads the properties file.
	 * 
	 * @param log the StyledDocument to be used for logging.
	 */
	private OptionsImpl(StyledDocument log, String [] args) {
		props = new Properties();
		propertyFilename = System.getProperty("user.dir");
		propertyFilename += CBUSIO_PROPERTIES;
		
		System.out.println("Using properties:"+propertyFilename);
		try {
			StyleContext sc = StyleContext.getDefaultStyleContext();
	    	AttributeSet redAset = sc.addAttribute(SimpleAttributeSet.EMPTY,
	    	                                        StyleConstants.Foreground, Color.RED);
			if (log != null) log.insertString(0, "Using properties:"+propertyFilename+"\n", redAset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		FileInputStream is;
		try {
			is = new FileInputStream(propertyFilename);
			props.load(is);
			is.close();
		} catch (FileNotFoundException e) {
			System.out.println(propertyFilename+ " not found\n");
		} catch (IOException e) {
			System.out.println(propertyFilename+ " io exception\n");
		}
		if (args != null) {
			for (String a : args) {
				if ("-fb".equals(a)) noFeedback = true;
				if ("-sync".equals(a)) noSync = true;
				if ("-det".equals(a)) noDetectors = true;
				if ("-reconnect".equals(a)) noReconnect = true;
			}
		}
	}

	@Override
	public String usage() {
		return "[-fb] [-sync] [-det] [-reconnect]\n" +
				"-fb = no feedback from points/signals\n" +
				"-sync = don't send sync messages\n" +
				"-det = ignore Toti and location detectors\n" +
				"-reconnect = don't try to reconnect\n";
	}
	
	/**
	 * Return the singleton instance of the Options.
	 * 
	 * @param log StyledDocument to be used for logging.
	 * @oaram args command line arguments
	 * @return the singleton
	 */
	public static OptionsImpl getOptions(StyledDocument log, String [] args) {
		if (_instance == null) {
			_instance = new OptionsImpl(log, args);
		}
		return _instance;
	}
	public static Options getOptions() {
		return _instance;
	}
	
	/**
	 * Write the Options back to the properties file.
	 */
	private void save() {
		FileOutputStream os;
		try {
			os = new FileOutputStream(propertyFilename);
			props.store(os,  "CBUSIO properties");
			os.close();
		} catch (FileNotFoundException e) {
			System.out.println("Failed to save properties");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed to save properties");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Return the base/radix to be used for logging of numbers. Defaults to 16 if none is specified or the
	 * specified base is not 10.
	 * 
	 * @return the base
	 */
	public int getBase() {
		String v = props.getProperty("defaultBase");
		if (v == null) return 16;
		if (! "10".equals(v)) return 16;
		return 10;
	}
	
	/**
	 * Set the number base to be used for logging.
	 * @param b the base
	 */
	public void setBase(int b) {
		props.setProperty("defaultBase", ""+b);
		save();
	}
	
	/**
	 * Get the driver class name. Defaults to the FazecastSerialCbusDriver if none is specified.
	 * 
	 * @return the class name of the driver
	 */
	public String getDriver() {
		String v = props.getProperty("driver");
		if (v == null) return "co.uk.ccmr.cbus.driver.fazecastSerial.FazecastSerialCbusDriver";
		return v;
	}
	
	/**
	 * Get the list of available ports to be presented to the user as a selection. 
	 * 
	 * @return the list of available port names
	 */
	public String [] getPorts() {
		String v = props.getProperty("ports");
		if (v == null) return new String[0];
		return v.split(",");
	}

	@Override
	public String getAutoConnect() {
		return props.getProperty("autoconnect");

	}

	@Override
	public boolean getNoFeedback() {
		return noFeedback;
	}

	@Override
	public boolean getNoSync() {
		return noSync;
	}

	@Override
	public boolean getNoDetectors() {
		return noDetectors;
	}
	
	@Override
	public boolean getNoReconnect() {
		return noReconnect;
	}

}

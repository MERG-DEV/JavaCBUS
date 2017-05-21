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
package co.uk.ccmr.cbus.sniffer;


/**
 * Util class contains a number of utility static functions.
 *
 */
public class Util {
	/**
	 * Convert a byte to a hexadecimal String.
	 * @param b
	 * @return
	 */
	public static String byteToHex(int b) {
		if ((b < 0) || (b > 0xFF)) throw new IllegalArgumentException("byte out of range");
		int lowNibble = b & 0x0F;
		int highNibble = (b >> 4) & 0x0F;
		return ""+nibbleToChar(highNibble) + nibbleToChar(lowNibble);
	}
	
	/**
	 * Convert a nibble (0-15) to a hexadecimal digit character.
	 * @param nibble value in the range 0-15
	 * @return the character '0'-'9' and 'A'-'F'
	 */
	public static char nibbleToChar(int nibble) {
		if ((nibble < 0) || (nibble > 0x0F)) throw new IllegalArgumentException("nibble out of range");
		if (nibble <= 9) return (char) ('0' + nibble);
		return (char) ('A' + nibble-10);
	}
	
	/**
	 * Convert the two characters into a byte value.
	 * @param c1 most significant nibble
	 * @param c2 least significant nibble
	 * @return the value represented by the 2 hexadecimal characters
	 */
	public static int getNum(char c1, char c2) {
		int n1=0;
		if ((c1 >= '0') && (c1 <= '9')) {
			n1 = c1 - '0';
		} else if ((c1 >= 'A') && (c1 <= 'F')) {
			n1 = c1 - 'A' + 10;
		} else if ((c1 >= 'a') && (c1 <= 'f')) {
			n1 = c1 - 'a' + 10;
		}
		
		int n2=0;
		if ((c2 >= '0') && (c2 <= '9')) {
			n2 = c2 - '0';
		} else if ((c2 >= 'A') && (c2 <= 'F')) {
			n2 = c2 - 'A' + 10;
		} else if ((c2 >= 'a') && (c2 <= 'f')) {
			n2 = c2 - 'a' + 10;
		}
		
		return n1*0x10 + n2;
	}
	
	/**
	 * Convert the hexadecimal string to an integer.
	 * @param s the hexadecimal string
	 * @return the integer value of the hexadecimal string
	 */
	public static int getNum(String s, int base) {
		return Integer.parseInt(s, base);
	}

	/**
	 * Convert an integer to a hex string with leading zeros based on len bytes.
	 * @param val
	 * @param len
	 * @return
	 */
	public static String intToHex(int val, int len) {
		String s = Integer.toHexString(val);
		len = len*2-s.length();
		for (int i=0; i<len; i++) {
			s = "0"+s;
		}
		return s;
	}
}

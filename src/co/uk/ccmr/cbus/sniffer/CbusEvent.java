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
 * The CbusEvent represents a CBUS event including the priorities, CAN_ID, OPC and data.
 * The class also handles the encoding and decoding of events.
 * 
 */
import java.io.IOException;
import java.io.InputStreamReader;

public class CbusEvent {
	/**
	 * Major Priority enum.
	 *
	 */
	public enum MjPri {
		HIGH(0x00),
		MED(0x01),
		LOW(0x02);
		private final int value;
		private MjPri(int v) {
			value = v;
		}
		public int getValue() {
			return value;
		}
		public static MjPri of(int v) {
			for (MjPri m : MjPri.values()) {
				if (m.getValue() == v) return m;
			}
			return null;
		}
	};
	
	/**
	 * Minor priority enum.
	 *
	 */
	public enum MinPri {
		VHIGH(0x00),
		HIGH(0x01),
		MED(0x02),
		LOW(0x03);
		private final int value;
		private MinPri(int v) {
			value = v;
		}
		public int getValue() {
			return value;
		}
		public static MinPri of(int v) {
			for (MinPri m : MinPri.values()) {
				if (m.getValue() == v) return m;
			}
			return null;
		}
	};
	
	/** The Op code */
	private Opc opc;	// op code
	/** The CAN_ID */
	private int can_id;
	/** Major priority */
	private MjPri mjPri;	// Major priority
	/** Minor priority */
	private MinPri minPri;	// Minor priority
	/** data for any additional parameters */
	private int[] data;
	
	/**
	 * Simple constructor to allow building an event to be built ready to be transmitted.
	 */
	public CbusEvent() {
		data = new int[7];
	}
	
	 /**
	  * Decode the string into a CBUS event.
	  * @param cmd the string to decode
	  */
	public CbusEvent(String cmd) throws InvalidEventException {
		data = new int[7];
		
		if (cmd.length() < 9) throw new InvalidEventException("Event too short - only "+cmd.length()+" characters");
		char c = cmd.charAt(0);
		if (c != ':') throw new InvalidEventException("Missing : - got "+c);
		c = cmd.charAt(1);
		if (c != 'S') throw new InvalidEventException("Missing S - got "+c);
		int b1 = Util.getNum(cmd.charAt(2), cmd.charAt(3));
		setSIDH(b1);
		b1 = Util.getNum(cmd.charAt(4), cmd.charAt(5));
		setSIDL(b1);
		c = cmd.charAt(6);
		if (c != 'N') throw new InvalidEventException("Missing N - got "+c);
		b1 = Util.getNum(cmd.charAt(7), cmd.charAt(8));
		opc = Opc.of(b1);
		if (opc == null) opc = Opc.UNKNOWN;
		int pos = 9;
		try {
			for (int i=0; i< getLen(); i++) {
				b1 = Util.getNum(cmd.charAt(pos), cmd.charAt(pos+1));
				data[i] = b1;
				pos += 2;
			}
		} catch (Exception e) {
			throw new InvalidEventException(e.getMessage());
		}
		System.out.println("new CbusEvent data[0]="+data[0]+" data[1]="+data[1]+" data2="+data[2]+" data3="+data[3]);
	}
	
	/**
	 * Convert this event into a string ready to be transmitted.
	 */
	@Override
	public String toString() {
		String ret = ":S";
		ret += Util.byteToHex(getSIDH());
		ret += Util.byteToHex(getSIDL());
		ret +=  "N";
		if (opc == Opc.UNKNOWN) {
			ret += "??";
		} else {
			ret += Util.byteToHex(opc.getValue());
		}
		for (int i=0; i<getLen(); i++) {
			ret += Util.byteToHex(data[i]);
		}
		return ret + ";";
	}

	/**
	 * Get the CAN header SIDH byte.
	 * @return SIDH byte
	 */
	public int getSIDH() {
		return (mjPri.getValue() << 6) | (minPri.getValue() << 4) | (can_id & 0x78)>> 3;
	}
	/**
	 * Set the priority values and CAN_ID from the SIDH byte.
	 * @param sidh
	 */
	private void setSIDH(int sidh) {
		int v = (sidh >> 6) & 0x03;
		mjPri = MjPri.of(v);
		if (mjPri == null) throw new IllegalArgumentException("Invalid SIDH MjPri="+v);
		v = (sidh >> 4) & 0x03;
		minPri = MinPri.of(v);
		if (minPri == null) throw new IllegalArgumentException("Invalid SIDH MinPri="+v);
		can_id = (sidh<<3) & 0x78;
	}

	/**
	 * Get the CAN header SIDL byte.
	 * @return SIDL byte
	 */
	public int getSIDL() {
		return (can_id & 0x07) << 5;
		
	}
	/**
	 * Set the CAN_ID from the SIDL byte.
	 * @param sidh
	 */
	private void setSIDL(int sidl) {
		can_id |= (sidl >> 5) & 0x07;
	}
	
	/**
	 * Convenience method to get the NN from where it is normally stored. 
	 * No checking is done to ensure the OPC supports an NN.
	 * @return the node number
	 */
	public int getNN() {
		return (data[0]<<8) + data[1];
	}
	/**
	 * Convenience method to set a NN. 
	 * No checking is done to ensure the OPC supports an NN.
	 * @param nn node number
	 */
	public void setNN(int nn) {
		data[0] = (nn >> 8) & 0xFF;
		data[1] = nn & 0xFF;
	}
	
	/**
	 * Convenience method to get the DN/EN from where it is normally stored. 
	 * No checking is done to ensure the OPC supports a DN.
	 * @return the device number
	 */
	public int getDN() {
		return (data[2]<<8) + data[3];
	}
	/**
	 * Convenience method to set a DN/EN. 
	 * No checking is done to ensure the OPC supports an DN.
	 * @param nn node number
	 */
	public void setDN(int dn) {
		data[2] = (dn >> 8) & 0xFF;
		data[3] = dn & 0xFF;
	}
	
	/**
	 * Gets the Major priority for this event.
	 * @return major priority
	 */
	public MjPri getMjPri() {
		return mjPri;
	}
	/**
	 * Sets the Major priority for this event.
	 * @param mp major priority
	 */
	public void setMjPri(MjPri mp) {
		mjPri = mp;
	}
	
	/**
	 * Gets the Minor priority for this event.
	 * @return minor priority
	 */
	public MinPri getMinPri() {
		return minPri;
	}
	/**
	 * Sets the Minor priority for this event.
	 * @param mp minor priority
	 */
	public void setMinPri(MinPri mp) {
		minPri = mp;
	}
	
	/**
	 * Gets the op code for this event.
	 * @return op code
	 */
	public Opc getOpc() {
		return opc;
	}
	/**
	 * Sets the op code for this event.
	 * @param o op code
	 */
	public void setOpc(Opc o) {
		opc = o;
	}
	
	/**
	 * Gets the CAN ID for this event.
	 * @return CAN ID
	 */
	public int getCANID() {
		return can_id;
	}
	/**
	 * Sets the CAN ID for this event.
	 * @param c CAN ID
	 */
	public void setCANID(int c) {
		can_id = c & 0x7F;
	}
	
	/**
	 * Get the number of parameter bytes for the event's op code.
	 * @return
	 */
	public int getLen() {
		return opc.getLen();
	}
	
	/**
	 * Get the data byte at the specified index.
	 * @param idx index
	 * @return data byte
	 */
	public int getData(int idx) {
		if ((idx < 0) || (idx > getLen())) {
			return -1;
		}
		return data[idx];
	}
	/**
	 * Set the data byte at the specified index.
	 * @param idx index
	 * @param v value of data byte
	 */
	public void setData(int idx, int v) {
		if ((idx < 0) || (idx > getLen())) {
			return;
		}
		data[idx] = v;
	}
	
	/**
	 * Convert the event to a human readable String.
	 * @return string representation of the event
	 */
	public String dump(int base) {
		String ret;
		if (base == 16) {
			ret = "MjPri="+mjPri+" MinPri="+minPri + " CAN_ID="+Util.byteToHex(can_id)+" OPC="+opc.toString(base);
		} else {
			ret = "MjPri="+mjPri+" MinPri="+minPri + " CAN_ID="+can_id+" OPC="+opc.toString(base);
		}

		int idx = 0;
		for (ParamNameAndLen p : opc.getParams()) {
			ret += " "+p.getName();
			if (p.getType() == ParamType.NUMBER) {
				int val = 0;
				for (int i=0; i<p.getLen(); i++) {
					val = val*256 + data[idx++];
				}
				if (base == 16) {
					ret += "="+Util.intToHex(val, p.getLen());
				} else {
					ret += "="+val;
				}
			}
			if (p.getType() == ParamType.STRING) {
				ret += "=\"";
				for (int i=0; i<p.getLen(); i++) {
					ret += (char)data[idx++];
				}
				ret += '"';
			}
		}
		return ret;
	}
	
	
	/**
	 * Test method.
	 * Checks that the total length of OPC parameters matches the length bits of the OPC.
	 * Checks the encoding and decoding of events.
	 * @param args
	 */
	public static void main(String [] args) {
		while (true) {
			System.out.println("Checking OPC lengths");
			for (Opc o: Opc.values()) {
				int l = 0;
				for (ParamNameAndLen p : o.getParams()) {
					l += p.getLen();
				}
				if (o.getLen() != l) System.out.println("OPC "+o+" incorrect param len="+l+" instead of "+o.getLen());
			}
			System.out.println("Enter a command string");
			String line="";
			InputStreamReader in = new InputStreamReader(System.in);
	        do {
	        	int c=0;
	        	try {
					c = in.read();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	if (c <=0) break;
	        	char cc = (char)c;
	        	line += cc;
	        	if (cc == '\n') {
	        		CbusEvent cmd = null;
					try {
						cmd = new CbusEvent(line);
						System.out.println("Command = "+cmd.toString());
		    			System.out.println("MjPri = "+cmd.mjPri+" MinPri = "+cmd.minPri + " OPC = "+cmd.opc + " NN = "+cmd.getNN());
					} catch (InvalidEventException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			
	        		line = "";
	        	}
	        } while (true);
		}
	}
}

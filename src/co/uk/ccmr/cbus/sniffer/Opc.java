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
 * Enum representing the OPCs and the parameters each OPC supports.
 *
 */
public enum Opc {
	ACDAT(0xF6, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("data 1", 1),
			new ParamNameAndLen("data 2", 1),
			new ParamNameAndLen("data 3", 1),
			new ParamNameAndLen("data 4", 1),
			new ParamNameAndLen("data 5", 1)}),
	ACK(0x00, new ParamNameAndLen[]{}),
	ACOF(0x91, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2)}),
	ACOF1(0xB1, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data", 1)}),
	ACOF2(0xD1, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1)}),
	ACOF3(0xF1, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1),
			new ParamNameAndLen("data3", 1)}),
	ACON(0x90, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2)}),
	ACON1(0xB0, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data", 1)}),
	ACON2(0xD0, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1)}),
	ACON3(0xF0, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1),
			new ParamNameAndLen("data3", 1)}),					
	ARDAT(0xF7, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("data 1", 1),
			new ParamNameAndLen("data 2", 1),
			new ParamNameAndLen("data 3", 1),
			new ParamNameAndLen("data 4", 1),
			new ParamNameAndLen("data 5", 1)}),			
	AREQ(0x92, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2)}),
	AROF(0x94, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2)}),
	AROF1(0xB4, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data", 1)}),
	AROF2(0xD5, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1)}),
	AROF3(0xF4, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1),
			new ParamNameAndLen("data3", 1)}),
	ARON(0x93, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2)}),
	ARON1(0xB3, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data", 1)}),
	ARON2(0xD4, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1)}),
	ARON3(0xF3, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1),
			new ParamNameAndLen("data3", 1)}),
	ARSOF(0x9E, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2)}),
	ARSOF1(0xBE, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data", 1)}),
	ARSOF2(0xDE, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1)}),
	ARSOF3(0xFE, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1),
			new ParamNameAndLen("data3", 1)}),
	ARSON(0x9D, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2)}),
	ARSON1(0xBD, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data", 1)}),
	ARSON2(0xDD, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1)}),	
	ARSON3(0xFD, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1),
			new ParamNameAndLen("data3", 1)}),	
	ARST(0x07, new ParamNameAndLen[]{}),
	ASOF(0x99, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2)}),
	ASOF1(0xB9, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data", 1)}),
	ASOF2(0xD9, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1)}),
	ASOF3(0xF9, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1),
			new ParamNameAndLen("data3", 1)}),
	ASON(0x98, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2)}),
	ASON1(0xB8, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data", 1)}),	
	ASON2(0xD8, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1)}),
	ASON3(0xF8, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data1", 1),
			new ParamNameAndLen("data2", 1),
			new ParamNameAndLen("data3", 1)}),	
	ASRQ(0x9A, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("DN", 2)}),
	BON(0x03, new ParamNameAndLen[]{}),
	BOOTM(0x5C, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	CANID(0x75, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("CAN_ID", 1)}),
	CMDERR(0x6F, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("Error", 1)}),
	DBG1(0x30, new ParamNameAndLen[] {
			new ParamNameAndLen("Status", 1)}),
	DDES(0xFA, new ParamNameAndLen[] {
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data 1", 1),
			new ParamNameAndLen("data 2", 1),
			new ParamNameAndLen("data 3", 1),
			new ParamNameAndLen("data 4", 1),
			new ParamNameAndLen("data 5", 1)}),
	DDRS(0xFB, new ParamNameAndLen[] {
			new ParamNameAndLen("DN", 2),
			new ParamNameAndLen("data 1", 1),
			new ParamNameAndLen("data 2", 1),
			new ParamNameAndLen("data 3", 1),
			new ParamNameAndLen("data 4", 1),
			new ParamNameAndLen("data 5", 1)}),
	DFLG(0x48, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("SpeedMode", 1)}),
	DFNOF(0x4A, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("Fnum", 1)}),
	DFNON(0x49, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("Fnum", 1)}),
	DFUN(0x60, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("Fn1", 1),
			new ParamNameAndLen("Fn2", 1)}),
	DKEEP(0x23, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1)}),
	DSPD(0x47, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("Speed/Dir", 1)}),
	ENRSP(0xF2, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN3", 1),
			new ParamNameAndLen("EN2", 1),
			new ParamNameAndLen("EN1", 1),
			new ParamNameAndLen("EN0", 1),
			new ParamNameAndLen("EN#", 1)}),
	ENUM(0x5D, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	ERR(0x63, new ParamNameAndLen[] {
			new ParamNameAndLen("Dat1", 1),
			new ParamNameAndLen("Dat2", 1),
			new ParamNameAndLen("Dat3", 1)}),
	ESTOP(0x06, new ParamNameAndLen[]{}),
	EVANS(0xD3, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("EV#", 1),
			new ParamNameAndLen("EV val", 1)}),
	EVLRN(0xD2, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN",2),
			new ParamNameAndLen("EV#",1),
			new ParamNameAndLen("EV val",1)}),
	EVLRNI(0xF5, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("EN#", 1),
			new ParamNameAndLen("EV#", 1),
			new ParamNameAndLen("EV val", 1)}),
	EVNLF(0x70, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EVSPC", 1)}),
	EVULN(0x95, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2)}),
	FCLK(0xCF, new ParamNameAndLen[] {
			new ParamNameAndLen("mins", 1),
			new ParamNameAndLen("hrs", 1),
			new ParamNameAndLen("wdmon", 1),
			new ParamNameAndLen("div", 1),
			new ParamNameAndLen("mday", 1),
			new ParamNameAndLen("temp", 1)}),
	GLOC(0x61, new ParamNameAndLen[] {
			new ParamNameAndLen("Addr", 2),
			new ParamNameAndLen("Flags", 1)}),
	HLT(0x02, new ParamNameAndLen[]{}),
	KCON(0x46, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("Consist#", 1)}),
	KLOC(0x21, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1)}),
	NAK(0x01, new ParamNameAndLen[]{}),
	NAME(0xE2, new ParamNameAndLen[] {
			new ParamNameAndLen("NAME", 7, ParamType.STRING)}),
	NENRD(0x72, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN#", 1)}),
	NERD(0x57, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	NEVAL(0xB5, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN#", 1),
			new ParamNameAndLen("EV#", 1),
			new ParamNameAndLen("EVval", 1)}),
	NNACK(0x52, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	NNCLR(0x55, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	NNEVN(0x56, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	NNLRN(0x53, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	NNREL(0x51, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	NNULN(0x54, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	NUMEV(0x74, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("No. of events", 1)}),
	NVANS(0x97, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("NV#", 1),
			new ParamNameAndLen("NV val", 1)}),
	NVRD(0x71, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("NV#", 1)}),
	NVSET(0x96, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("NV#", 1),
			new ParamNameAndLen("NV val", 1)}),
	PARAMS(0xEF, new ParamNameAndLen[] {
			new ParamNameAndLen("PARA 1", 1),
			new ParamNameAndLen("PARA 2", 1),
			new ParamNameAndLen("PARA 3", 1),
			new ParamNameAndLen("PARA 4", 1),
			new ParamNameAndLen("PARA 5", 1),
			new ParamNameAndLen("PARA 6", 1),
			new ParamNameAndLen("PARA 7", 1)}),
	PARAN(0x9B, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("Para#", 1),
			new ParamNameAndLen("Para val", 1)}),
	PCON(0x45, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("Consist#", 1)}),
	PCVS(0x85, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("CV", 2),
			new ParamNameAndLen("Value", 1)}),
	PLOC(0xE1, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("Addr", 2),
			new ParamNameAndLen("Speed/Dir", 1),
			new ParamNameAndLen("Fn1", 1),
			new ParamNameAndLen("Fn2", 1),
			new ParamNameAndLen("Fn3", 1)}),
	PNN(0xB6, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("Manuf Id", 1),
			new ParamNameAndLen("Module Id", 1),
			new ParamNameAndLen("Flags", 1)}),
	QCVS(0x84, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("CV", 2),
			new ParamNameAndLen("Mode", 1)}),
	QLOC(0x22, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1)}),
	QNN(0x0D, new ParamNameAndLen[]{}),
	RDCC3(0x80, new ParamNameAndLen[] {
			new ParamNameAndLen("Rep", 1),
			new ParamNameAndLen("Byte1", 1),
			new ParamNameAndLen("Byte2", 1),
			new ParamNameAndLen("Byte3", 1)}),
	RDCC4(0xA0, new ParamNameAndLen[] {
			new ParamNameAndLen("REP", 1),
			new ParamNameAndLen("Byte0", 1),
			new ParamNameAndLen("Byte1", 1),
			new ParamNameAndLen("Byte2", 1),
			new ParamNameAndLen("Byte3", 1)}),
	RDCC5(0xC0, new ParamNameAndLen[] {
			new ParamNameAndLen("REP", 1),
			new ParamNameAndLen("Byte0", 1),
			new ParamNameAndLen("Byte1", 1),
			new ParamNameAndLen("Byte2", 1),
			new ParamNameAndLen("Byte3", 1),
			new ParamNameAndLen("Byte4", 1)}),
	RDCC6(0xE0, new ParamNameAndLen[] {
			new ParamNameAndLen("REP", 1),
			new ParamNameAndLen("Byte0", 1),
			new ParamNameAndLen("Byte1", 1),
			new ParamNameAndLen("Byte2", 1),
			new ParamNameAndLen("Byte3", 1),
			new ParamNameAndLen("Byte4", 1),
			new ParamNameAndLen("Byte5", 1)}),
	REQEV(0xB2, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN", 2),
			new ParamNameAndLen("EV#", 1)}),
	RESTP(0x0A, new ParamNameAndLen[]{}),
	REVAL(0x9C, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("EN#", 1),
			new ParamNameAndLen("EV#", 1)}),
	RLOC(0x40, new ParamNameAndLen[] {
			new ParamNameAndLen("Dat1", 1),
			new ParamNameAndLen("Dat2", 1)}),
	RQDAT(0x5A, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),	
	RQDDS(0x5B, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	RQEVN(0x58, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	RQMN(0x11, new ParamNameAndLen[]{}),
	RQNN(0x50, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	RQNP(0x10, new ParamNameAndLen[]{}),
	RQNPN(0x73, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("Para#", 1)}),
	RSTAT(0x0C, new ParamNameAndLen[]{}),
	RTOF(0x08, new ParamNameAndLen[]{}),
	RTON(0x09, new ParamNameAndLen[]{}),
	SNN(0x42, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	SSTAT(0x4C, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("Status", 1)}),
	STAT(0xE3, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2),
			new ParamNameAndLen("CSnum", 2),
			new ParamNameAndLen("Flags", 2),
			new ParamNameAndLen("Unused", 1)}),
	STMOD(0x44, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("mode", 1)}),
	TOF(0x04, new ParamNameAndLen[]{}),
	TON(0x05, new ParamNameAndLen[]{}),
	WCVB(0x83, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("CV", 2),
			new ParamNameAndLen("Value", 1)}),
	WCVO(0x82, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("CV", 2),
			new ParamNameAndLen("Value", 1)}),
	WCVOA(0xC2, new ParamNameAndLen[] {
			new ParamNameAndLen("Addr", 2),
			new ParamNameAndLen("CV", 2),
			new ParamNameAndLen("Mode", 1),
			new ParamNameAndLen("Value", 1)}),
	WCVS(0xA2, new ParamNameAndLen[] {
			new ParamNameAndLen("Session", 1),
			new ParamNameAndLen("CV", 2),
			new ParamNameAndLen("Mode", 1),
			new ParamNameAndLen("Value", 1)}),
	WRACK(0x59, new ParamNameAndLen[] {
			new ParamNameAndLen("NN", 2)}),
	UNKNOWN(-1, new ParamNameAndLen[]{});
	
	private final int value;
	private ParamNameAndLen [] params;
	
	private Opc(int v, ParamNameAndLen [] p) {
		value = v;
		params = p;
	}
	/**
	 * Get the byte value of this OPC.
	 * @return opc value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * Get the length of the OPC using the upper 3 bits.
	 * @return length as determined from OPC.value().
	 */
	public int getLen() {
		return (value >> 5) & 7;
	}
	/**
	 * Get a Opc given its value.
	 * @param v the Opc value
	 * @return
	 */
	public static Opc of(int v) {
		for (Opc o : Opc.values()) {
			if (o.getValue() == v) return o;
		}
		return null;
	}
	/**
	 * Get the set of parameters for this Opc.
	 * @return array of parameters
	 */
	public ParamNameAndLen[] getParams() {
		return params;
	}
	
	/**
	 * toString to include the hex value.
	 */
	public String toString(int base) {
		String v;
		if (base == 16) {
			v = Integer.toHexString(value);
			if (v.length() == 1) {
				v = "0"+v;
			}
			v = v.toUpperCase();
		} else {
			v = ""+value;
		}
		return super.toString()+" ("+v+")";
	}
	
	/**
	 * Just the normal enum name.
	 * @return
	 */
	public String toShortString() {
		return super.toString();
	}
};

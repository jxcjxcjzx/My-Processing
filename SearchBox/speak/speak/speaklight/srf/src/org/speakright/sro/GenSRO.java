package org.speakright.sro;

public class GenSRO {

	/** Generates the sro *.java file from sro\sronumber_definition.xml.
	 *  You MUST run this class manually whenever you change the model.
	 *  
	 *  This main simply calls SROGen, but I find GenSRO.java easier to find
	 *  in Eclipse's Package Explorer
	 * @param args
	 */
	public static void main(String[] args) {
		SROGen.main(args);
	}
}

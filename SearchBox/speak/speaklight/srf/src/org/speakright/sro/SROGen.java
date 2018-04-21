/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.sro;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.speakright.tools.srogen.*;

/**
 * Generates the generated code for SROs in SpeakRight.
 * 
 * To aid code consistency we use code-generation to create a base class for
 * each SRO.  The base class handles its prompts, where each prompt has a field
 * (m_outOfRangePrompt), a set method (set_outOfRangePrompt), and a prompt id ("id:outOfRange").
 * A prompt xml file ({sroname}_prompts.xml) file is created with the prompt text for each
 * id.  You must copy these xml files into your project (see copy_sro_files.cmd in SampleServlet).
 * 
 * Once the base class is generated (in a 'gen' sub-package), the real SRO class (such as SRONumber)
 * is created by hand in the main sro package.  The real class handles everything that the generated
 * code doesn't, such as validation.
 * 
 * @author IanRaeLaptop
 *
 */
public class SROGen {

	/** Generates the sro *.java file from sro\sronumber_definition.xml.
	 *  You MUST run this class manually whenever you change the model.
	 * @param args
	 */
	public static void main(String[] args) {
		log("SRGen version 0.1");
		
		/*************************************************/
		/* LIST OF SROS TO GENERATE!! ADD NEW ONES HERE. */
		String[] sros = { "SRONumber", "SROConfirmYesNo", "SROChoice", "SRODigitString",
				"SROListNavigator", "SROYesNo", "SROTransferCall" };
		
		String dir = "C:\\Source\\speakright\\srf\\src\\org\\speakright\\sro\\";
		
		for(String sroName : sros) {
			genJavaFile(dir, sroName);
			genXMLFile(dir, sroName);
		}
		log("Done.");
	}
	
	static void genJavaFile(String dir, String sroName)
	{
		SROGenerator gen = new SROGenerator(); //(args);
		String xmlPath = dir + sroName + ".xml";
		String code = gen.generateBaseClass(sroName, "org.speakright.sro", xmlPath);
		
		String path = dir;
		path = path + "gen\\";
		path += "gen" + sroName + ".java";
		log("writing file: " + path);
		write(path, code);
		log("Done.");
	}
	
	static void genXMLFile(String dir, String sroName)
	{
		SROGenerator gen = new SROGenerator(); //(args);
		String xmlPath = dir + sroName + ".xml";
		String code = gen.generatePromptsXML(sroName, xmlPath);
		
		String path = dir;
		path = path + "gen\\";
		path += sroName + "_prompts.xml";
		log("writing file: " + path);
		write(path, code);
		log("Done.");
	}
	
	static void log(String msg)
	{
		System.out.println(msg);
	}
	
	static boolean write(String path, String content)
	{
		boolean b = false;
		try
		{
			BufferedWriter w = new BufferedWriter(new FileWriter(path));
			w.write(content);
			w.close();
			b = true;
		}
		catch(IOException e)
		{
			log("Exception: " + e.getLocalizedMessage());
		}
		return b;
	}
}

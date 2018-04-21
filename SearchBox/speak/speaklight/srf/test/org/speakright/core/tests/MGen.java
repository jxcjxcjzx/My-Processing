/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.speakright.tools.mgen.*;

public class MGen {

	/** Generates the model.java file from testfiles\sample_model.xml.
	 *  You MUST run this class manually whenever you change the model.
	 * @param args
	 */
	public static void main(String[] args) {
		log("MGen version 0.1");

		MGenGenerator gen = new MGenGenerator(); //(args);
		String xmlPath = BaseTest.dir + "sample_model.xml";
		String code = gen.run("org.speakright.core.tests", xmlPath);
		
		String path = BaseTest.dir;
		path = path.replace("\\testfiles\\", "\\");
		path += "Model.java";
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

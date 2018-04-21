/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.itest;
import java.io.*;

/**
 * Writes a voicexml page into a file.  This may be useful
 * for inspecting the voicexml later, or sending it to a
 * VoiceXML platform to see how it executes.
 * 
 * @author IanRaeLaptop
 */
public class PageWriter {

	public PageWriter()
	{}
	
	public boolean write(String path, String content)
	{
		boolean b = false;
		try
		{
			System.out.println("output: " + path);
			BufferedWriter w = new BufferedWriter(new FileWriter(path));
			w.write(content);
			w.close();
			b = true;
		}
		catch(IOException e)
		{
			System.out.println("Exception: " + e.getLocalizedMessage());
		}
		return b;
	}
}

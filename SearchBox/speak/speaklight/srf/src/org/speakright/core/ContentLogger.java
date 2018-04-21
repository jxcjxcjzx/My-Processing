package org.speakright.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Dumps each VXML page to a file.  Used for troubleshooting only. 
 * @author IanRaeLaptop
 *
 */
public class ContentLogger {
	String m_dir;
	SRLogger m_logger; //used for logging errors, not for logging the content itself
	int m_uci; //unique call identifier
	
	static int the_next_num; //used to create unique filenames
	
	public ContentLogger(int uci, String dir, SRLogger logger)
	{
		m_uci = uci;
		m_dir = dir;
		m_logger = logger;
	}
	
	void log(String msg)
	{
		m_logger.log(msg);
	}
	
	public void dump(String content)
	{
		boolean b = false;
		try
		{
			String ext = (content.indexOf("<vxml>") >= 0) ? "vxml" : "html"; //!!need better way to do this
			String path = m_dir + String.format("content_%d__%d.%s", m_uci, getNextFileNum(), ext);
			log("dump to: " + path);
			BufferedWriter w = new BufferedWriter(new FileWriter(path));
			w.write(content);
			w.close();
			b = true;
		}
		catch(IOException e)
		{
			log("Exception (dumpContent): " + e.getLocalizedMessage());
		}
	}
	
	synchronized int getNextFileNum()
	{
		the_next_num++;
		return the_next_num;
	}

}

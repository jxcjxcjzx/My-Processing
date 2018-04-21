package org.speakright.itest;
import java.io.*;

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

package org.speakright.sro;
import java.util.*;

import org.speakright.sro.gen.BaseSROSayDate;
import org.speakright.sro.gen.BaseSROSayDate.Prompts;

public class SROSayDate extends BaseSROSayDate {

	public SROSayDate()
	{
		super(new Date());
	}
	public SROSayDate(Date dt)
	{
		super(dt);
		
	}

	public static void main(String[] args)
	{
		xlog("xxxxxxxx");
		SROSayDate sro = new SROSayDate();
		
		xlog(sro.getPrompt(Prompts.mainPROMPT));
	}
	static void xlog(String msg)
	{
		System.out.println(msg);
	}
	
}

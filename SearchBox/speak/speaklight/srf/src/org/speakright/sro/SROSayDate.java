/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
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

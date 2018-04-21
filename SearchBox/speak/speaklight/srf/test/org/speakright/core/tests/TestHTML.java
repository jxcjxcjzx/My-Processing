/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.junit.Assert;
import org.junit.Test;
import org.speakright.core.*;
import org.speakright.core.render.*;
import org.speakright.core.render.html.*;

import static org.junit.Assert.assertEquals;


public class TestHTML implements ISRExtensionFactory {

	SRLogger m_logger = SRLogger.createLogger();
	public void log(String s)
	{
		m_logger.log(s);
	}
	@Test public void test1()
	{
		App1 flow = new App1();

		log("RUNNING for HTML" + flow.name() + ".........");
		SRFactory factory = new SRFactory();
		SRRunner run = factory.createRunner(BaseTest.dir, "http://abc.com", "", null);
		run.setExtensionFactory(this);
		boolean b = run.start(flow);		
		Assert.assertTrue(b);
		
		log("html: " + run.getContent());
	}
	
	public ISpeechPageWriter createPageWriter()
	{
		return new HTMLSpeechPageWriter();
	}
	
}

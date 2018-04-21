/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import java.io.*;
import org.speakright.core.*;
import org.speakright.core.flows.BasicFlow;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.tests.TestRender.MyItem;


public class TestContentDumping extends BaseTest {

	@Test public void simpleNesting()
	{
		BasicFlow flow = new BasicFlow();
		flow.setName("aaa");
		//add sub-flows that are single-shots
		flow.add(new PromptFlow("a"));
		flow.add(new PromptFlow("b"));
		flow.add(new PromptFlow("c"));

		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance.forceUCIReset();
		SRInstance run = CreateInstance(wrap1, "", "");
	    String dir = SRLocations.fixupDir(System.getProperty("user.dir"));
	    dir += "/tmpfiles/";
	    run.setContentLogging(dir);
	    
	    File file1 = new File(dir + "content_1__1.html");
	    file1.delete();
	    File file2 = new File(dir + "content_1__2.html");
	    file2.delete();
	    File file3 = new File(dir + "content_1__3.html");
	    file3.delete();
	    Assert.assertTrue(! file1.exists());
	    Assert.assertTrue(! file2.exists());
	    Assert.assertTrue(! file3.exists());
		
	    boolean b = m_run.start(flow);		
	    Assert.assertTrue(b);
	    
		Proceed(run); //a
		Proceed(run); //b
		Proceed(run);
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		
		ChkTrail(run, "PromptFlow;PromptFlow;PromptFlow");
	    Assert.assertTrue(file1.exists());
	    Assert.assertTrue(file2.exists());
	    Assert.assertTrue(file3.exists());
	}

}

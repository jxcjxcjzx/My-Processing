/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.junit.Test;
import org.speakright.core.SRInstance;
import org.speakright.core.SRResults;
import org.speakright.core.TrailWrapper;


public class TestApp1 extends BaseTest {

	@Test public void Run()
	{
		log("----------testApp1zz----------------");
		App1 flow = new App1();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "id33");
		Proceed(run, "222");
		Proceed(run);
		Proceed(run, "choice1");
		Proceed(run);
		Proceed(run, "choice2");
		Proceed(run);
		Proceed(run, "choice1");
		Proceed(run, SRResults.ResultCode.DISCONNECT);
//		assertEquals("fail", false, run.isFailed());
//		assertEquals("fin", true, run.isFinished());
//		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "id;pwd;b;ask;choice1;ask;choice2;ask;choice1");
		ChkTrail(wrap1, "beg;F;N;N;DISC;end");
		log("----------end testApp1zz----------------");
	}

	@Test public void RunAndRestart()
	{
		App1 flow = new App1();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "id33");
		Proceed(run, "222");
		Proceed(run);
		Proceed(run, "choice1");
		Proceed(run);
		Proceed(run, "choice2");
		Proceed(run);
		Proceed(run, "choice3"); //throws restart!
		
		Proceed(run, "id44");
		Proceed(run, "222");
		Proceed(run);
		Proceed(run, "choice1");
		Proceed(run);
		
		Proceed(run, SRResults.ResultCode.DISCONNECT);
//		assertEquals("fail", false, run.isFailed());
//		assertEquals("fin", true, run.isFinished());
//		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "id;pwd;b;ask;choice1;ask;choice2;ask;id;pwd;b;ask;choice1;ask");
		ChkTrail(wrap1, "beg;F;N;N;CATCH;N;N;DISC;end");
	}
}

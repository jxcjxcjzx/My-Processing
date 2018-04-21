/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.speakright.core.*;
import org.speakright.core.flows.BasicFlow;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.TransferFlow;


public class TestTransfer extends BaseTest {

	@Test public void simpleNesting()
	{
		BasicFlow flow = new BasicFlow();
		flow.setName("aaa");
		//add sub-flows that are single-shots
		flow.add(new PromptFlow("a"));
		flow.add(new TransferFlow(TransferFlow.TransferType.Blind, "222", "see ya"));

		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
		ChkTrail(run, "PromptFlow;transfer");
	}
	
	@Test public void xferFailedBusy()
	{
		log("---------XFERFAILEDBUSY..");
		BasicFlow flow = new BasicFlow();
		flow.setName("aaa");
		//add sub-flows that are single-shots
		flow.add(new PromptFlow("a"));
		flow.add(new TransferFlow(TransferFlow.TransferType.Bridge, "222", "see ya"));
		flow.add(new PromptFlow("b"));

		TrailWrapper wrap1 = new TrailWrapper(flow);

		SRInstance run = StartIt(wrap1);
		Proceed(run, ""); //a
		SRResults res = new SRResults("", SRResults.ResultCode.TRANSFER_FAILED);
		res.m_transferResult = "busy";
		Proceed(run, res);
		Proceed(run, ""); //error prompt
		Proceed(run, ""); //b
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());

		ChkTrail(run, "PromptFlow;transfer;transfer_Failed;PromptFlow");
	}
	
	@Test public void xferSucceed()
	{
		log("---------xferSucceed..");
		BasicFlow flow = new BasicFlow();
		flow.setName("aaa");
		//add sub-flows that are single-shots
		flow.add(new PromptFlow("a"));
		flow.add(new TransferFlow(TransferFlow.TransferType.Bridge, "222", "see ya"));
		flow.add(new PromptFlow("b"));

		TrailWrapper wrap1 = new TrailWrapper(flow);

		SRInstance run = StartIt(wrap1);
		Proceed(run, ""); //a
		SRResults res = new SRResults("", SRResults.ResultCode.SUCCESS);
		res.m_transferResult = "ok";
		Proceed(run, res);
		Proceed(run, ""); //b
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());

		ChkTrail(run, "PromptFlow;transfer;PromptFlow");
	}

	//other tests in TestRender.java
}

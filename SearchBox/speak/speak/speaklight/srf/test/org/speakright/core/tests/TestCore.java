/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Test;
import org.speakright.core.SRInstance;
import org.speakright.core.SRResults;
import org.speakright.core.TrailWrapper;
import org.speakright.core.flows.BasicFlow;


public class TestCore extends BaseTest {

	@Test public void BasicFlow()
	{
		BasicFlow flow = new BasicFlow();
		assertEquals("name", flow.name(), "BasicFlow");
		flow.setName("abc");
		assertEquals("name", flow.name(), "abc");

		Assert.assertSame(flow, flow.getFirst(null));
		assertNull(flow.getNext(null, null));
		assertNull(flow.onCatch(null, null, null, null));
		assertNull(flow.onDisconnect(null, null));
		assertNull(flow.onNoInput(null, null));
		
		//!!later assert Execute generates no content
		
	}
	@Test public void Start()
	{
		MyFlow flow = new MyFlow("a");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = new SRInstance();
		run.locations().setProjectDir(dir);
		assertEquals("", false, run.isFailed());
		assertEquals("", false, run.isFinished());
		assertEquals("", false, run.isStarted());
		
		boolean b = run.start(wrap1);
		Assert.assertTrue(b);
		
		assertEquals("", false, run.isFailed());
		assertEquals("", false, run.isFinished());
		assertEquals("", true, run.isStarted());
		
		ChkTrail(wrap1, "beg;F;E");
	}
	@Test public void Run1()
	{
		MyFlow flow = new MyFlow("a");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = new SRInstance();		
		run.locations().setProjectDir(dir);
		boolean b = run.start(wrap1);
		Assert.assertTrue(b);
		
		run.proceed(new SRResults());
		
		assertEquals("", false, run.isFailed());
		assertEquals("", true, run.isFinished());
		assertEquals("", true, run.isStarted());
		
		ChkTrail(run, "a");
		ChkTrail(wrap1, "beg;F;E;N;T;end");
	}
	@Test public void Run2()
	{
		MyFlow flow = new MyFlow("a");
		MyFlow flow2 = new MyFlow("b");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TrailWrapper wrap2 = new TrailWrapper(flow2);
		Flow2Wrapper outer = new Flow2Wrapper("outer", wrap1, wrap2);
		TrailWrapper wrap3 = new TrailWrapper(outer);
		
		SRInstance run = RunIt(wrap3);		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "a;b");
		ChkTrail(wrap1, "beg;F;E;N;T;end");
		ChkTrail(wrap2, "beg;F;E;N;T;end");
		ChkTrail(wrap3, "beg;F;N;N;T;end");
	}
	@Test public void ExitFlow()
	{
		MyFlow flow = new MyFlow("a");
		MyFlow flow2 = new MyFlow("b");
		flow2.m_exitInGetNext = true;
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TrailWrapper wrap2 = new TrailWrapper(flow2);
		
		Flow2Wrapper flowBoth = new Flow2Wrapper("wrap", wrap1, wrap2);
		TrailWrapper wrap3 = new TrailWrapper(flowBoth);
	
		SRInstance run = RunIt(wrap3);		
		ChkTrail(run, "a;b");
		ChkTrail(wrap1, "beg;F;E;N;T;end");
		ChkTrail(wrap2, "beg;F;E;N;end");
		ChkTrail(wrap3, "beg;F;N;end");
	}
	@Test public void Disconnect()
	{
		MyFlow flow = new MyFlow("a");
		MyFlow flow2 = new MyFlow("b");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TrailWrapper wrap2 = new TrailWrapper(flow2);
		
		Flow2Wrapper flowBoth = new Flow2Wrapper("wrap", wrap1, wrap2);
		TrailWrapper wrap3 = new TrailWrapper(flowBoth);
	
		SRInstance run = RunSpecial(wrap3, wrap2, SRResults.ResultCode.DISCONNECT);
		ChkTrail(run, "a;b");
		ChkTrail(wrap1, "beg;F;E;N;T;end");
		ChkTrail(wrap2, "beg;F;E;end");
		ChkTrail(wrap3, "beg;F;N;DISC;end");
	}
	@Test public void NoInput()
	{
		MyFlow flow = new MyFlow("a");
		flow.m_handleNoInput = true; 
		MyFlow flow2 = new MyFlow("b");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TrailWrapper wrap2 = new TrailWrapper(flow2);
		
		Flow2Wrapper flowBoth = new Flow2Wrapper("wrap", wrap1, wrap2);
		TrailWrapper wrap3 = new TrailWrapper(flowBoth);
	
		SRInstance run = RunSpecial(wrap3, wrap1, SRResults.ResultCode.NOINPUT);
		ChkTrail(run, "a");
		ChkTrail(wrap1, "beg;F;E;NOINPUT;end");
		ChkTrail(wrap2, "");
		ChkTrail(wrap3, "beg;F;end");
	}
	@Test public void Throw()
	{
		MyFlow flow = new MyFlow("a");
		flow.m_throwInGetNext = true; 
		MyFlow flow2 = new MyFlow("b");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TrailWrapper wrap2 = new TrailWrapper(flow2);
		
		Flow2Wrapper flowBoth = new Flow2Wrapper("wrapthrow", wrap1, wrap2);
		TrailWrapper wrap3 = new TrailWrapper(flowBoth);
	
		SRInstance run = RunIt(wrap3);
		ChkTrail(run, "a");
		ChkTrail(wrap1, "beg;F;E;N;end");
		ChkTrail(wrap2, "");
		ChkTrail(wrap3, "beg;F;CATCH;end");
	}
	@Test public void ThrowInExecute()
	{
		MyFlow flow = new MyFlow("a");
		flow.m_throwInExecute = true; 
		MyFlow flow2 = new MyFlow("b");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TrailWrapper wrap2 = new TrailWrapper(flow2);
		
		Flow2Wrapper flowBoth = new Flow2Wrapper("wrapthrow", wrap1, wrap2);
		TrailWrapper wrap3 = new TrailWrapper(flowBoth);
	
		SRInstance run = RunIt(wrap3);
		ChkTrail(run, "a");
		ChkTrail(wrap1, "beg;F;E;end");
		ChkTrail(wrap2, "");
		ChkTrail(wrap3, "beg;F;CATCH;end");
	}
	
	@Test public void ValidationFail()
	{
		ValFlow flow = new ValFlow("a");
		MyFlow flow2 = new MyFlow("b");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TrailWrapper wrap2 = new TrailWrapper(flow2);
		
		Flow2Wrapper flowBoth = new Flow2Wrapper("valwrap", wrap1, wrap2);
		TrailWrapper wrap3 = new TrailWrapper(flowBoth);
	
		SRInstance run = StartIt(wrap3);
		Proceed(run, "111");
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		ChkTrail(run, "a");
		ChkTrail(wrap1, "beg;F;E;end");
		ChkTrail(wrap2, "");
		ChkTrail(wrap3, "beg;F;VALFAIL;end");
	}
	@Test public void ValidationPass()
	{
		ValFlow flow = new ValFlow("a");
		MyFlow flow2 = new MyFlow("b");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TrailWrapper wrap2 = new TrailWrapper(flow2);
		
		Flow2Wrapper flowBoth = new Flow2Wrapper("valwrap", wrap1, wrap2);
		TrailWrapper wrap3 = new TrailWrapper(flowBoth);
	
		SRInstance run = StartIt(wrap3);
		Proceed(run, "222"); //a
		Proceed(run);  //b
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		ChkTrail(run, "a;b");
		ChkTrail(wrap1, "beg;F;E;N;T;end");
		ChkTrail(wrap2, "beg;F;E;N;T;end");
		ChkTrail(wrap3, "beg;F;N;N;T;end");
	}
	@Test public void AsyncTransaction()
	{
		AsyncFlow flow = new AsyncFlow("a");
		MyFlow flow2 = new MyFlow("b");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TrailWrapper wrap2 = new TrailWrapper(flow2);
		
		Flow2Wrapper flowBoth = new Flow2Wrapper("asyncwrap", wrap1, wrap2);
		TrailWrapper wrap3 = new TrailWrapper(flowBoth);
	
		SRInstance run = StartIt(wrap3);
		assertEquals("pause", false, run.isPaused());
		Proceed(run, "222");
		assertEquals("pause", true, run.isPaused());
		run.resume(); //finish a
		assertEquals("pause", false, run.isPaused());
		Proceed(run); //b
		assertEquals("pause", false, run.isPaused());
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		ChkTrail(run, "a;b");
		ChkTrail(wrap1, "beg;F;E;N;T;end");
		ChkTrail(wrap2, "beg;F;E;N;T;end");
		ChkTrail(wrap3, "beg;F;N;N;T;end");
		System.out.println("---done async----");
	}
}

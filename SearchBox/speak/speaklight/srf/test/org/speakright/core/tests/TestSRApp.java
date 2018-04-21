/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.junit.Test;
import org.speakright.core.AppEvent;
import org.speakright.core.IFlow;
import org.speakright.core.SRInstance;
import org.speakright.core.SRResults;
import org.speakright.core.ThrowEvent;
import org.speakright.core.TrailWrapper;
import org.speakright.core.flows.BasicFlow;
import org.speakright.core.flows.FlowList;
import org.speakright.core.flows.LoopFlow;
import org.speakright.core.flows.BranchFlow;
import org.speakright.core.flows.SRApp;
import org.speakright.core.flows.BranchFlow.GotoBranchEvent;


public class TestSRApp extends BaseTest {

	@Test public void run()
	{
		TestApp1 flow = new TestApp1();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
//		assertEquals("fail", false, run.isFailed());
//		assertEquals("fin", true, run.isFinished());
//		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "a;e;f;e;f;b");
		ChkTrail(wrap1, "beg;F;N;N;N;T;end");
	}

	@Test public void appThatOverridesGetWelcome()
	{
		TestApp2 flow = new TestApp2();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
//		assertEquals("fail", false, run.isFailed());
//		assertEquals("fin", true, run.isFinished());
//		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "a;e;f;e;f;b");
		ChkTrail(wrap1, "beg;F;N;N;N;T;end");
	}
	
	@Test public void appWithGotoEvent()
	{
		TestApp3 flow = new TestApp3();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
//		assertEquals("fail", false, run.isFailed());
//		assertEquals("fin", true, run.isFinished());
//		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "a;e;f;MM");
		ChkTrail(wrap1, "beg;F;N;N;CATCH;N;T;end"); //extra N for e2 (which we don't execute)
	}
	/**
	 * plain app that builds its own flow list
	 * @author IanRaeLaptop
	 *
	 */
	@SuppressWarnings("serial")
	public class TestApp1 extends SRApp {
		MyFlow m_flow1 = new MyFlow("a"); //why are these member vars?!!
		LoopFlow m_loop = new LoopFlow(2);
		
		MyFlow m_flow2 = new MyFlow("b");
		public Model M;
		
		public TestApp1()
		{
			m_loop.add(new MyFlow("e"));
			m_loop.add(new MyFlow("f"));
			add(m_flow1);
			add(m_loop);
			add(m_flow2);
		}
	}
	
	/**
	 * app that overrides getWelcome,getMainLoop,and getGoodbye
	 * @author IanRaeLaptop
	 *
	 */
	@SuppressWarnings("serial")
	public class TestApp2 extends SRApp {
		public Model M;
		
		@Override
		public IFlow createWelcome()
		{
			return new MyFlow("a");
		}
		@Override
		public void initMainLoop(LoopFlow loop)
		{
			loop.setName("MainLoop");
			loop.add(new MyFlow("e"));
			loop.add(new MyFlow("f"));
			loop.setNumIterations(2); //must be after add sub-flows!
		}		
		@Override
		public IFlow createGoodbye()
		{
			return new MyFlow("b");
		}
	}

	/**
	 * app that uses goto-events
	 * @author IanRaeLaptop
	 *
	 */
	@SuppressWarnings("serial")
	public class TestApp3 extends SRApp {
		public Model M;
		
		@Override
		public IFlow createWelcome()
		{
			return new MyFlow("a");
		}
		@Override
		public void initMainLoop(LoopFlow loop)
		{
			loop.setName("MainLoop");
			//test the shouldExecute feature
			add(new MyFlow("e"));
			
			MyFlow flow = new MyFlow("e2");
			flow.m_shouldExecute = false;
			loop.add(flow);
			
			loop.add(new FlowList(new MyFlow("f"), "MainMenu"));
//			loop.setNumIterations(20); //avoid loop forever. must be after add sub-flows!
		}		
		@Override
		public IFlow createGoodbye()
		{
			return new MyFlow("b");
		}
		@Override
		public IFlow onCatch(IFlow current, SRResults results, String eventName, ThrowEvent event) {
			if (isAppEvent(event, "MainMenu")) {
				return new MyFlow("MM");
			}
			return super.onCatch(current, results, eventName, event);
		}		
	}
	
}

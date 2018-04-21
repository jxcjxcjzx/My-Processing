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
import org.speakright.core.flows.LoopFlow;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.tests.TestRender.MyItem;


public class TestNesting extends BaseTest {

	@Test public void simpleNesting()
	{
		BasicFlow flow = new BasicFlow();
		flow.setName("aaa");
		//add sub-flows that are single-shots
		flow.add(new PromptFlow("a"));
		flow.add(new PromptFlow("b"));
		flow.add(new PromptFlow("c"));

		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
		ChkTrail(run, "PromptFlow;PromptFlow;PromptFlow");
	}

	@Test public void repeatexecutionNesting()
	{
		BasicFlow flow = new BasicFlow();
		flow.setName("aaar");
		//add sub-flows that are single-shots
		flow.add(new RepeatFlow("ar"));
		flow.add(new RepeatFlow("br"));
		flow.add(new PromptFlow("c"));

		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
		ChkTrail(run, "RepeatFlow;RepeatFlow;RepeatFlow;RepeatFlow;PromptFlow");
	}
	
	@Test public void optionalFlows()
	{
		log("--optionalflow--");
		BasicFlow flow = new BasicFlow();
		flow.setName("aaa");
		//add sub-flows that are single-shots
		flow.add(new OptionalFlow("a", false));
		flow.add(new OptionalFlow("b", true));
		flow.add(new OptionalFlow("c", true));

		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
		assertEquals("fin", true, run.isFinished());
		assertEquals("failed", false, run.isFailed());
		ChkTrail(run, "b;c");
	}

	@Test public void optionalFlowsSecond()
	{
		log("--optionalflowsecond--");
		BasicFlow flow = new BasicFlow();
		flow.setName("aaa");
		//add sub-flows that are single-shots
		flow.add(new OptionalFlow("a", true));
		flow.add(new OptionalFlow("b", false));
		flow.add(new OptionalFlow("c", true));

		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
		assertEquals("fin", true, run.isFinished());
		assertEquals("failed", false, run.isFailed());
		ChkTrail(run, "a;c");
	}

//our new implementation of optional sub-flows (IFlow.getSubFlowAfter) doesn't support the LAST sub-flow
//returning null in its getFirst
//	@Test public void optionalFlowsThird()
//	{
//		log("--optionalflowthird--");
//		BasicFlow flow = new BasicFlow();
//		flow.setName("aaa");
//		//add sub-flows that are single-shots
//		flow.add(new OptionalFlow("a", true));
//		flow.add(new OptionalFlow("b", true));
//		flow.add(new OptionalFlow("c", false));
//
//		TrailWrapper wrap1 = new TrailWrapper(flow);
//		
//		SRInstance run = RunIt(wrap1);
//		assertEquals("fin", true, run.isFinished());
//		assertEquals("failed", false, run.isFailed());
//		ChkTrail(run, "a;b");
//	}

	@Test public void optionalFlowsInLoop1()
	{
		log("--xxxoptionalFlowsInLoop--");
		LoopFlow flow = this.createLoopFlow(false, true, true);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
		assertEquals("fin", true, run.isFinished());
		assertEquals("failed", false, run.isFailed());
		ChkTrail(run, "b;c;b;c");
	}
	@Test public void optionalFlowsInLoop2()
	{
		log("--xxxoptionalFlowsInLoop2--");
		LoopFlow flow = this.createLoopFlow(true, false, true);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
		assertEquals("fin", true, run.isFinished());
		assertEquals("failed", false, run.isFailed());
		ChkTrail(run, "a;c;a;c");
	}
	@Test public void optionalFlowsInLoop3()
	{
		log("--xxxoptionalFlowsInLoop3--");
		LoopFlow flow = this.createLoopFlow(true, true, false);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
		assertEquals("fin", true, run.isFinished());
		assertEquals("failed", true, run.isFailed()); //FAILED since last sub-flow can't return null
		ChkTrail(run, "a;b;a;b");
	}

	LoopFlow createLoopFlow(boolean b1, boolean b2, boolean b3)
	{
		LoopFlow flow = new LoopFlow(2);
		flow.setName("again");
		//add sub-flows that are single-shots
		flow.add(new OptionalFlow("a", b1));
		flow.add(new OptionalFlow("b", b2));
		flow.add(new OptionalFlow("c", b3));
		return flow;
	}

	
	
	@SuppressWarnings("serial")
	public class RepeatFlow extends PromptFlow {
		int m_n = 2; //# of times to repeat
		
		public RepeatFlow(String text)
		{			
			super(text);
		}

		@Override
		public IFlow getNext(IFlow current, SRResults results) {
			this.log("gn " + m_n);
			m_n--;
			if (m_n > 0) return this;
			
			return super.getNext(current, results);
		}
		
	}

	@SuppressWarnings("serial")
	public class OptionalFlow extends PromptFlow {
		boolean m_execute;
		
		public OptionalFlow(String text, boolean dontExecute)
		{			
			super(text);
			this.setName(text);
			m_execute = dontExecute;
		}

		@Override
		public IFlow getFirst(IFlowContext context) {
			if (! m_execute) {
				return null;
			}
			return super.getFirst(context);
		}
	}
}

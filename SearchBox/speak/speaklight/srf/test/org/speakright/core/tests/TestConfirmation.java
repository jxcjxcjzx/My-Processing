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
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.QuestionFlow;
import org.speakright.core.flows.SRApp;
import org.speakright.core.tests.TestSerialization.PFlow;


public class TestConfirmation extends BaseTest {

	@Test public void confirmYes()
	{
		TrailWrapper wrap1 = createApp();
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "");
		Proceed(run, "222", 70); //quest
		Proceed(run, "yes");
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "PromptFlow;QuestionFlow;ConfYNFlow");
	}
	
	TrailWrapper createApp()
	{
        SRApp flow = new SRApp();
        flow.add(new PromptFlow("hey"));
        QuestionFlow quest = new QuestionFlow("abc.grxml", "What size?");
        ConfirmationWrapper cw = new ConfirmationWrapper(quest, new ConfYNFlow());
        flow.add(cw);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		return wrap1;		
	}
	
	@Test public void confirmNo()
	{
		TrailWrapper wrap1 = createApp();
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "");
		Proceed(run, "222", 70); //quest
		Proceed(run, "no");
		Proceed(run, "333", 70); //quest
		Proceed(run, "yes"); //quest
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "PromptFlow;QuestionFlow;ConfYNFlow;QuestionFlow;ConfYNFlow");
	}
	
	@Test public void confirmSkip()
	{
		this.log("--confirmSkip---");
		TrailWrapper wrap1 = createApp();
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "");
		Proceed(run, "222", 81); //quest
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "PromptFlow;QuestionFlow");
	}
	
	@Test public void confirmSkipSlot()
	{
		TrailWrapper wrap1 = createApp();
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "");
		Proceed(run, "222", "slot1", 81); //quest
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "PromptFlow;QuestionFlow");
	}
	
	@Test public void confirmModel()
	{
        SRApp flow = new SRApp();
        flow.add(new PromptFlow("hey"));
        AskCityFlow quest = new AskCityFlow();
        ConfirmationWrapper cw = new ConfirmationWrapper(quest, new ConfYNFlow());
        flow.add(cw);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "");
		Proceed(run, "austin", "slot1", 41); //quest
		Proceed(run, "no");
		Proceed(run, "rocklin", "slot1", 41); //quest
		Proceed(run, "no");
		Proceed(run, "boston", "slot1", 81); //quest
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "PromptFlow;AskCityFlow;ConfYNFlow;AskCityFlow;ConfYNFlow;AskCityFlow");
		assertEquals("city", "boston", quest.M.city().get());
	}
	

	@SuppressWarnings("serial")
	public static class ConfYNFlow extends QuestionFlow implements IConfirmationFlow {
		public Model M;
		IConfirmationNotifier m_notifier;
		
		public ConfYNFlow()
		{			
			super("yn.grxml","do you want x?");
		}
		
		public void setNotifier(IConfirmationNotifier notifier)
		{
			m_notifier = notifier;
		}
		
		
		public boolean needToExecute(IFlow current, SRResults results) {
			SRResults.Slot slot = results.getIthSlot(0); //first slot of previous question
			if (slot == null) {
				return results.m_overallConfidence < 80; //!!param later
			}
			return slot.m_confidence < 80; //!!param later
		}

		@Override
		public IFlow getNext(IFlow current, SRResults results) 
		{
			boolean wasRejected = results.m_input.equals("no");
			log("Rejected: " + wasRejected);
			m_notifier.notifyConfirmationFinished(wasRejected,  results);
			return super.getNext(current, results); //better be null!
		}
	}


	@SuppressWarnings("serial")
	public class AskCityFlow extends QuestionFlow  {
		public Model M;

		public AskCityFlow()
		{			
			super("city.grxml","what city?");
			this.addBinding("slot1", "City");
		}
	}
}

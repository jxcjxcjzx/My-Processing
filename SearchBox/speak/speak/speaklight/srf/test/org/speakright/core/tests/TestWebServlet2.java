/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;
import org.speakright.core.*;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.QuestionFlow;
import org.speakright.core.flows.SRApp;
import org.speakright.core.render.InlineGrammar;
import org.speakright.core.tests.TestConfirmation.AskCityFlow;
import org.speakright.core.tests.TestConfirmation.ConfYNFlow;

import java.io.Serializable;

//see also test in TestSerializable

public class TestWebServlet2 extends BaseTest implements Serializable {
	
	
	@Test public void runApp()
	{
        MyApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "");
		Proceed(run, "");
		Proceed(run, "yes", "confirm");
		Proceed(run, "");
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "PFlow;PFlow;QuestionFlow;PFlow");
		assertEquals("city", "yes", flow.M.city().get());
	}

	public static MyApp createApp()
	{
        MyApp flow = new MyApp();

//        Model m = new Model();
//		run.setModelBinder(m, new ModelBinder(m));
       
        flow.add(new PFlow("this is the FIRST page MONDAY 28"));
        flow.add(new PFlow("and now the SECOND page of my speech application."));
//        flow.add(new QFlow("abc.grxml", "what size is it?"));

        QuestionFlow qflow = new QuestionFlow("", "what size? {audio:welcome.wav}");
		InlineGrammar gram = new InlineGrammar("yes no");
		gram.addBinding("confirm", "city");
		qflow.addGrammar(gram);
		flow.add(qflow);
        
        flow.add(new PFlow("back from the big question. City is {$M.city} {audio:audio/Greeting.wav}"));
        return flow;
	}


	@SuppressWarnings("serial")
	public static class MyApp extends SRApp {

		public Model M;
		
		public MyApp()
		{			
			super();
		}
		
	}
	@SuppressWarnings("serial")
	public static class QFlow extends QuestionFlow {

		public QFlow(String gtext, String text)
		{			
			super(gtext, text);
		}
		
	}
	@SuppressWarnings("serial")
	public static class PFlow extends PromptFlow {

		public Model M;
		static public String the_cityval = "";
		@Override
		public void execute(IExecutionContext context) {
			// TODO Auto-generated method stub
			super.execute(context);
			the_cityval = M.city().get();
			this.log("setting cityval");
		}

		public PFlow(String text)
		{			
			super(text);
		}
		
	}
}

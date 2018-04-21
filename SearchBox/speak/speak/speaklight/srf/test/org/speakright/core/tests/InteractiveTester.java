/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import java.util.ArrayList;

import org.speakright.itest.*;
import org.speakright.sro.SROListNavigator;
import org.speakright.sro.tests.TestNumber.SROQuantity;
import org.speakright.core.*;
import org.speakright.sro.*;
import org.speakright.core.flows.DisconnectFlow;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.QuestionFlow;
import org.speakright.core.flows.SRApp;
import org.speakright.core.render.InlineGrammar;
import org.speakright.core.tests.TestConfirmation.ConfYNFlow;
import org.speakright.core.tests.TestWebServlet2.PFlow;
import org.speakright.sro.*;

//this is not a unit test!
public class InteractiveTester {

	public static void main(String[] args) 
	{
		SRConfig.init(BaseTest.dir, "sample.properties"); //do FIRST
		
		SRInteractiveTester tester = new SRInteractiveTester();
	    String currentDir = SRLocations.fixupDir(System.getProperty("user.dir"));
		tester.setOutputDir(currentDir + "tmpfiles");
		
		AppFactory factory = new AppFactory();
		SRRunner run = factory.createRunner();
		
		App2 app = new App2();
//		SRLocations locations = new SRLocations();
//		locations.setProjectDir(BaseTest.dir);
		tester.run(run, app);
		
		System.out.println("Done.");
	}
	
	static public class App2 extends SRApp
	{
		public Model M;
	
		public App2()
		{
			int which = 5; //change this to run different apps!
			if (which == 2)
			{
				init2();
				return;
			}
			else if (which == 3)
			{
				init3();
				return;
			}
			else if (which == 4)
			{
				init4();
				return;
			}
			else if (which == 5)
			{
				initListNav();
				return;
			}
			add(new PromptFlow("g'day!"));
//			add(new QuestionFlow("abc.gxml", "what size? {audio:welcome.wav}"));
//			QuestionFlow flow = new QuestionFlow("builtin:digits", "what size? {audio:welcome.wav}");
//			flow.addBinding("city", "city");
			
			QuestionFlow flow = new QuestionFlow("", "what size? {audio:welcome.wav}");
//			flow.addBinding("confirm", "city");
//			InlineGrammar gram = new InlineGrammar("yes no"); 
//			gram.setSlotName("confirm");
//			flow.addGrammar(gram);
			flow.addGrammar("grammar/gram1.grxml");
			flow.addBinding("MySlot", "city"); //must be after add gram
			
			add(flow);
//			SRAskNumber ask  = new SRAskNumber("how many bottles?", "bottles", 3, 10);
//			ask.setMin(3);
//			ask.setMax(10);
//			ask.outputToModel("City");
//			add(ask);
			add(new PromptFlow("you said {$M.city}"));
//			add(new DisconnectFlow("bye"));
		}

		void init2()
		{
			add(new PromptFlow("g'day!"));
	        SROQuantity qflow = new SROQuantity("bottles", 1, 10);
	        qflow.setConfirmer(new ConfYNFlow());
			add(qflow);
			add(new PFlow("You said {$INPUT}"));
			add(new DisconnectFlow("bye"));
		}
		void init3()
		{
			add(new PromptFlow("let's test sronumber!"));
	        SRONumber qflow = new SRONumber("bottles", 1, 10);
	        qflow.setConfirmer(new SROConfirmYesNo("wine"));
			add(qflow);
			add(new PFlow("You said {$INPUT}"));
			add(new DisconnectFlow("bye"));
		}
		void init4()
		{
	        ArrayList<String> L = new ArrayList<String>();
	        L.add("red");
	        L.add("blue");
	        L.add("green");
	        SROChoice sro = new SROChoice("color", L);
	        sro.setSampleValue1("red");
	        sro.setSampleValue2("blue");
	        
	        sro.setModelVar("city");
	        SROConfirmYesNo conf = new SROConfirmYesNo("bottles");
	        conf.addGrammar("grammar/yes_no.grammar#YES_NO");
	    
        	sro.setConfirmer(conf);
			add(sro);

			addPromptFlow("You said {$INPUT}");
		}
		
		void initListNav()
		{
	        ArrayList<String> L = new ArrayList<String>();
	        L.add("apple");
	        L.add("banana");
//	        L.add("cherry");
	        SROListNavigator qflow = new SROListNavigator("flights", L);
	        qflow.setName("Lister");
	        qflow.setModelVar("city");
			add(qflow);
		}
		
	}
	
}

/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.speakright.core.IFlow;
import org.speakright.core.ModelBinder;
import org.speakright.core.SRInstance;
import org.speakright.core.SRPersistentState;
import org.speakright.core.SRResults;
import org.speakright.core.TrailWrapper;
import org.speakright.core.flows.BasicFlow;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.QuestionFlow;
import org.speakright.core.flows.SRApp;

import java.io.Serializable;


public class TestSerialization extends BaseTest implements Serializable {
	
	String save(IFlow flow)
	{
		String path = String.format("tmpfiles\\%s.out", flow.name());
		try
		{
		ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream(path));
		out.writeObject(flow);
		out.close();
		}
		catch(Exception e)
		{}
		return path;
	}
	IFlow read(String path)
	{
//		return new TrailWrapper(new MyFlow("a"));
		IFlow flow = null;
		try
		{
		ObjectInputStream in = new ObjectInputStream( new FileInputStream(path));
		flow = (IFlow)in.readObject();
		in.close();
		}
		catch(Exception e)
		{
			log("execption!" + e.getLocalizedMessage());
		}
		
		if (flow == null) {
			log("is nnnnulll");
		}
		return flow;
	}

	@Test public void realSimple()
	{
		App1 flow = new App1();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		//SRInstance run = StartIt(wrap1);
		StartIt(wrap1);

		String sv = save(wrap1);
		TrailWrapper ww = (TrailWrapper)read(sv);
		String ss = ww.m_trail.toString();
		assertEquals("serializedtrail", "beg;F", ss);
	}
	
	@Test public void SerializeRun()
	{
		App1 flow = new App1();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		
		SRInstance run = StartIt(wrap1);
		String path = String.format("tmpfiles\\sr_1.out");
		SRPersistentState state = new SRPersistentState(run, path);
		
		try
		{
			FileOutputStream fout = new FileOutputStream(path);
			boolean b = state.passivate(fout, path);
			assertEquals("b",true,b);
			fout.close();
		}
		catch(Exception e)
		{}

		try
		{
			path = state.m_streamId;
			FileInputStream fin = new FileInputStream(path);
			boolean b = state.activate(fin);
			assertEquals("b",true,b);
			fin.close();
			state.m_run.finishActivation();
			state.m_run.restoreModelBinder(new ModelBinder());
		}
		catch(Exception e)
		{}
		
		run = state.m_run; //the recreated run object
		wrap1 = (TrailWrapper)run.ApplicationFlow();
		assertEquals("appflow", "org.speakright.core.TrailWrapper", wrap1.getClass().getCanonicalName());
		
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
	}



	SRPersistentState passivate(SRInstance run, String path)
	{
		//first get the SRInstance to clear its transient fields
		run.prepareToPassivate();		
		
		SRPersistentState state = new SRPersistentState(run, path);
		try
		{
			FileOutputStream fout = new FileOutputStream(path);
			boolean b = state.passivate(fout, path);
			log("passivate..");
			assertEquals("b",true,b);
			fout.close();
		}
		catch(Exception e)
		{}
		return state;		
	}
	SRPersistentState persistentStartIt(IFlow flow, String path)
	{
		SRInstance run = StartIt(flow);
		return passivate(run, path);
	}
	SRInstance persistentProceed(String key, SRResults.ResultCode code)
	{
		return persistentProceed(key, "", "", code);
	}
	SRInstance persistentProceed(String key, String input)
	{
		return persistentProceed(key, input, "", SRResults.ResultCode.SUCCESS);
	}
	SRInstance persistentProceedSlot(String key, String input, String slot)
	{
		return persistentProceed(key, input, slot, SRResults.ResultCode.SUCCESS);
	}
	SRInstance persistentProceed(String key, String input, String slot, SRResults.ResultCode code)
	{
		SRPersistentState state = new SRPersistentState();
		state.m_streamId = key; //not really needed

		String path = key;
		try
		{
			path = state.m_streamId;
			FileInputStream fin = new FileInputStream(path);
			boolean b = state.activate(fin);
			state.m_run.restoreModelBinder(new ModelBinder());
			log("activate..");
			assertEquals("b",true,b);
			fin.close();
		}
		catch(Exception e)
		{}
		
		state.m_run.finishActivation();
		
		Proceed(state.m_run, input, slot, 100, code);
		
		if (! state.m_run.isFinished()) {
			passivate(state.m_run, path);
		}
		return state.m_run;		
	}
	

	@Test public void SerializeRun2()
	{
		log("---------serial2-------");
		App1 flow = new App1();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		
		String path = String.format("tmpfiles\\sr_2.out");
		persistentStartIt(wrap1, path);
		
		persistentProceed(path, "id33");
		persistentProceed(path, "222");
		persistentProceed(path, "");
		persistentProceed(path, "choice1");
		persistentProceed(path, "");
		persistentProceed(path, "choice2");
		persistentProceed(path, "");
		persistentProceed(path, "choice1");
		SRInstance run = persistentProceed(path, SRResults.ResultCode.DISCONNECT);
		wrap1 = (TrailWrapper)run.ApplicationFlow();
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "id;pwd;b;ask;choice1;ask;choice2;ask;choice1");
		ChkTrail(wrap1, "beg;F;N;N;DISC;end");
		log("---------end serial2-------");
	}

	@Test public void SerializePromptsAndGrammars()
	{
		log("---------SerializePromptsAndGrammars-------");
        SRApp flow = new SRApp();
        flow.add(new PFlow("this is the FIRST page"));
        flow.add(new PFlow("and now the SECOND page"));
        flow.add(new QFlow("abc.grxml", "what zxzzsize is it?"));
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		
		String path = String.format("tmpfiles\\sr_3.out");
		persistentStartIt(wrap1, path);
		
		SRInstance run;
		
		persistentProceed(path, "");
//		persistentProceed(path, "");
		persistentProceed(path, "joe");
//		persistentProceed(path, "sue");
		run = persistentProceed(path, SRResults.ResultCode.DISCONNECT);
		wrap1 = (TrailWrapper)run.ApplicationFlow();
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "PFlow;PFlow;QFlow");
	}
	
	@Test public void webservlet2()
	{
		/*
		 * This was a wierd bug. Could not get flow.M.city().get() to be "yes".
		 * I eventually realized that the local variable flow is not the same as
		 * the one inside SRInstance -- a new flow object is created with each
		 * activation (using serialization).  So of course the local object flow's
		 * Model won't get set.
		 * Then I tried using run.ApplicationFlow and even that didn't work. This
		 * is STRANGE because I can see in the log that M.city = 'yes' occurs. Perhaps
		 * there is a one off error, where run.Application flow is the previous flow
		 * object.
		 * Finally I moved the test into TestWebServlet2.PFlow.  It stores M.city in
		 * it's own static var the_cityval, and this is "yes".
		 */
		log("---------webservlet2-------");
		TestWebServlet2.MyApp flow = TestWebServlet2.createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		String path = String.format("tmpfiles\\sr_3.out");
		persistentStartIt(wrap1, path);
		
		SRInstance run = persistentProceed(path, "");
		TrailWrapper flow2w = (TrailWrapper)run.ApplicationFlow();
		TestWebServlet2.MyApp flow2 = (TestWebServlet2.MyApp)flow2w.InnerFlow();
		
		persistentProceed(path, "");
		log("--xxx--");
		persistentProceedSlot(path, "yes", "confirm");
//		assertEquals("city", "yes", flow2.M.city().get());
		assertEquals("city", "yes", TestWebServlet2.PFlow.the_cityval);
		persistentProceed(path, "");
		run = persistentProceed(path, SRResults.ResultCode.DISCONNECT);
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "PFlow;PFlow;QuestionFlow;PFlow");
		flow2w = (TrailWrapper)run.ApplicationFlow();
		flow2 = (TestWebServlet2.MyApp)flow2w.InnerFlow();
		assertEquals("city", "yes", flow2.M.city().get());
		log("---------end webservlet2-------");
	}
	

	@SuppressWarnings("serial")
	public class QFlow extends QuestionFlow {

		public QFlow(String gtext, String text)
		{			
			super(gtext, text);
		}
		
	}
	@SuppressWarnings("serial")
	public class PFlow extends PromptFlow {

		public PFlow(String text)
		{			
			super(text);
		}
		
	}
}

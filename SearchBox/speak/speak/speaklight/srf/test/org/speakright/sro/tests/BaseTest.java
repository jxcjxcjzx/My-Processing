package org.speakright.sro.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.speakright.core.*;
import org.speakright.core.render.ISpeechPageWriter;
import org.speakright.core.render.mock.*;
import org.speakright.core.tests.Model;

/**
 * Base class for all Junit tests in this package.
 * @author Ian Rae
 *
 */
public class BaseTest implements ISRExtensionFactory {
	SRLogger m_logger = SRLogger.createLogger();
	public void log(String s)
	{
		m_logger.log(s);
	}

	public static String dir = "C:\\Source\\speakright\\srf\\test\\org\\speakright\\core\\tests\\testfiles\\";
	
	SRRunner m_runner;
	SRInstance m_run;
	Model M; //the model
	MockSpeechPageWriter m_writer;

	public SRInstance StartIt(IFlow flow)
	{
		return StartIt(flow, "");
	}
	public SRInstance CreateInstance(IFlow flow, String baseUrl)
	{
		log("RUNNING " + flow.name() + ".........");
		SRFactory factory = new SRFactory();
		m_runner = factory.createRunner(dir, "http://abc.com", baseUrl, null);
		m_run = m_runner.getImpl();
//		m_run.locations().setProjectDir(dir);
		String path = dir + "prompts.xml";
		m_run.registerPromptFile(path);
//		m_run.setReturnUrl("http://abc.com");
//		m_run.setGrammarBaseUrl(baseGramUrl);
//		m_run.setPromptBaseUrl(basePromptUrl);
		m_run.setExtensionFactory(this);
		M = new Model();
		m_run.setModelBinder(M, new ModelBinder(M));
		return m_run;
	}
	public SRInstance StartIt(IFlow flow, String baseUrl)
	{
		CreateInstance(flow, baseUrl);
		boolean b = m_run.start(flow);		
		Assert.assertTrue(b);
		return m_run;
	}

	public void Proceed(SRInstance run, String input)
	{
		m_run = run;
		SRResults results = new SRResults(input, SRResults.ResultCode.SUCCESS);
		run.proceed(results);
	}
	public void Proceed(SRInstance run, String input, int overallConfidence)
	{
		m_run = run;
		SRResults results = new SRResults(input, SRResults.ResultCode.SUCCESS);
		results.m_overallConfidence = overallConfidence;
		run.proceed(results);
	}
	public void Proceed(SRInstance run, String input, String slot)
	{
		m_run = run;
		SRResults results = new SRResults(input, SRResults.ResultCode.SUCCESS);
		results.addSlot(slot, input);
		run.proceed(results);
	}
	public void Proceed(SRInstance run, String input, String slot, int confidence)
	{
		m_run = run;
		SRResults results = new SRResults(input, SRResults.ResultCode.SUCCESS);
		results.addSlot(slot, input, confidence);
		run.proceed(results);
	}
	public void Proceed(SRInstance run, String input, String slot, int confidence, SRResults.ResultCode code)
	{
		m_run = run;
		SRResults results = new SRResults(input, code);
		if (slot.length() > 0) {
			results.addSlot(slot, input, confidence);
		}
		run.proceed(results);
	}
	public void Proceed(SRInstance run)
	{
		m_run = run;
		SRResults results = new SRResults("", SRResults.ResultCode.SUCCESS);
		run.proceed(results);
	}
	public void Proceed(SRInstance run, SRResults.ResultCode code)
	{
		m_run = run;
		SRResults results = new SRResults("", code);
		run.proceed(results);
	}
	public void Proceed(SRInstance run, String input, SRResults.ResultCode code)
	{
		m_run = run;
		SRResults results = new SRResults(input, code);
		run.proceed(results);
	}
	
	
	public void ChkTrail(SRInstance run, String trail)
	{
		assertEquals("trail", trail, run.m_trail.toString());
	}
	public void ChkTrail(TrailWrapper wrap, String trail)
	{
		assertEquals(wrap.name(), trail, wrap.m_trail.toString());
	}
	
	public ISpeechPageWriter createPageWriter() {
		m_writer = new MockSpeechPageWriter();
		return m_writer;
	}
}

/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.speakright.core.*;
import org.speakright.core.render.ISpeechPageWriter;
import org.speakright.core.render.mock.MockSpeechPageWriter;

/**
 * Base class for all Junit tests that use SRInstance.
 * But also a helper class for all XMLUnit tests; that's
 * why all the methods and fields of BaseTest are public
 * @author Ian Rae
 *
 */
public class BaseTest implements ISRExtensionFactory {
	@BeforeClass static public void redirectStderr() {
		SRConfig.init(dir, "sample.properties");
//		System.out.println("bbbbbbbbbbbbbbbbbbbb b b b b ");
	}
	
	SRLogger m_logger = SRLogger.createLogger();
	public void log(String s)
	{
		m_logger.log(s);
	}
	
	public ISpeechPageWriter createPageWriter() {
		m_writer = new MockSpeechPageWriter();
		return m_writer;
	}
	
	//C:\Source\Eclipse\SpeakRight\test\org\speakright\core\tests\testfiles
	//                           C:\Source\speakright\srf\test\org\speakright\core\tests\testfiles
	public static String dir = "C:\\Source\\speakright\\srf\\test\\org\\speakright\\core\\tests\\testfiles\\";
	
	SRRunner m_runner;
	SRInstance m_run;
	MockSpeechPageWriter m_writer;
	protected boolean m_useMockPageWriter;

	public SRInstance RunIt(IFlow flow)
	{
		log("RUNNING " + flow.name() + ".........");
		m_runner = createRunner();
//		m_run = m_runner.getImpl();
//		m_run.locations().setProjectDir(dir);
//		m_run.setReturnUrl("http://abc.com");
//		Model m = new Model();
//		m_run.setModelBinder(m, new ModelBinder(m));
//		String path = dir + "prompts.xml";
//		m_run.registerPromptFile(path);
//		if (m_useMockPageWriter) {
//			m_run.setExtensionFactory(this);
//		}
		
		boolean b = m_run.runAll(flow);		
		Assert.assertTrue(b);
		log("RUNNING " + flow.name() + ".........END");
		return m_run;
	}
	
	public SRInstance StartIt(IFlow flow)
	{
		return StartIt(flow, "", "");
	}
	public SRInstance StartIt(IFlow flow, String baseGramUrl)
	{
		return StartIt(flow, baseGramUrl, "");
	}
	SRRunner createRunner()
	{
		AppFactory factory = new AppFactory();
		m_runner = factory.createRunner();
		m_run = m_runner.getImpl();
//		m_run.locations().setProjectDir(dir);
//		String path = dir + "prompts.xml";
//		m_run.registerPromptFile(path);
////		m_run.setReturnUrl("http://abc.com");
//		Model m = new Model();
//		m_run.setModelBinder(m, new ModelBinder(m));
		if (m_useMockPageWriter) {
			m_run.setExtensionFactory(this);
		}
		return m_runner;
	}
	public SRInstance CreateInstance(IFlow flow, String baseGramUrl, String basePromptUrl)
	{
		log("RUNNING " + flow.name() + ".........");

		m_runner = createRunner();
//		m_run = m_runner.getImpl();
////		m_run.locations().setProjectDir(dir);
//		String path = dir + "prompts.xml";
//		m_run.registerPromptFile(path);
//		m_run.setReturnUrl("http://abc.com");
		m_run.setGrammarBaseUrl(baseGramUrl);
		m_run.setPromptBaseUrl(basePromptUrl);
//		Model m = new Model();
//		m_run.setModelBinder(m, new ModelBinder(m));
//		if (m_useMockPageWriter) {
//			m_run.setExtensionFactory(this);
//		}
		return m_run;
	}
	public SRInstance StartIt(IFlow flow, String baseGramUrl, String basePromptUrl)
	{
		CreateInstance(flow, baseGramUrl, basePromptUrl);
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
	public void Proceed(SRInstance run, SRResults results)
	{
		m_run = run;
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
	public SRInstance RunSpecial(IFlow first, IFlow target, SRResults.ResultCode codeToUse)
	{
		m_runner = createRunner();
//		m_run = m_runner.getImpl();
//		m_run.locations().setProjectDir(dir);
//		String path = dir + "prompts.xml";
//		m_run.registerPromptFile(path);
//		m_run.setReturnUrl("http://abc.com");
		m_run.start(first);
		while (! m_run.isFinished())
		{
			IFlow current = m_run.peekCurrent();
			SRResults results = new SRResults("", SRResults.ResultCode.SUCCESS);
			if (current == target)
			{
				results = new SRResults("", codeToUse);
			}
			m_run.proceed(results);
		}
		return m_run;
	}

}

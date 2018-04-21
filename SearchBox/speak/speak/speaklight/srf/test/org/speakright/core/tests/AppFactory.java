package org.speakright.core.tests;

import org.speakright.core.ModelBinder;
import org.speakright.core.SRFactory;
import org.speakright.core.SRRunner;
import org.speakright.servlet.ISRServlet;

/** 
 * Factory for this app.
 * @author IanRaeLaptop
 *
 */
public class AppFactory {

	public SRRunner createRunner() 
	{
		SRFactory factory = new SRFactory();
		SRRunner run = factory.createRunner(BaseTest.dir, "http://abc.com", "", null);
//		m_run.locations().setProjectDir(dir);
		String path = BaseTest.dir + "prompts.xml";
		run.registerPromptFile(path);
//		m_run.setReturnUrl("http://abc.com");
		Model m = new Model();
		run.setModelBinder(m, new ModelBinder(m));
		return run;
	}

}

package org.speakright.core;

import javax.servlet.ServletContext;

import org.speakright.servlet.ISRServlet;

public class SRFactory {

	/**
	 * Create a runner and initialize it.  It inits the URLs and paths.  It calls onCreateRunner which
	 * you can override to do any addional initialization.
	 * 
	 * @param projectDir  full path to project dir
	 * @param returnUrl	absolute URL that voicexml pages postback to. eg http://localhost:8080/SimpsonsDemoServlet/App1
	 * @param baseUrl absolute URL of the web apps main directory. eg http://localhost:8080/SimpsonsDemoServlet  It's used
	 * to set the prompt and grammar urls.
	 * @param servlet callback interface. can be null.
	 * @return runner
	 */
	public SRRunner createRunner(String projectDir, String returnUrl, String baseUrl, ISRServlet servlet)
	{
		SRRunner run = new SRRunner();
        run.log("SpeakRight Framework version: " + SRRunner.SPEAKRIGHT_VERSION);
    
        initUrls(run, returnUrl, baseUrl);
        
        run.log("project dir: " + projectDir);
        run.locations().setProjectDir(projectDir);

        onCreateRunner(run);
        reInitRunner(run, servlet);
        
		return run;
	}
	
	public void initUrls(SRRunner run, String returnUrl, String baseUrl)
	{
        //set urls
        run.log("return url: " + returnUrl);
        run.log("base url: " + baseUrl);
        run.setReturnUrl(returnUrl); 
        run.setGrammarBaseUrl(baseUrl);
        run.setPromptBaseUrl(baseUrl);
	}
	
	/**
	 * A method you can override to do any addional initialization.
	 * 
	 * Things apps may want to do here:
	 * - setting model and binder
	 * - registering prompt file
	 * - set extension factory
	 * 
	 * Note.  The ISRServlet.initLocations is done after onCreateRunner
	 * 
	 * @param run  has been initialized by createRunner 
	 */
	protected void onCreateRunner(SRRunner run)
	{
	}

	/**
	 * Re-initialize a runner.  Between postbacks the SRRunner object is serialized and then
	 * de-serialized.  To reduce its size, some information is not stored and needs to be re-initialized.
	 * This method does that.
	 * @param run  a runner object that was created by createRunner
	 * @param servlet callback interface. can be null.
	 */
	public void reInitRunner(SRRunner run, ISRServlet servlet)
	{
		if (servlet != null) {
			servlet.initLocations(run);
		}
	}
}

package org.speakright.core;

import java.util.ArrayList;

import org.speakright.core.render.ISpeechPageWriter;
import org.speakright.core.render.RenderContext;
import org.speakright.core.render.SpeechPage;
import org.speakright.core.render.SpeechPageRenderer;

/**
 * The main class in SpeakRight.  This class is the runtime
 * for a single instance (that is a single phone call).
 * It takes an application flow object (which has sub-flows)
 * and executes it, but asynchronously.  
 * 
 * Begin by calling <code>start</code>. Then call <code>getContent</code>
 * to get the generated VoiceXML page.  Send it off the the voicexml engine.  
 * 
 * Then passivate this object until the HTTP reply from the voicexml engine
 * arrives.  When it does, activate this object and call <code>proceed</code>,
 * then call <code>getContent</code>
 * to get the generated VoiceXML page.  Send it off the the voicexml engine.  
 * Repeat until <code>isFinished</code> is true.
 * 
 * This class wraps SRInstance to provide a safe public API.
 *  
 * @author IanRaeLaptop
 *
 */
public class SRRunner {
	SRInstance m_run;
	
	public SRRunner()
	{
		m_run = new SRInstance();
	}
	
	/**
	 * For advance use only.
	 * @return
	 */
	public SRInstance getImpl()
	{
		return m_run;
	}
	
	/**
	 * When saving state in a servlet, we're going to just save the
	 * whoe SRInstance object in the HttpSession.  But we need to 
	 * clear the transient fields since they are not needed and
	 * are not serializable.
	 *
	 */
	public void prepareToPassivate()
	{
		m_run.prepareToPassivate();
	}
	
	/**
	 * MUST be called after activating (restoring this object using serialization).
	 * It re-initializes the SRInstance transient fields.
	 */
	public void finishActivation()
	{
		m_run.finishActivation();
	}
	
	/**
	 * Register a prompt file, for the duration of the call.
	 * @param path
	 */
	public void registerPromptFile(String path)
	{
		m_run.registerPromptFile(path);
	}
	
	/**
	 * Set the return url, which is used in the submit VoiceXML tag to
	 * POST the results of a page back.  For a java servlet, the return
	 * url is simply the servlet's URL.
	 * @param url  
	 */
	public void setReturnUrl(String url)
	{
		m_run.setReturnUrl(url);
	}
	
	/**
	 * Set the URL of grammar files.  Any grammar urls in the app that
	 * are relative urls will have this url pre-pended.
	 * @param url  usually a url within the java web application, such
	 *  as "http://somecompany.com/speechapp1/grammar"
	 */
	public void setGrammarBaseUrl(String url)
	{
		m_run.setGrammarBaseUrl(url);
	}
	
	/**
	 * Set the URL of audio files.  Any audio urls in the app that
	 * are relative urls will have this url pre-pended.
	 * @param url  usually a url within the java web application, such
	 *  as "http://somecompany.com/speechapp1/audio"
	 */
	public void setPromptBaseUrl(String url)
	{
		m_run.setPromptBaseUrl(url);
	}
	
	public void log(String message)
	{
		m_run.log(message);
	}
	
	/**
	 * extracts any errors that this SRInstance object logged.
	 * @param parent  error object to copy error info into.
	 * @return true if any errors have occured.
	 */
	public boolean failed(SRError parent)
	{
		return m_run.failed(parent);
	}
	
	/**
	 * Get the outermost flow object, that was passed to <code>start</code>.
	 * @return
	 */
	public IFlow ApplicationFlow()
	{
		return m_run.ApplicationFlow();
	}
	
	/**
	 * Set the extension point factory.  This method
	 * is optional because SRInstance initializes itself
	 * to use a default factory.
	 * Use this method when you have created your own factory.
	 * @param factory
	 */
	public void setFactory(ISRFactory factory)
	{
		m_run.setFactory(factory);
	}

	/**
	 * Get the current language 
	 * @return language, such as "en-us"
	 */
	public String language()
	{
		return m_run.language();
	}
	/**
	 * Set the current language.
	 * @param s language, such as "en-us"
	 */
	public void setLanguage(String lang)
	{
		m_run.setLanguage(lang);
	}
	
//	/**
//	 * Get the locations object.
//	 * @return
//	 */
//	public SRLocations locations()
//	{
//		return m_locations;
//	}
	
	/**
	 * Has an error ocurred yet.
	 * @return true if error has ocurred.
	 */
	public boolean isFailed()
	{
		return m_run.isFailed();
	}

	/**
	 * Start the application.
	 * @param flow the application flow object.
	 * @return whether app was started successfully.
	 */
	public boolean start(IFlow flow)
	{
		return m_run.start(flow);
	}
	/**
	 * Get the content (the VoiceXML page).
	 * MUST be called immediately after <code>start</code> or <code>proceed</code>
	 * @return the generated voicexml page.
	 */
	public String getContent()
	{
		return m_run.getContent();
	}
	
	/**
	 * Generate the final page for the application.
	 * This page terminates the call and the
	 * VoiceXML session.
	 *
	 */
	public void generateFinPage()
	{
		m_run.generateFinPage();
	}
	
	
	/**
	 * Resume (after pausing).  Only used by
	 * async transactions (IFlow.doTransaction).
	 *
	 */
	public void resume()
	{
		m_run.resume();
	}
	
	/**
	 * Is the application paused.  Only used by
	 * async transactions (IFlow.doTransaction)
	 * @return
	 */
	public boolean isPaused()
	{
		return m_run.isPaused();
	}
	
	/**
	 * Continue execution, using the given results that
	 * we returned by the voicexml platform to determine what flow
	 * is executed next.
	 * @param results  results from the voicexml platform.  These are the results of executing the previous page.
	 */
	public void proceed(SRResults results)
	{
		m_run.proceed(results);
	}
	
	/**
	 * Has the application finished.
	 * @return true if fin
	 */
	public boolean isFinished()
	{
		return m_run.isFinished();
	}
	
	/**
	 * Has <code>start</code> been called.
	 * @return true if started.
	 */
	public boolean isStarted()
	{
		return m_run.isStarted();
	}
	
	/**
	 * used for unit tests only.
	 * @param flow
	 * @return
	 */
	public boolean runAll(IFlow flow)
	{
		return m_run.runAll(flow);
	}
}

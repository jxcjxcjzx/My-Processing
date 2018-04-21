/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.speakright.core.IFlow;
import org.speakright.core.ISRExtensionFactory;
import org.speakright.core.ModelBinder;
import org.speakright.core.SRConfig;
import org.speakright.core.SRFactory;
import org.speakright.core.SRLocations;
import org.speakright.core.SRLogger;
import org.speakright.core.SRResults;
import org.speakright.core.SRRunner;
import org.speakright.core.RawCGIParams;
import org.speakright.core.render.ISpeechPageWriter;
import org.speakright.core.render.html.HTMLSpeechPageWriter;
import org.speakright.core.render.voicexml.VoiceXMLSpeechPageWriter;

/**
 * Manages running a SpeakRight application inside a servlet.
 * Is a lightweight wrapper around SRInstance to remove dependency
 * of application code from it.
 *  
 * @author IanRaeLaptop
 *
 */
public class SRServletRunner implements ISRExtensionFactory {

	 boolean m_doingHTML = false;
	 HttpServletRequest m_request;
	 HttpServletResponse m_response;	 
	 SRRunner m_run;
	 SRLogger m_logger;
	 ISRServlet m_self;
	 SRFactory m_factory;
	
	public SRServletRunner(SRFactory factory, ISRServlet self, HttpServletRequest request, HttpServletResponse response, String httpActionName)
	{
	    //do this here so we look for local log4j.properties file (in web-inf\classes) first
	    m_logger = SRLogger.createLogger();
	    
	    m_logger.log("------ SERVLET: doing " + httpActionName + " ---------");
		m_request = request;
		m_response = response;
		m_self = self;
		m_factory = factory;
		
		getMode(); //must do this before do getWriter
	}
	
	/**
	 * Get the print writer that will output the servlet's content (voicexml)
	 * @return
	 * @throws IOException
	 */
	public PrintWriter getWriter() throws IOException
	{
		PrintWriter out = m_response.getWriter();
		return out;
	}
	
	/**
	 * Get the SpeakRight logger (log4j)
	 * @return
	 */
	public SRLogger logger()
	{
		return m_logger;
	}
	
	public void log(String msg)
	{
		m_logger.log(msg);
	}
	
	public SRRunner runner()
	{
		return m_run;
	}
	
	/**
	 * Is this a new session?  A session is considered new if session.isNew is true or
	 * if it can't find the saved SRRunner object saved in the session.
	 * @return
	 */
	public boolean isNewSession()
	{
        HttpSession session = m_request.getSession();
        boolean isNew = session.isNew();
        String id = session.getId();
        m_logger.log("SERVLET: " + ((isNew) ? "*** NEW SESSION ***" : "EXISTING SESSION") + " ID: " + id);
        
        if (!isNew) {
	        SRRunner run = activateSavedSRRunner(m_request);
	        if (run == null)
	        {
	    		m_logger.log("SERVLET: oops, no runner. what happened to our session?!!!!!!!!!!!!!");
				return true;
	        }
        }
        return isNew;
	}
	
	/**
	 * Start the SpeakRight app.  This MUST be called only once for each session (phone call),
	 * when isNewSession returns true.
	 * startApp will generate a single VoiceXML page and write it to the print writer.
	 * @param flow the application flow object
	 * @throws IOException
	 */
	public void startApp(IFlow flow) throws IOException
	{
		m_logger.log("SERVLET: startApp");
		m_run.start(flow);

		m_logger.log("SERVLET: writing content.");
		String content = m_run.getContent();
        PrintWriter  out = getWriter();
        out.print(content); //the html or voicexml
        
        m_logger.log("SERVLET: saving state.");
        saveSRRunner(m_request, false); //save new SRRunner
		out.close();
		m_logger.log("SERVLET: done.");
	}
	
	public void continueApp() throws IOException
	{
        String contentType = m_request.getContentType();
        if (contentType != null && contentType.toLowerCase().indexOf("multipart") >= 0)
        {
			log("multipart (recording)..");
        	getAudioRecording(m_request);
        }
        
        doContinueApp();
	}
	
	void getAudioRecording(HttpServletRequest request)
	{
		boolean SUCCESS = true; //always optimistic...
		String res = ""; //default

		try {
		    //set the destination for the recorded audio file
//	        ServletContext context = this.getServletContext();
//		    String dir = request.getRealPath("/audio"); //modify as necessary
			String path = m_run.locations().projectDir();
		    String dir = path + "audio"; //"context.getRealPath("/audio"); //modify as necessary
			log("recording dir: " + dir);

		    //process the request object, save the audio part, parse the posted variables
		    com.oreilly.servlet.MultipartRequest mr = new com.oreilly.servlet.MultipartRequest(request, dir);
			log("mulitpart done.");
		   
		    //assign our posted variables
		    res = mr.getParameter("sr__res");
			log("sr__res: " + res);
		} catch (Exception e) {
		    SUCCESS = false;  //something went wrong.
		}		
	}
	
	
	/**
	 * Continues the SpeakRight app.  This MUST be called once for each postback during a session
	 * (phone call).
	 * Will generate a single VoiceXML page and write it to the print writer.
	 * @throws IOException
	 */
	void doContinueApp() throws IOException
	{
		m_logger.log("SERVLET: continueApp");
        if (m_run == null)
        {
    		m_logger.log("SERVLET: continueApp: no runner. what happened to our session?!!!!!!!!!!!!!");
			return;
        }
//        m_self.initLocations(m_run);
        m_factory.reInitRunner(m_run, m_self);

        PrintWriter  out = getWriter();
		m_run.restoreModelBinder(new ModelBinder());
    	boolean stillRunning = generateNextPage(m_request, out);

    	if (stillRunning) {
    		m_logger.log("SERVLET: writing content.");
    		String content = m_run.getContent();
    		out.print(content); //the html or voicexml
    		saveSRRunner(m_request, true); //save SRRunner again

    		out.close();
    	}
		m_logger.log("SERVLET: done.");
	}
	
	boolean getMode()
	{
	    //do this here so we look for local log4j.properties file (in web-inf\classes) first
	    SRLogger logger = SRLogger.createLogger();

//        logger.log("in doGet!");
        boolean b = false;
        String str = m_request.getParameter("mode");
//        logger.log("mode: " + str);
        if (str!= null && str.equals("html"))
        {
        	logger.log("doing html!");
        	b = true;
        	m_response.setContentType("text/html");
        }
        else { //vxml
        	logger.log("doing vxml!");
        	
        }
	    
		m_response.setHeader("pragma", "no-cache");
		m_doingHTML = b;
		return b;
	}

	String getUrl(HttpServletRequest request)
	{
        String scheme = request.getScheme();             // http
        String serverName = request.getServerName();     // hostname.com
        int serverPort = request.getServerPort();        // 80
        String contextPath = request.getContextPath();   // /mywebapp
        String servletPath = request.getServletPath();   // /servlet/MyServlet
//        String pathInfo = request.getPathInfo();         // /a/b;c=123
//        String queryString = request.getQueryString();          // d=789
    
        // Reconstruct original requesting URL
        String url = scheme+"://"+serverName+":"+serverPort+contextPath+servletPath;
        return url;		
	}

	String getServletUrl(HttpServletRequest request)
	{
        String scheme = request.getScheme();             // http
        String serverName = request.getServerName();     // hostname.com
        int serverPort = request.getServerPort();        // 80
        String contextPath = request.getContextPath();   // /mywebapp
//        String servletPath = request.getServletPath();   // /servlet/MyServlet
//        String pathInfo = request.getPathInfo();         // /a/b;c=123
//        String queryString = request.getQueryString();          // d=789
    
        // Reconstruct original requesting URL
        String url = scheme+"://"+serverName+":"+serverPort+contextPath; //+servletPath;
        return url;		
	}
	
	/**
	 * Create an SRRunner object for this session (phone call)
	 * @param servlet
	 * @return
	 */
	public SRRunner createNewSRRunner(HttpServlet servlet)
	{
		//init SRConfig, which must be done before SRConfig.getProperty can be called
		ServletContext context = servlet.getServletContext();
		String path = context.getRealPath("/");
		SRConfig.init(path, "srf.properties");
		
//        SRRunner run = new SRRunner();
        String returnUrl = getUrl(m_request);
        String baseUrl = getServletUrl(m_request);

        
        m_run = m_factory.createRunner(path, returnUrl, baseUrl, m_self);
//      m_logger.log("SpeakRight version: " + SRRunner.SPEAKRIGHT_VERSION);
        m_run.setExtensionFactory(this);
//      setupUrls(run, m_request);
//      m_run = run;
//        m_run.log("zzzrrrrrrrrrrrrrrrrrrrrr");
//      run.locations().setProjectDir(path);
//      m_self.initLocations(m_run);
        m_run.log("config path: " + SRConfig.configPath());
        
        return m_run;
	}

//	//!!remove later -- move to SRFactory
//	void setupUrls(SRRunner run, HttpServletRequest request)
//	{
//        // Reconstruct original requesting URL
//        String url = getUrl(request);
//        run.setReturnUrl(url); 
//        
//        url = getServletUrl(request);
//        run.setGrammarBaseUrl(url);
//        run.setPromptBaseUrl(url);
//        
////        String x = request.getPathTranslated();
////        run.log("gtran " + x);
////        SRLocations locations = run.locations();
////        locations.setProjectDir(x);
//	}
	
	/**
	 * Create a page writer that renders SpeakRight output content
	 * into VoiceXML or whatever markup text you want.
	 * @return
	 */
	public ISpeechPageWriter createPageWriter()
	{
		if (m_doingHTML)
		    return new HTMLSpeechPageWriter();
		else
			return new VoiceXMLSpeechPageWriter();
	}
	
	void saveSRRunner(HttpServletRequest request, boolean alreadyExists)
	{
		HttpSession session = request.getSession();
        SRRunner savedRun = (SRRunner)session.getAttribute("runner");
        if (!alreadyExists && savedRun != null)
        {
        	m_logger.log("WARNING: new session GET and runner already exists!");
        }
        m_run.prepareToPassivate(); //must call this!
        session.setAttribute("runner", m_run);
//    	System.out.println("saved SRRunner");	        
	}
	
	SRRunner activateSavedSRRunner(HttpServletRequest request)
	{
		SRLogger logger = SRLogger.createLogger();
        HttpSession session = request.getSession();
        SRRunner run = (SRRunner)session.getAttribute("runner");
        if (run == null)
        {
        	logger.log("POST and no runner!");
			return null;
        }
        else
        {
        	logger.log("POST restored saved session");	        
	        run.restoreModelBinder(new ModelBinder());
	        run.finishActivation(); //must call this!
			run.setExtensionFactory(this);
//	        setupUrls(run, request);
			m_factory.initUrls(run, getUrl(m_request), getServletUrl(m_request));
	        m_run = run;
	        return run;
        }
	}
	
	boolean generateNextPage(HttpServletRequest request, java.io.PrintWriter out)
	{
		SRRunner run = m_run;
        HttpSession session = request.getSession();

        if (run.isFinished()) {
        	finish(out, session);
			return false;
		}
        
        
        RawCGIParams rawParams = new RawCGIParams();
        Enumeration paramNames = m_request.getParameterNames();
        while (paramNames.hasMoreElements()) {
        	String name = (String) paramNames.nextElement();
        	String[] values = m_request.getParameterValues(name);
        	for (int i = 0; i < values.length; i++) {
        		//out.println("      " + values[i]);
        		//I assume most of the time, each param only has one value!
        		rawParams.add(name, values[i]);
        	}
       	}
       
        SRResults results = new SRResults(rawParams);
        run.proceed(results);

	    if (run.isFinished()) {
	    	finish(out, session);
			return false;
		}
	    return true;
	}
	
	String getcgiparam(HttpServletRequest request, String param)
	{
		String result = request.getParameter(param);
		if (result == null) {
			result = "";
		}
		return result.trim();
	}
	
	void finish(java.io.PrintWriter out, HttpSession session)
	{
		writeFinPage(out);
		session.invalidate(); //done
		out.close();
	}
	void writeFinPage(java.io.PrintWriter out)
	{
		m_run.generateFinPage();
		out.print(m_run.getContent()); //the html or voicexml
//      out.println("<html>");
//      out.println("<head>");
//      out.println("<title>App has finished</title>");
//      out.println("</head>");
//      out.println("<body>");
//      out.println("<h1>App has finished</h1>");
//      out.println("</body>");
//      out.println("</html>");
	}
	
	
}

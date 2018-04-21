package org.speakright.itest;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.speakright.core.*;
import org.speakright.core.render.ISpeechPageWriter;
import org.speakright.core.render.voicexml.*;
import org.speakright.core.render.html.*;

/**
 * An interactive tester for console applications.
 * Pass it the application IFLow (and an optional IModel object),
 * and then invoke the <code>run</code> method.
 * 
 * InteractiveTester presents a command prompt '>' where you can type
 *  'q'  to quit
 *  'g' or 'go' to generate the next voicexml page.
 *              You can pass user input as well, such as "g boston"
 *              The input simulates the results of running the previous page
 *              on a VoiceXML server, where it would send user input back to
 *              the web app hosting the SpeakRight application.
 *  'bye'       disconnect the session.
 *  
 *  You can test this class using org.speakright.core.tests.TestInteractive
 * @author Ian Rae
 *
 */
public class SRInteractiveTester implements ISRFactory {

	boolean m_done; //quit cmd has been input
	boolean m_hasFinished; //interpreter has finished
	boolean m_hasGeneratedFinPage; //after interpreter finished we have to gen fin page once only.
	Cmd m_cmd;
	SRInstance m_run;
	IFlow m_flow;
	TrailWrapper m_wrap1;
	String m_outputDir = "";
	int m_turnNumber = 0;
	boolean m_genVXML = true;
	ArrayList<Cmd> m_cmdL = new ArrayList<Cmd>();
	
	public SRInteractiveTester()
	{
		addCmd("q", "", "quit");
		addCmd("go", "g", "run or proceed");
		addCmd("bye", "", "simulate a DISCONNECT");
		addCmd("status", "s", "show interpreter status");
		addCmd("out", "", "turn on file output of each page in tmpfiles dir");
		addCmd("ret", "", "set return url");
		addCmd("gramurl", "gurl", "gram base dir");
		addCmd("prompturl", "purl", "prompt base dir");
		addCmd("html", "h", "switch to HTML output");
		addCmd("vxml", "", "switch to VXML output");
	}
	void addCmd(String cmdName, String shortForm, String help)
	{
		Cmd cmd = new Cmd();
		cmd.m_cmd = cmdName;
		cmd.m_cmdShortForm = shortForm;
		cmd.m_help = help;
		m_cmdL.add(cmd);
	}
	
	public void setOutputDir(String path)
	{
		m_outputDir = SRLocations.fixupDir(path);
		System.out.println("page files will be written to: " + m_outputDir);
	}
	
	/**
	 * Start the app.  This method is  for advanced uses;
	 * using <code>run</code> is the preferred way to execute an app.
	 * @param flow the application flow object
	 * @param model (optional) model 
	 */
	protected void start(IFlow flow, IModel model)
	{
		System.out.println("SpeakRight ITester..");
		m_done = false;
		m_hasFinished = false;
		m_hasGeneratedFinPage = false;
		m_turnNumber = 0;
		
		m_flow = flow;
		m_wrap1 = new TrailWrapper(m_flow);
		System.out.println("RUNNING " + m_flow.name() + ".........");
		m_run = new SRInstance();
		if (model != null) {
			m_run.SetModelBinder(model, new ModelBinder(model));
		}
		m_run.setFactory(this);
	}
	
	
	public ISpeechPageWriter createPageWriter() {
		return (m_genVXML) ? new VoiceXMLSpeechPageWriter() : new HTMLSpeechPageWriter();
	}

	/**
	 * Check if finished.  
	 * @return whether app has finished
	 */
	protected boolean isDone()
	{
		return m_done;	
	}
	/**
	 * Read the next line from the console.  
	 */
	protected void readLine()
	{
		InputStreamReader stdin = new InputStreamReader(System.in);
		BufferedReader console = new BufferedReader(stdin);

		String line = "";
		try
		{
			System.out.print(String.format("%d> ", m_turnNumber + 1));
			line = console.readLine();
			line = line.trim();
		}
		catch(Exception e)
		{}
		
		
		line = line.trim();
		if (line.equals("q"))
			m_done = true;
		else
		{
			line = line.toLowerCase();
			
			String[] sar = line.split(" ");
			Cmd match = null;
			for(Cmd cmd : m_cmdL) {
				String s = sar[0];
				if (cmd.isMatch(s))
				{
					match = cmd;
					match.m_param1 = ""; //clear
					if (sar.length > 1) {
						match.m_param1 = sar[1];
					}
					break;
				}
			}
			if (match == null) {
				out("???");
				out("available cmds: ");
				for(Cmd cmd : m_cmdL) {
					out("  " + cmd.m_cmd + " -- " + cmd.m_help);
				}
				out("---");
			}
			
//			//handle shortforms
//			if (line.equals("g")) line = "go";
//			if (line.startsWith("g ")) line = "go" + line.substring(1);
//			if (line.equals("s")) line = "status";
			
			m_cmd = match;
		}
	}
	/**
	 * Process the console input.  
	 */
	protected void execute()
	{
		if (m_done || m_cmd == null) return;
		String cmdName = m_cmd.m_cmd;
		out("cmd: " + cmdName);
		
		//wierd java thing.  string == does a pointer comparison, not a content comparison!!
		//use equals to compare actual strings
		if (cmdName.equals("status")) {
			if (m_run.isFinished()) {
				boolean success = ! m_run.isFailed();
				out("FINISHED: " + success);
			}
			else {
				IFlow peek = m_run.peekCurrent();
				out("current: " + peek.name());
			}
		}
		if (cmdName.equals("out")) {
			this.setOutputDir("tmpfiles\\");
		}
		else if (cmdName.equals("ret")) {
			m_run.setReturnUrl("http://www.somecompany.com/app1"); //from cmd later!!
			out("setting ret-url");
		}
		else if (cmdName.equals("gramurl")) {
			m_run.setGrammarBaseUrl("grammars/"); //from cmd later!!
			out("setting gram-url");
		}
		else if (cmdName.equals("prompturl")) {
			m_run.setPromptBaseUrl("audio/"); //from cmd later!!
			out("setting gram-url");
		}
		else if (cmdName.equals("html")) {
			m_genVXML = false;
			out("HTML output");
		}
		else if (cmdName.equals("vxml")) {
			m_genVXML = true;
			out("VXML output");
		}
		else if (cmdName.equals("go")) {
			if (m_hasFinished) {
				if (! m_hasGeneratedFinPage) {
					m_hasGeneratedFinPage = true;
					m_turnNumber++;
					out("gen FIN PAGE");
					m_run.generateFinPage();
					showContent(); //the final page
				}
				else {
					out("HAS ALREADY FINISHED!");
				}
			}
			else {
				doGo();
			}
		}
		else if (cmdName.equals("bye")) {
			if (! m_run.isStarted()) {
			}
			else {
				SRResults results = new SRResults("", SRResults.ResultCode.DISCONNECT);
				m_run.proceed(results);
			
				if (m_run.isFinished()) {
					finish();
				}
			}
		}
		else {
			out("??");
		}		
	}
	
	void doGo()
	{
		m_turnNumber++;
		
		if (! m_run.isStarted()) {
			boolean b = m_run.start(m_flow);
			out("b = " + b);
			showContent();
//		Assert.assertTrue(b);
		}
		else {
			String input = m_cmd.m_param1;
			
			//look for slot
			String slot = "";
			int pos = input.indexOf(':');
			if (pos > 0) {
				slot = input.substring(0, pos);
				input = input.substring(pos+1);
				out(String.format("slot %s = %s", slot, input));
			}
			SRResults results = new SRResults(input, SRResults.ResultCode.SUCCESS);
			results.m_slotName = slot;
			
			m_run.proceed(results);
		
			if (m_run.isFinished()) {
				finish();
			}
			else {
				showContent();
				
				if (m_run.peekCurrent() instanceof DisconnectFlow) {
					results = new SRResults("", SRResults.ResultCode.DISCONNECT);
					m_run.proceed(results);
				
					if (m_run.isFinished()) {
						finish();
					}						
				}
			}
				
		}
	}
	
	void finish()
	{
		out("FIN.......................");
		m_hasFinished = true;
		showContent(); //fin page
	}
	void showContent()
	{
		String content = m_run.getContent();
		
		int pos = content.indexOf((m_genVXML) ? "<form>" : "<body>");
		String toDisplay = content;
		if (pos >= 0) {
			toDisplay = content.substring(pos); //trim header
		}

		System.out.println(toDisplay);
		if (m_outputDir != "") {
			PageWriter w = new PageWriter();
			String ext = (m_genVXML) ? "vxml" : "html";
			String file = String.format("page%d.%s", m_turnNumber, ext);
			w.write(m_outputDir + file, content);
		}
	}
	void out(String s)
	{
		System.out.println(s);
	}
	
	/**
	 * Starts execution of the SpeakRight application.  Use this
	 * version of run when there is no model.
	 * @param flow  application flow object
	 */
	public void run(IFlow app)
	{
		run(app, null); //run with no model
	}
	
	/**
	 * Starts execution of the SpeakRight application
	 * @param flow  application flow object
	 * @param model model for the app. Optional. use null if the app doesn't
	 * have a model.
	 */
	public void run(IFlow app, IModel model)
	{
		start(app, model);
		
		while(! isDone()) {
			readLine();
			execute();
		}		
	}
	
	class Cmd 
	{
		public String m_cmd;
		public String m_cmdShortForm;
		public String m_help;
		
		//the following get set each time this cmd is invoked
		public String m_param1 = "";
		
		boolean isMatch(String line)
		{
			if (m_cmd.equals(line)) {
				return true;
			}
			else if(m_cmdShortForm.length() > 0 && m_cmdShortForm.equals(line)) {
				return true;
			}
			return false;
		}
	}
}

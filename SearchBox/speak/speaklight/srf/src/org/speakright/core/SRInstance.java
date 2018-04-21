/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Stack;
import java.util.ArrayList;

import org.speakright.core.render.*;

/**
 * SRInstance is the SpeakRight runtime for a single call.  It manages
 * execution of the flow objects.
 * 
 * It is Serializable so SpeakRight can save & restore state between HTTP requests.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class SRInstance implements Serializable, IPromptAdjuster, IGrammarAdjuster {
	//state that must be peristed
	Stack m_flowStack = new Stack(); //stack of IFlow objects
	IFlow m_lastExecutedOrPopped = null;
	public Trail m_trail = new Trail(); //trail of Executes
	boolean m_isStarted = false;
	boolean m_isFinished = false;
	SRResults m_pausedResults; //used for Resume
	IModel m_model; //can be null
	SRLocations m_locations = new SRLocations();
	PlayOnceTracker m_playOnceTracker = new PlayOnceTracker();
	int m_uci; //unique call identifier
	static int the_next_uci; //!!must be thread-safe
	ArrayList m_savedBindingL = new ArrayList(); //persisted from ModelBinder
	public boolean m_dtmfOnlyModeIsActive = false;
	
	//transient variables  !!must update prepareToPassivate
	transient boolean m_stopNow = false; //only used in Continue
	transient IModelBinder m_binder; //can be null
	transient String m_content = "";
	transient IFlow m_appFlow;
	transient SRError m_err = new SRError("SpeakRight interpreter"); //tracks our errors
	transient SRLogger m_logger = SRLogger.createLogger();
	transient ISRExtensionFactory m_factory = new StandardFactory();
	transient ArrayList<String> m_promptFileL = new ArrayList<String>();
	transient String m_returnUrl = ""; //address of our servlet so vxml can submit back to us
	transient ISRInstrumentation m_instrumentation; //null by default
	transient FieldBinder m_fieldBinder = new FieldBinder();
	transient boolean m_logPrompts = false;
	transient ContentLogger m_contentLogger; //default is null
	
	/**
	 * When saving state in a servlet, we're going to just save the
	 * whoe SRInstance object in the HttpSession.  But we need to 
	 * clear the transient fields since they are not needed and
	 * are not serializable.
	 *
	 */
	public void prepareToPassivate()
	{
		if (m_binder != null) {
			m_savedBindingL = m_binder.getBindings();
		}
		
		m_stopNow = false; 
		m_binder = null; 
		String m_content = "";
		//m_appFlow = null;   Don't set to null so unit tests can see the previous appflow.
		//This should increase the size of the serialized data since m_appFlow points to an object
		//in the flow stack, which we serialize already.
		m_err =  null;
		m_logger  = null;
		m_factory  = null;
	}
	
	/**
	 * MUST be called after activating (restoring this object using serialization).
	 * It re-initializes the SRInstance transient fields.
	 */
	public void finishActivation()
	{
		m_stopNow = false; 
		String m_content = "";
		//leave binder null until setModelBinder done
//		m_binder = null; 

		//if app is finished then we can't set appflow. Careful!
		if (m_flowStack.empty())
		{
			m_appFlow = null;
		}
		else {
			m_appFlow = (IFlow)m_flowStack.firstElement(); //bottom obj on stack is the app
		}
		m_err = new SRError("SpeakRight interpreter");
		m_logger = SRLogger.createLogger();
		
		//set factory. you can override it using setFactory after
		//this method
		m_factory = new StandardFactory();

		m_promptFileL = new ArrayList<String>();

//		m_savedBindingL = m_binder.getBindings();
		m_fieldBinder = new FieldBinder();
	}
	
	/**
	 * Register a prompt file, for the duration of the call.
	 * @param path
	 */
	public void registerPromptFile(String path)
	{
		m_promptFileL.add(path); //added permanently
	}
	
	/**
	 * Enable/disable logging of prompts during rendering.
	 * @param b
	 */
	public void setPromptLogging(boolean b)
	{
		m_logPrompts = b;
	}
	
	/**
	 * Set the return url, which is used in the submit VoiceXML tag to
	 * POST the results of a page back.  For a java servlet, the return
	 * url is simply the servlet's URL.
	 * @param url  
	 */
	public void setReturnUrl(String url)
	{
		m_returnUrl = url;
	}
	
	/**
	 * Set the URL of grammar files.  Any grammar urls in the app that
	 * are relative urls will have this url pre-pended.
	 * @param url  usually a url within the java web application, such
	 *  as "http://somecompany.com/speechapp1/grammar"
	 */
	public void setGrammarBaseUrl(String url)
	{
		m_locations.m_baseGrammarUrl = url;
	}
	
	/**
	 * Set the base URL of sro files.  Under this there should
	 * be audio/ and grammar/ sub-dirs.
	 * @param url  usually a url within the java web application, such
	 *  as "http://somecompany.com/speechapp1/sro"
	 */
	public void setSROBaseUrl(String url)
	{
		m_locations.m_baseSROUrl = url;
	}
	
	/**
	 * Set the URL of audio files.  Any audio urls in the app that
	 * are relative urls will have this url pre-pended.
	 * @param url  usually a url within the java web application, such
	 *  as "http://somecompany.com/speechapp1/audio"
	 */
	public void setPromptBaseUrl(String url)
	{
		m_locations.m_basePromptUrl = url;
	}
	
	/**
	 * Set the instrumentation object (can be at most one)
	 * @param instrumentation object that gets notified of important things
	 * during the session (phone call).
	 */
	public void setInstrumentation(ISRInstrumentation instrumentation)
	{
		m_instrumentation = instrumentation;
	}
	
	public void setContentLogging(String dir)
	{
		m_contentLogger = new ContentLogger(m_uci, dir, m_logger);
	}
	
	
	public SRInstance()
	{
		genUCI();
	}
	
	/**
	 * used for unit testing only
	 *
	 */
	public static void forceUCIReset()
	{
		the_next_uci = 0; 
	}
	
	synchronized void genUCI()
	{
		the_next_uci++; //thread-safe
		m_uci = the_next_uci; 
	}

	/**
	 * Unique Call Identifier (UCI) is a value generated by SpeakRight to distinguish
	 * one session (phone call) from another.  Each session has its own SRInstance object,
	 * so the UCI is stored here.
	 * @return the UCI
	 */
	public int UCI()
	{
		return m_uci;
	}
	
	/**
	 * Log to the SpeakRight logger (log4j)
	 * @param message
	 */
	public void log(String message)
	{
		if (m_logger != null)
			m_logger.log(message);
	}
	
	/**
	 * extracts any errors that this SRInstance object logged.
	 * @param parent  error object to copy error info into.
	 * @return true if any errors have occured.
	 */
	public boolean failed(SRError parent)
	{
		return m_err.failed(parent);
	}
	
	/**
	 * Get the outermost flow object, that was passed to <code>start</code>.
	 * @return
	 */
	public IFlow ApplicationFlow()
	{
		return m_appFlow;
	}
	
	/**
	 * Set the model and model binder.  Use of a model is optional in a SpeakRight application,
	 * but if you're app has a model, then you MUST call this before calling <code>start</code>
	 * @param model the IModel object used by this session (phone call)
	 * @param binder a model binder (usually ModelBinder)
	 */
	public void setModelBinder(IModel model, IModelBinder binder)
	{
		m_model = model;
		m_binder = binder;
		m_binder.setModel(model);
	}
	
	/**
	 * Restore the model binder.  Used after de-serialization (aka. activation).  For performance reasons,
	 * we don't serialize the model binder, so you must invoke this method after activation in order
	 * to restore this SRInstance object properly.
	 * Only necessary to call this if your app has a model.
	 * @param binder a new ModelBinder object.
	 */
	public void restoreModelBinder(IModelBinder binder)
	{
		log("restoring model binder");
		if (m_model == null)
			return;
		log("restoring model binder 2");
		m_binder = binder;
		m_binder.setBindings(m_savedBindingL);
		//m_model already set by serialization load
		m_binder.setModel(m_model);
	}
	
	/**
	 * Set the extension point factory.  This method
	 * is optional because SRInstance initializes itself
	 * to use a default factory.
	 * Use this method when you have created your own factory.
	 * @param factory
	 */
	public void setExtensionFactory(ISRExtensionFactory factory)
	{
		m_factory = factory;
	}

	/**
	 * Get the current language 
	 * @return language, such as "en-us"
	 */
	public String language()
	{
		return m_locations.language();
	}
	/**
	 * Set the current language.
	 * @param s language, such as "en-us"
	 */
	public void setLanguage(String s)
	{
		m_locations.setLanguage(s);
	}
	
	/**
	 * Get the locations object.
	 * @return
	 */
	public SRLocations locations()
	{
		return m_locations;
	}
	
	/**
	 * Has an error ocurred yet.
	 * @return true if error has ocurred.
	 */
	public boolean isFailed()
	{
		return m_err.failure();
	}

	void Reset()
	{
		m_isStarted = false;
		m_isFinished = false;
		m_stopNow = false;
		m_flowStack.clear();
		m_lastExecutedOrPopped = null;
		m_trail.clear();
	}
	
	/**
	 * Start the application.
	 * @param flow the application flow object.
	 * @return whether app was started successfully.
	 */
	public boolean start(IFlow flow)
	{
		if (m_isStarted)
		{
			CoreErrors.logError(m_err, CoreErrors.BadInterpreterState,
					String.format("start has already been called: flow = %s", flow.name()));			
			return false;
		}
		Reset();
		m_appFlow = flow;
		m_isStarted = true;
		m_stopNow = false;
		log(String.format("START(uci=%d): %s", m_uci, flow.name()));
		if (m_instrumentation != null) {
			m_instrumentation.onCallBegin(m_uci, flow);
		}
		push(flow);
		run(null);
		if (m_stopNow) {
			finish();
		}
		return true;
	}
	void push(IFlow flow)
	{
		//after a catch we don't want to re-push the active flow that
		//just caught the event
		if (flow != null && ! m_flowStack.isEmpty()) {
			IFlow top = peek();
			if (flow == top) {
				log("skipping a re-push of: " + flow.name());
				return;
			}
		}
		
		
		log(" push " + flow.name());
		flow.setExecutionCount(1); //reset
		
		injectModel(flow);
		
		if (m_instrumentation != null) {
			m_instrumentation.onActivateFlow(m_uci, flow);
		}
		flow.onBegin(getFlowContext());
		m_flowStack.push(flow);
	}
	
	void injectModel(IFlow flow) {
		//we inject flow into model lazily
		if (m_binder != null) {
			m_binder.injectModel(flow);
			m_binder.failed(m_err); //propogate errors
		}		
	}
	IFlow peek()
	{
		return (IFlow)m_flowStack.peek();
	}
	
	/**
	 * Get the object just below the current top-of-stack object
	 * (if there is one).
	 * Used for optional sub-flows.
	 */
	IFlow peekNextFromTop()
	{
		int n = m_flowStack.size();
		if (n <= 1) {
			return null;
		}
		IFlow flow = (IFlow)m_flowStack.elementAt(n - 2);
		return flow;
	}
	
	void pop()
	{
		IFlow flow = (IFlow) m_flowStack.pop();
		log(" pop  " + flow.name());
		flow.onEnd(getFlowContext());
		m_playOnceTracker.removeAllFor(flow);
		m_lastExecutedOrPopped = flow; //save for Continue
	}
	
	/**
	 * execute the top flow in the flow stack.
	 * @param results either null (if app is just starting) or the results of
	 * the previous turn (which may be from the current flow or a previous one)
	 */
	void run(SRResults results)
	{
		//err if stack empty!!
		IFlow flow = peek();
		while(true) {
			IFlow first = findFirstFlow(flow);
			
			if (first == null) {
				CoreErrors.logError(m_err, CoreErrors.GetFirstError,
						String.format("getFirst cannot return null: flow = %s", flow.name()));
				return;
			}
			//first can't be null!!
			if (first != flow)
			{
				push(first);
				flow = first;
			}
			else
				break;
		}
		execute(peek(), results);
	}
	
	private FlowContext getFlowContext()
	{
		FlowContext context = new FlowContext();
		context.m_locations = m_locations;
		return context;
	}
	
	IFlow findFirstFlow(IFlow flow)
	{
		FlowContext context = getFlowContext();
		IFlow first = flow.getFirst(context);
		
		//call shouldExecute, and skip if it returns false
		if (first != null) {
			injectModel(first);
		}
		if (first != null && ! first.shouldExecute()) {
			log(" skipping optional-subflow (shouldExecute false) " + first.name());
			if (first != flow) { //first is a sub-flow of flow?
				first = flow.getSubFlowAfter(first);
				if (first != null) {
					log(" skipping to " + first.name());
					pop(); //remove previous, since we're skipping it
					return first;
				}
			}
			else //first == flow. so we'll default to the old optional-sub-flow case (below)
			{
				first = null;
			}
		}
		
		//normally getFirst never returns null.  But in the case of optional sub-flows, it can.
		//In this case ask the parent (just below it on the stack) for the next sub-flow
		if (first == null) {
			log(" skipping optional sub-flow " + flow.name());
			IFlow parent = peekNextFromTop();
			if (parent != null) { //is a parent?
				first = parent.getSubFlowAfter(flow);
				if (first != null) {
					log(" skipping to " + first.name());
					pop(); //remove previous, since we're skipping it
				}
			}
		}
		return first;
	}
	
	void execute(IFlow flow, SRResults results)
	{
		doExecute(flow, results);
		int count = flow.executionCount();
		flow.setExecutionCount(count + 1); //increment
	}
	void doExecute(IFlow flow, SRResults results)
	{
		if (flow instanceof ExitEvent)
		{
			log("EXIT EVENT!");
			m_stopNow = true;
			return;
		}
		log(String.format("EXEC ---> %s (count=%d)", flow.name(), flow.executionCount()));
		m_trail.add(flow.name());
		ExecutionContext context = new ExecutionContext();
		context.m_binder = m_binder;
		context.m_promptFileL = new ArrayList<String>(); //temporary ones
		context.m_results = results; //can be null
		context.m_flow = flow;
		context.m_locations = m_locations;
		
		if (m_instrumentation != null) {
			m_instrumentation.onExecuteFlow(m_uci, flow);
		}
		
		//now execute
		flow.execute(context);
		
		//Execute can throw an event
		if (context.m_thrownEvent != null)
		{
			log("THROW IN EXECUTE! " + context.m_thrownEvent.name());
			IFlow current = flow;
//			IFlow next = findCatchHandler(current, new SRResults(), context.m_thrownEvent);
			IFlow next = findCatchHandler(current, results, context.m_thrownEvent);
			
			if (next == current) 
			{
				//careful, we don't want to call same Execute again and have it throw another
				//exception -- will get stack overflow
				CoreErrors.logError(m_err, CoreErrors.ExecuteCaughtOwnException,
						String.format("can't catch own thrown event from execute: flow = %s", flow.name()));							
				
				m_stopNow = true;
			}
			else 
			{
				push(next); 
				run(results); 
				return;
	  		}
			
		}
		else if (context.m_renderL.size() > 0) //some content to render?
		{
			m_content = generateOutputContent(flow, context);
		}
		else
		{
			log("empty pge............................");
			CoreErrors.logError(m_err, CoreErrors.NoContentGenerated,
					String.format("Empty pages are not allowed: flow = %s", flow.name()));							
			m_content = "";
		}

		//for now empty pages are an error
		if (m_content == "") {
			generateFinPage();
		}
		
		if (m_contentLogger != null) {
			m_contentLogger.dump(m_content);
		}
		
		m_lastExecutedOrPopped = flow; //save for Continue
	}
	
	/**
	 * Generate the final page for the application.
	 * This page terminates the call and the
	 * VoiceXML session.
	 *
	 */
	public void generateFinPage()
	{
		log("FIN PAGE!");
		ExecutionContext context = new ExecutionContext();
		context.m_promptFileL = new ArrayList<String>(); //

		m_content = generateOutputContent(null, context);
	}
	
	String generateOutputContent(IFlow flow, ExecutionContext context)
	{
//		if (context.m_renderL.size() > 0) //some content to render?
//		{
			SpeechPageRenderer pageRenderer = new SpeechPageRenderer();
			
			RenderContext rcontext = new RenderContext();
			rcontext.m_promptAdjuster = this;
			rcontext.m_grammarAdjuster = this;
			rcontext.m_binder = m_binder;
			rcontext.m_fieldBinder = m_fieldBinder;
			rcontext.m_flow = flow;
			rcontext.m_playOnceTracker = m_playOnceTracker;
			rcontext.m_locations = m_locations;
			rcontext.m_logger = m_logger;
			rcontext.m_promptFileL = new ArrayList<String>();
			//copy both the permanent and temporary ones
			rcontext.m_promptFileL.addAll(m_promptFileL);
			rcontext.m_promptFileL.addAll(context.m_promptFileL);
			rcontext.m_returnUrl = m_returnUrl;
//			rcontext.m_baseGrammarUrl = m_baseGrammarUrl;
//			rcontext.m_basePromptUrl = m_basePromptUrl;
			rcontext.m_dtmfOnlyModeIsActive = m_dtmfOnlyModeIsActive;
			rcontext.m_results = context.m_results;
			rcontext.m_promptGroup = (flow != null) ? flow.promptGroup() : "";
			rcontext.m_logPrompts = m_logPrompts;
			
			SpeechPage page = pageRenderer.render(context.m_renderL, rcontext);
			
			String content = "";
			if (! pageRenderer.failed(m_err)) {
				ISpeechPageWriter writer = m_factory.createPageWriter();
//				VoiceXMLSpeechPageWriter writer = new VoiceXMLSpeechPageWriter();
				writer.setRenderContext(rcontext);
				writer.beginPage();
				writer.render(page);
				writer.endPage();
				
				content = writer.getContent();
			}
			return content;
//		}
//		else
//		{
//			return "";
//		}
	}
	
	/**
	 * Walk the flow stack until a flow object returns
	 * non-null from it's <code>fixupPrompt</code> method.
	 */
	public String fixupPrompt(String item)
	{
		//walk stack down from current to bottom
		//keep calling IFlow.fixupPrompt until it returns non-null

		int n = m_flowStack.size();
		for(int i = n - 1; i >= 0; i--) {
			IFlow flow = (IFlow)m_flowStack.elementAt(i);
			
			String tmp = flow.fixupPrompt(item);
			if (tmp != null) {
				return item;
			}
		}
	
		return null;
	}

	/**
	 * Walk the flow stack until a flow object returns
	 * non-null from it's <code>fixupGrammar</code> method.
	 */
	public Grammar fixupGrammar(Grammar gram)
	{
		//walk stack down from current to bottom
		//keep calling IFlow.fixupGrammar until it returns non-null

		int n = m_flowStack.size();
		for(int i = n - 1; i >= 0; i--) {
			IFlow flow = (IFlow)m_flowStack.elementAt(i);
			
			Grammar tmpGram = flow.fixupGrammar(gram);
			if (tmpGram != null) {
				return tmpGram;
			}
		}
	
		return null;
	}


	/**
	 * Get the content (the VoiceXML page).
	 * MUST be called immediately after <code>start</code> or <code>proceed</code>
	 * @return the generated voicexml page.
	 */
	public String getContent()
	{
		return m_content;
	}
	
	/**
	 * Resume (after pausing).  Only used by
	 * async transactions (IFlow.onComplete).
	 *
	 */
	public void resume()
	{
		if (m_pausedResults == null) {
			CoreErrors.logError(m_err, CoreErrors.BadInterpreterState,
					String.format("Can't resume when not paused"));							
			return; 
		}
		log("RESUME..");
		pop(); //check for problems here!!
		proceed(m_pausedResults);
		m_pausedResults = null;
	}
	
	/**
	 * Is the application paused.  Only used by
	 * async transactions (IFlow.onComplete)
	 * @return
	 */
	public boolean isPaused()
	{
		return m_pausedResults != null;
	}
	
	/**
	 * Continue execution, using the given results that
	 * we returned by the voicexml platform to determine what flow
	 * is executed next.
	 * @param results  results from the voicexml platform.  These are the results of executing the previous page.
	 */
	public void proceed(SRResults results)
	{
		results.m_locations = m_locations;
		
		if (! m_isStarted || m_isFinished)
		{
			CoreErrors.logError(m_err, CoreErrors.BadInterpreterState,
					String.format("Proceed called after interpreter has finished"));							
			return;
		}
		m_stopNow = false;
		doContinue(results);
		if (m_stopNow || stackIsEmpty())
		{
			finish();
		}
	}
	boolean stackIsEmpty()
	{
		return m_flowStack.empty();
	}
	
	void logResults(String prefix, SRResults results)
	{
		String s = "";
		for (int i = 0; i < results.slotCount(); i++) {
			SRResults.Slot slot = results.getIthSlot(i);
			s += String.format(" [%s='%s']", slot.m_slotName, slot.m_value);
		}
		s = s.trim();
		
		if (results.m_overallConfidence < 100) {
			s += String.format(" (conf:%d)", results.m_overallConfidence);
		}
		
		log(prefix + results.m_resultCode.toString() + " '" + results.m_input + "'" + 
				" depth: " + m_flowStack.size() + " " + s);
	
	}
	void doContinue(SRResults results)
	{		
		logResults("CONTINUE: ", results);
		int runawayCount = 0;
		while(! m_stopNow)
		{
			runawayCount++;
			if (runawayCount > 100)
			{
				CoreErrors.logError(m_err, CoreErrors.RunawayInterpreter,
						String.format("infinite loop in proceed()"));							
				m_stopNow = true;
				return;
			}
			if (m_flowStack.isEmpty())
			{
				m_stopNow = true;
				return;
			}
			IFlow current = peek();
			log("cont: " + current.name());

			IFlow next = getNextFlow(current, results);
			if (m_stopNow)
			{
				return;
			}
			if (next == null) 
			{
				if (m_pausedResults == null) { //not paused?
					//do any completion logic
					boolean isAsync = ! current.onComplete();
					if (isAsync) {
						m_pausedResults = results;
						log("PAUSED!");
						return;
					}
				}
				pop(); 
			}  
			else if (next == current) 
			{ 
				execute(next, results); 
				return; 
			}
			else 
			{
				push(next); 
				run(results); 
				return;
	  		}
		}
	}
	
	IFlow getNextFlow(IFlow current, SRResults results)
	{
		IFlow next = null;
		
		if (results.m_resultCode == SRResults.ResultCode.SUCCESS) {
			//validation code..
			if (results.m_input != "") {
				
				if (m_instrumentation != null) {
					m_instrumentation.onUserInput(m_uci, current, results);
				}
				
				results.m_validateSucceeded = current.validateInput(results.m_input, results);
				if (results.m_validateSucceeded) {

					//bind data
					if (m_binder != null) {
						m_binder.bind(current, results);
						m_binder.failed(m_err); //propogate errors
					}
				}
				else {
					log("VALIDATE FAILED in " + current.name());
					next = findValidateFailedHandler(current, results);
				}
			}
			
			if (next == null)
				next = current.getNext(m_lastExecutedOrPopped, results);
		}
		else if (results.m_resultCode == SRResults.ResultCode.DISCONNECT) {
			next = findHandler(current, results, results.m_resultCode);
		}
		else if (results.m_resultCode == SRResults.ResultCode.NOINPUT) {
			next = findHandler(current, results, results.m_resultCode);
		}
		else if (results.m_resultCode == SRResults.ResultCode.PLATFORM_ERROR) {
			next = findHandler(current, results, results.m_resultCode);
		}
		else if (results.m_resultCode == SRResults.ResultCode.TRANSFER_FAILED) {
			next = findHandler(current, results, results.m_resultCode);
		}

		//now check for a throw
		if (next instanceof ThrowEvent) {
			log("THROW! " + next.name());
			next = findCatchHandler(current, results, (ThrowEvent)next);
		}
		return next;
	}
	
	IFlow findCatchHandler(IFlow current, SRResults results, ThrowEvent event)
	{
		IFlow possible = current.onCatch(current, results, event.name(), event);
		while (possible == null) {
			pop();
			if (stackIsEmpty())
			{
				return fail("no CATCH handler!");
			}
			current = peek();
			possible = current.onCatch(current, results, event.name(), event);
		}
		log("CAUGHT by " + current.name());
		return possible; //not null!
	}
	IFlow findValidateFailedHandler(IFlow current, SRResults results)
	{
		IFlow possible = current.onValidateFailed(current, results);
		while (possible == null) {
			pop();
			if (stackIsEmpty())
			{
				return fail("no VALFAIL handler!");
			}
			current = peek();
			possible = current.onValidateFailed(current, results);
		}
		log("VALFAILED by " + current.name());
		return possible; //not null!
	}
	IFlow findHandler(IFlow current, SRResults results, SRResults.ResultCode target)
	{
		IFlow possible = getPossible(current, results, target);
		while (possible == null) {
			pop();
			if (stackIsEmpty())
			{
				return fail("no " + target.toString() + " handler!");
			}
			current = peek();
			possible = getPossible(current, results, target);
		}
		log("HANDLER(" + target.toString() + ") is " + current.name());
		return possible; //not null!
	}
	IFlow getPossible(IFlow current, SRResults results, SRResults.ResultCode target)
	{
		IFlow flow = null;
		switch(target) {
		case DISCONNECT:
			flow = current.onDisconnect(current, results);
			break;
		case NOINPUT:
			flow = current.onNoInput(current, results);
			break;
		case PLATFORM_ERROR:
			flow = current.onPlatformError(current, results);
			break;
		case TRANSFER_FAILED:
			flow = current.onTransferFailed(current, results);
			break;
		}
		return flow;
	}
	
	IFlow fail(String logmsg)
	{
		CoreErrors.logError(m_err, CoreErrors.EventNotCaught,
				String.format(logmsg));							
		m_stopNow = true;
		return new ExitEvent();
	}
	
	void finish()
	{
		log("FINISH!");
		m_isFinished = true;
		
		//finish any flows remaining on the stack
		while(! stackIsEmpty()) {
			pop();
		}
		if (m_instrumentation != null) {
			m_instrumentation.onCallEnd(m_uci);
		}
	}
	
	/**
	 * Has the application finished.
	 * @return true if fin
	 */
	public boolean isFinished()
	{
		return m_isFinished;
	}
	
	/**
	 * Has <code>start</code> been called.
	 * @return true if started.
	 */
	public boolean isStarted()
	{
		return m_isStarted;
	}
	
	/**
	 * Get the currently executing flow object.
	 * @return flow object that's on the top of the flow stack.
	 */
	public IFlow peekCurrent()
	{
		return peek();
	}
	
	/**
	 * used for unit tests only.
	 * @param flow
	 * @return
	 */
	public boolean runAll(IFlow flow)
	{
		start(flow);
		while (! m_isFinished)
		{
			//IFlow current = Peek();
			SRResults results = new SRResults();
			proceed(results);
		}
		return true;
	}
}

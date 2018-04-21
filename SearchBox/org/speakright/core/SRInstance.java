package org.speakright.core;
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
public class SRInstance implements Serializable, IPromptAdjuster {
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
	
	//transient variables  !!must update prepareToPassivate
	transient boolean m_stopNow = false; //only used in Continue
	transient IModelBinder m_binder; //can be null
	transient String m_content = "";
	transient IFlow m_appFlow;
	transient SRError m_err = new SRError("SpeakRight interpreter"); //tracks our errors
	transient SRLogger m_logger = SRLogger.createLogger();
	transient ISRFactory m_factory = new StandardFactory();
	transient ArrayList<String> m_promptFileL = new ArrayList<String>();
	transient String m_returnUrl = ""; //address of our servlet so vxml can submit back to us
	transient String m_baseGrammarUrl = ""; //empty or address of grammars dir, such as "grammars/"
	transient String m_basePromptUrl = ""; //empty or address of auiod file dir, such as "audio/"

	/**
	 * When saving state in a servlet, we're going to just save the
	 * whoe SRInstance object in the HttpSession.  But we need to 
	 * clear the transient fields since they are not needed and
	 * are not serializable.
	 *
	 */
	public void prepareToPassivate()
	{
		m_stopNow = false; 
		m_binder = null; 
		String m_content = "";
		m_appFlow = null;
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
		this.m_baseGrammarUrl = url;
	}
	
	/**
	 * Set the URL of audio files.  Any audio urls in the app that
	 * are relative urls will have this url pre-pended.
	 * @param url  usually a url within the java web application, such
	 *  as "http://somecompany.com/speechapp1/audio"
	 */
	public void setPromptBaseUrl(String url)
	{
		this.m_basePromptUrl = url;
	}
	
	public SRInstance()
	{
	}
	
	public void log(String message)
	{
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
	
	public void SetModelBinder(IModel model, IModelBinder binder)
	{
		m_model = model;
		m_binder = binder;
		m_binder.setModel(model);
	}
	public void restoreModelBinder(IModelBinder binder)
	{
		m_binder = binder;
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
	public void setFactory(ISRFactory factory)
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
		log("START: " + flow.name());
		push(flow);
		run(null);
		if (m_stopNow) {
			finish();
		}
		return true;
	}
	void push(IFlow flow)
	{
		log(" push " + flow.name());
		
		//we inject flow into model lazily
		if (m_binder != null) {
			m_binder.injectModel(flow);
			m_binder.failed(m_err); //propogate errors
		}
		
		flow.onBegin();
		m_flowStack.push(flow);
	}
	IFlow peek()
	{
		return (IFlow)m_flowStack.peek();
	}
	void pop()
	{
		IFlow flow = (IFlow) m_flowStack.pop();
		log(" pop  " + flow.name());
		flow.onEnd();
		m_playOnceTracker.removeAllFor(flow);
		m_lastExecutedOrPopped = flow; //save for Continue
	}
	
	/**
	 * execute the top flow in the flow stack.
	 * @param results either null (if app is just starting) or the results of
	 * the previous turn (which may be from the current flow or a previous one)
	 */
	public void run(SRResults results)
	{
		//err if stack empty!!
		IFlow flow = peek();
		while(true) {
			IFlow first = flow.getFirst();
			
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
	
	void execute(IFlow flow, SRResults results)
	{
		if (flow instanceof ExitEvent)
		{
			log("EXIT EVENT!");
			m_stopNow = true;
			return;
		}
		log("EXEC " + flow.name());
		m_trail.add(flow.name());
		ExecutionContext context = new ExecutionContext();
		context.m_binder = m_binder;
		context.m_promptFileL = new ArrayList<String>(); //temporary ones
		context.m_results = results; //can be null
		
		//first bind any model vars that were done using flowbase.addbinding
		flow.processEarlyBindings(context);
		//now execute
		flow.execute(context);
		
		//Execute can throw an event
		if (context.m_thrownEvent != null)
		{
			log("THROW IN EXECUTE!");
			IFlow current = flow;
			IFlow next = findCatchHandler(current, new SRResults(), context.m_thrownEvent);
			
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
			rcontext.m_binder = m_binder;
			rcontext.m_flow = flow;
			rcontext.m_playOnceTracker = m_playOnceTracker;
			rcontext.m_locations = m_locations;
			rcontext.m_logger = m_logger;
			rcontext.m_promptFileL = new ArrayList<String>();
			//copy both the permanent and temporary ones
			rcontext.m_promptFileL.addAll(m_promptFileL);
			rcontext.m_promptFileL.addAll(context.m_promptFileL);
			rcontext.m_returnUrl = m_returnUrl;
			rcontext.m_baseGrammarUrl = m_baseGrammarUrl;
			rcontext.m_basePromptUrl = m_basePromptUrl;
			
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
	 * async transactions (IFlow.doTransaction).
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
	 * async transactions (IFlow.doTransaction)
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
	void doContinue(SRResults results)
	{		
		log("CONTINUE " + results.m_resultCode.toString() + " '" + results.m_input + "'" + 
				" depth: " + m_flowStack.size());
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
					//do transaction.
					boolean isAsync = ! current.doTransaction();
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
				if (current.ValidateInput(results.m_input, results)) {

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
			next = findHandler(current, results, SRResults.ResultCode.DISCONNECT);
		}
		else if (results.m_resultCode == SRResults.ResultCode.NOINPUT) {
			next = findHandler(current, results, SRResults.ResultCode.NOINPUT);
		}

		//now check for a throw
		if (next instanceof ThrowEvent) {
			log("THROW!");
			next = findCatchHandler(current, results, (ThrowEvent)next);
		}
		return next;
	}
	
	IFlow findCatchHandler(IFlow current, SRResults results, ThrowEvent event)
	{
		IFlow possible = current.onCatch(current, results, event.name());
		while (possible == null) {
			pop();
			if (stackIsEmpty())
			{
				return fail("no CATCH handler!");
			}
			current = peek();
			possible = current.onCatch(current, results, event.name());
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

/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import java.io.Serializable;

import org.speakright.core.render.Grammar;
import org.speakright.core.render.IFlowRenderer;

/**
 * IFlow is the basic building block of SpeakRight.
 * A flow object generates VoiceXML pages, and also manages control flow in the
 * application.  In MVC terms, a flow object is both a view and a controller.
 * Flow objects are similar to controls in a GUI application; they encapsulate
 * some user interface behaviour.
 * <p>
 * IFlow objects can contain child IFlow objects (called sub-flows).
 * Everything from the lowest-level prompt-block to a complete SpeakRight
 * application is an IFlow.
 * 
 * IFlow classes are Serializable so that SpeakRight can save & restore state
 * between HTTP requests.  Mark any fields transient that you don't want to
 * be persisted.
 */
@SuppressWarnings("serial")
public interface IFlow extends Serializable {

	/**
	 * Name of the flow object, used for logging and (optionally) as a
	 * means of finding flow objects.  
	 * <p>
	 * Name should be a descriptive single word, such as "AskFlightNumber".
	 * Question flows should begin with a verb such as "Ask", and output
	 * flows should begin with a verb such as "Say".
	 * <p>
	 * If the name is not specified by the developer, a flow's default name
	 * is the class name, for example, org.foobar.flightapp.AskFlightNumber.
	 * Names do not need to be unique.
	 * @return name of the flow object (names are case-sensitive).
	 */
	String name();
	
	/**
	 * Generates a VoiceXML page, which is sent to the speech platform for execution.
	 * SpeakRight will execute a single flow object at a time, waiting for the results
	 * from the speech platform before executing the next flow object.
	 * <p>
	 * Execute may choose to generate no content; this is called an "empty" flow object.
	 * Execute may also choose to generate a flow event.  This is used for error conditions 
	 * where the application can't proceed and needs to jump to a different flow.  For example,
	 * Execute may want to access a web service to play weather information.  If the web 
	 * service is down, a flow event can be thrown.
	 * @param context  Contains the page renderer.
	 */
	void execute(IExecutionContext context);
	
	/**
	 * Gets the first sub-flow to be executed.  getFirst is called when a flow begins
	 * execution.  It can return itself, or a sub-flow.
	 * <p>
	 * Returning null is not allowed.  Some exceptions to this exist; BasicFlow supports
	 * "optional" sub-flows where a sub-flow returns null from getFirst to indicate that it
	 * doesn't wish to run.  However the outer flow object (BasicFlow) ensures that its getFirst
	 * never returns null.
	 * <p>
	 * getFirst may be called more than once per activation (see BasicFlow).
	 * @return an IFlow object to be executed.
	 */
	IFlow getFirst(IFlowContext context);

	/**
	 * Gets the next flow object to be executed.  SpeakRight will call getFirst, and then getNext 
	 * repeatedly until it returns null.  Each call to getNext is returning the results of
	 * the previous VoiceXML page.  The flow object uses the results to determine which sub-flow
	 * to execute next.  It can return itself.  If the flow object is finished, it returns null.
	 * @param current  the currently executing flow object.  More precisely, current is the flow 
	 * object that this object most recently returned from getFirst or getNext.
	 * @param results  the results of <code>current</code>'s execution by the speech platform.  Results
	 * contains user input and/or events such as disconnect or platform-error.
	 * @return the next flow to run, or null which means this flow object is finished. 
	 */
	IFlow getNext(IFlow current, SRResults results);

	/**
	 * The first method called when a flow is executed.  Any initialization code should be placed here.
	 * Called once before getFirst.
	 *
	 */
	void onBegin(IFlowContext context);
	/**
	 * The last method called when a flow is executed.  Any cleanup code should be placed here.
	 * Called once after getNext returns null.  
	 *
	 */
	void onEnd(IFlowContext context);
	
	/**
	 * Event handler for a disconnect event.  Disconnect occurs when the user terminates the
	 * application by hanging up (or when the application disconnects using a HangUp block).
	 * This handler must return an ExitEvent since it makes no sense to keep executing after
	 * the call has ended.
	 * <p>
	 * The SRApp class provides a default implementation that returns an ExitEvent.
	 * @param current  the currently executing flow object.
	 * @param results  the results of <code>current</code>'s execution.  In this case results will
	 * contain a Disconnect result code.  User input may be present, if the caller spoke before hanging
	 * up.  Some applications may want to process this final utterance before terminating.
	 * @return the next flow to run, or null which means this flow object does not handle this
	 * event. 
	 */
	IFlow onDisconnect(IFlow current, SRResults results);
	
	/**
	 * Event handler for the no-input event.  No-input occurs when the VoiceXML was listening
	 * for the caller to speak, but no (valid) utterance was heard.  This can occur is the caller
	 * is silent or her speech is unrecognizable.  Speech recognition engines assign a confidence 
	 * level to each utterance that they match.  If the confidence is less than the configured
	 * recognition-threshold, the utterance is rejected as a "NoReco" error.  VoiceXML pages
	 * will typically retry several times.  Only if the maximum number of retries is exceeded,
	 * is a No-input event generated.
	 * <p>
	 * No-Input only applies if the VoiceXML has active grammars.  An output only page does
	 * not generate this event.
	 * 
	 * <p>
	 * SRApp provides a default implementation that transfers the call to the operator.
	 * In some cases, No-Input is a valid event.  For example, a flow object that reads a long
	 * weather bulletin may be listening for "stop" or "cancel".  If the caller listens to
	 * the whole bulletin without speaking, a No-Input will be returned.  The application ignores
	 * the No-Input by overriding onNoInput in the flow object, and having it return its normal
	 * getNext value.
	 *  
	 * @param current  the currently executing flow object.
	 * @param results  the results of <code>current</code>'s execution.  In this case results will
	 * contain a Disconnect result code.  User input may be present, if the caller spoke before hanging
	 * up.  Some applications may want to process this final utterance before terminating.
	 * @return the next flow to run, or null which means this flow object does not handle this
	 * event. 
	 */
	IFlow onNoInput(IFlow current, SRResults results);

	/**
	 * Event handler for validation failed.  Validation occurs whenever the results of a VoiceXML
	 * page execution contain user input.  The current flow's ValidateInput is called.  If it
	 * returns false then a validate-failed event is generated.
	 * Speech recognition grammars can limit somewhat the user's input.  However, the application
	 * may want to validate the input, such as looking up the flight number in a database.
	 * 
	 * <p>
	 * The default implementation of onValidateFailed is to return <code>this</code>, which causes
	 * the flow object to be executed again.  The ExecutionCount field can
	 * be used to detect re-execution, and generate a retry prompt.  FlowBase provides
	 * the default implemenation.
	 *  
	 * @param current  the currently executing flow object.
	 * @param results  the results of <code>current</code>'s execution.  In this case results will
	 * contain the user input that failed validation.
	 * @return the next flow to run, or null which means this flow object does not handle this
	 * event. 
	 */
	IFlow onValidateFailed(IFlow current, SRResults results);

	/**
	 * Event handler for custom events.  Custom events are generated by the application returning
	 * a ThrowEvent object in getFirst, getNext, or in execute.
	 * Custom events are a form of "goto" in that they cause a jump to another flow.  However,
	 * their behaviour is closer to exceptions in that the flow that "cathes" the event must
	 * be a currently executing flow (that is, is on the flow stack).
	 * Each custom event must have a unique name. 
	 * 
	 * <p>
	 * Event handling in SpeakRight involves a search up the flow stack.  First the currently
	 * executing flow (<code>current</code>) is checked.  If its event handler returns non-null
	 * then the event has been handled.  Otherwise the next flow object on the flow stack is checked,
	 * up to the outermost flow object (the application flow object).
	 * It is an error for an event not to be caught.  SRApp provides default handlers for
	 * all events.
	 * 
	 *  
	 * @param current  the currently executing flow object.
	 * @param results  the results of <code>current</code>'s execution.  
	 * @param eventName name of the event.  most event handling can be done using just the name.  
	 * @param event  event object.  advanced event handling may require the actual event object  
	 * @return the next flow to run, or null which means this flow object does not handle this
	 * event. 
	 */
	IFlow onCatch(IFlow current, SRResults results, String eventName, ThrowEvent event);

	/**
	 * Event handler for a failed transfer.  If a transfer fails due to busy or ring-no-answer 
	 * then this event is thrown.
	 * <p>
	 * The SRApp class provides a default implementation that transfers the call to an operator.
	 * @param current  the currently executing flow object.
	 * @param results  the results of <code>current</code>'s execution.  In this case results will
	 * contain a TransferFailed result code.  
	 * @return the next flow to run, or null which means this flow object does not handle this
	 * event. 
	 */
	IFlow onTransferFailed(IFlow current, SRResults results);

	/**
	 * Event handler for a error returned by the speech platform.  This can be due to many things
	 * such as bad grammar URLs, network problems, or a platform crash. 
	 * <p>
	 * The SRApp class provides a default implementation that transfers the call to an operator.
	 * @param current  the currently executing flow object.
	 * @param results  the results of <code>current</code>'s execution.  In this case results will
	 * contain a PlatformFailed result code.  
	 * @return the next flow to run, or null which means this flow object does not handle this
	 * event. 
	 */
	IFlow onPlatformError(IFlow current, SRResults results);

	/**
	 * Validates user input. Validation occurs whenever the results of a VoiceXML
	 * page execution contain user input.  The current flow's ValidateInput is called.  If it
	 * returns false then a validate-failed event is generated.
	 * Speech recognition grammars can limit somewhat the user's input.  However, the application
	 * may want to validate the input, such as looking up the flight number in a database.
	 * <p>
	 * FlowBase provides the default implementation, which simply returns true.
	 * @param input String value of the user input.  For simple, single-value inputs, <code>input</code>
	 * is sufficient.  For more complicated user inputs, use the SML in <code>results</code>.
	 * @param results Results containing the user input, including the SML, confidence vales, and
	 * NBest information.
	 * @return boolean indicating if the input is valid or not.
	 */
	boolean validateInput(String input, SRResults results);
	
	
	/** Do any side-effects that should be done when the flow finishes, such
	 * as updating a database.
	 * onComplete is where the business logic of an application goes.
	 * 
	 * @return boolean indicating whether the business logic has finished.  false means an asynchronous
	 * operation has been started.  The flow is now paused.  Later when the operation finishes,
	 * call SRInstance.Resume to continue.
	 */
	boolean onComplete();
	
	IFlowRenderer createRenderer();
	
	/** Callback that lets a flow object do any adjustments
	 * on prompt text.  For example, change "IBM" to "I.B.M" so 
	 * it will be spoken correctly by the TTS engine.
	 * When SpeakRight renders a prompt it walks the flow stack
	 * upward (from the current flow toward the app flow) calling
	 * fixupPrompt until a non-null value is returned.  
	 * @param item  the tts prompt item to be adjusted
	 * @return null means don't change. else return a new value for item
	 */
	String fixupPrompt(String item);
	
	/** Callback that lets a flow object do any adjustments
	 * on grammars.  An app may want to adjust the caching of
	 * its grammars.  This can be done in a single place by having
	 * the main application flow object override this method. 
	 * When SpeakRight renders a grammar it walks the flow stack
	 * upward (from the current flow toward the app flow) calling
	 * fixupGrammar until a non-null value is returned.  
	 * @param gram a grammar object
	 * @return null means don't change. else return a new value for item
	 */
	Grammar fixupGrammar(Grammar gram);
	
	
	/**
	 * used internally.
	 *
	 */
//	void processEarlyBindings(IExecutionContext context);
	
	/**
	 * The number of times this flow has been executed (in the current activation).
	 * Starts at 1.
	 * executionCount is not the same as the # of times a flow object has been executed
	 * during this phone call.  We don't track that (but it would be called activationCount
	 * if we did).
	 * @return
	 */
	int executionCount();

	/**
	 * Used internally by SRInstance.
	 * @return
	 */
	void setExecutionCount(int count);
	
	/**
	 * The prompt group prefix applies to prompt ids.  Prompt ids are used load prompt text from external XML files.
	 * Each prompt has a unique id. IFlow-derived classes often define prompt ids.  But we want instaces of these
	 * classes to be able to override prompts in an application-defined prompt XML file.  Prompt groups allow this.  
	 * The group prefix allows a two-step lookup; first the prefix is added to the prompt id and the prompt is looked up.  If not found
	 * then the original id is looked up.  
	 * <p>For example, consider a SROLogin class that defines a common login behaviour.  It has a prompt id
	 * "id:loginFailed" with a default prompt in its prompt XML file.  If we use SROLogin several times in our app, we may want to customize
	 * the loginFailed prompt each time.  Prompt groups allow this.  If our first login flow object uses a prompt group of "logon", then the
	 * two-stage lookup is: first lookup "id:logon.loginFailed", then "id:loginFailed".  Then in our application prompt XML file we'ld add a prompt
	 * for "login.loginFailed".
	 * Prompt groups are flexible because we can add to the application XML file at any time, without any code changes needed.
	 * @return the prompt group prefix.
	 */
	String promptGroup();
	
	/**
	 * Set the prompt group prefix.  The default prompt group prefix is the IFlow's name.
	 * @param groupPrefix  the prompt group prefix, or "*" which means uses the IFlow's name().
	 */
	void setPromptGroup(String groupPrefix);
	
	
	/**
	 * Return the next sub-flow after subFlow.
	 * Return null if subFlow is not one of our sub-flows, or there is no next one after it.
	 * This method is used to implement optional sub-flows (see BasicFlow)  
	 * @param subFlow
	 * @return null or next sub-flow after subFlow
	 */
	IFlow getSubFlowAfter(IFlow subFlow);
	
	
	/**
	 * Used for optional sub-flows.  A flow object can return false from this method if it
	 * wants to be skipped (that is, not execute).
	 * All the restrictions of optional-sub-flows apply, such as can't-be-last.
	 * @return true (default) to execute, false to not execute this flow object
	 */
	boolean shouldExecute();
}

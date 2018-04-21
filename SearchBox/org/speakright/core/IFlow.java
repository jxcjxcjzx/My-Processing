package org.speakright.core;
import java.io.Serializable;

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
	 * Returning null is not allowed.
	 * @return an IFlow object to be executed.
	 */
	IFlow getFirst();

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
	void onBegin();
	/**
	 * The last method called when a flow is executed.  Any cleanup code should be placed here.
	 * Called once after getNext returns null.  
	 *
	 */
	void onEnd();
	
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
	 * the flow object to be executed again.  The ExecutionCount field in ExecutionContext can
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
	 * @return the next flow to run, or null which means this flow object does not handle this
	 * event. 
	 */
	IFlow onCatch(IFlow current, SRResults results, String eventName);

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
	boolean ValidateInput(String input, SRResults results);
	
	
	/** Do any side-effects that should be done when the flow finishes, such
	 * as updating a database.
	 * DoTransaction is where the business logic of an application goes.
	 * 
	 * @return boolean indicating whether the transaction has finished.  false means an asynchronous
	 * transaction has been started.  The flow is now paused.  Later when the transaction finishes,
	 * call SRInstance.Resume to continue.
	 */
	
	boolean doTransaction();
	
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
	
	
	/**
	 * used internally.
	 *
	 */
	void processEarlyBindings(IExecutionContext context);

}

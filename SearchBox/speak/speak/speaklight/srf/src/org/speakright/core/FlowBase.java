/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import org.speakright.core.render.Grammar;
import org.speakright.core.render.IFlowRenderer;
import org.speakright.core.render.ISpeechForm;
import org.speakright.core.render.ISpeechPage;
import org.speakright.core.render.Prompt;
import java.util.*;

/**
 * FlowBase is an adapter for IFlow; it implements all the IFlow interface methods.
 * Most flow objects derive from FlowBase so that they need only to override a few of the
 * IFlow methods.
 * <p>
 * The default behaviour that FlowBase implements is a do-nothing flow object with no
 * sub-flows.  It returns itself in getFirst, and null in getNext, and execute does
 * nothing.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class FlowBase implements IFlow {
	String m_name = "";
	//early binding stuff moved to grammar
	
	transient SRLogger m_logger = null; //created on-demand
	int m_executionCount;
	String m_promptGroup = "*";
	
	public FlowBase()
	{
		m_name = this.getClass().getSimpleName();
	}
	
	/**
	 * Create a flow with the given name.  Names are
	 * used for logging only, but it helps if they're unique.
	 * @param name
	 */
	public FlowBase(String name)
	{
		m_name = name;
	}
	
	public String name()
	{
		return m_name;
	}

	/**
	 * Log to the SpeakRight logger (log4j)
	 * @param message
	 */
	public void log(String message)
	{
		if (m_logger == null) {
			m_logger = SRLogger.createLogger();
		}
		m_logger.log(message);
	}
	
	/**
	 * Log an error to the SpeakRight logger (log4j).
	 * Will be logged at 'error' level.
	 * @param message
	 */
	public void logError(String message)
	{
		if (m_logger == null) {
			m_logger = SRLogger.createLogger();
		}
		m_logger.logError(message);
	}
	
	/**
	 * Sets the name of the flow object.
	 * @see IFlow#name
	 * @param name
	 */
	public void setName(String name)
	{
		m_name = name;
	}

	
	/**
	 * The default implementation of execute is to render this object.
	 * However the default rendering is a 'silent' voicexml page.
	 */
	public void execute(IExecutionContext context)
	{
		context.render(this);		
	}
	/**
	 * The default implementation of getFirst is to return this. 
	 */
	public IFlow getFirst(IFlowContext context)
	{
		return this;
	}
	
	/**
	 * The default implementation of getNext is to return null.
	 */
	public IFlow getNext(IFlow current, SRResults results)
	{
		return null;
	}
	
	/**
	 * The default implementation of onBegin is to do nothing.
	 */
	public void onBegin(IFlowContext context)
	{
		
	}
	/**
	 * The default implementation of onEnd is to do nothing.
	 * 
	 */
	public void onEnd(IFlowContext context)
	{
		
	}
	
	/**
	 * The default implementation of onDisconnect is to return null,
	 * indicating that this object does not handle disconnect events.
	 */
	public IFlow onDisconnect(IFlow current, SRResults results)
	{
		return null;
	}
	/**
	 * The default implementation of onNoInput is to return null,
	 * indicating that this object does not handle no-input events.
	 */
	public IFlow onNoInput(IFlow current, SRResults results)
	{
		return null;
	}
	/**
	 * The default implementation of onCatch is to return null,
	 * indicating that this object does not handle custom events.
	 */
	public IFlow onCatch(IFlow current, SRResults results, String eventName, ThrowEvent event)
	{
		return null;
	}

	
	/**
	 * The default implementation of onCatch is to return null,
	 * indicating that this object does not handle platform errors.
	 */
	public IFlow onPlatformError(IFlow current, SRResults results) {
		return null;
	}

	/**
	 * The default implementation of onCatch is to return null,
	 * indicating that this object does not handle transfer failures.
	 */
	public IFlow onTransferFailed(IFlow current, SRResults results) {
		return null;
	}

	/**
	 * The default implementation of onValidateFailed is to return null,
	 * indicating that this object does not handle validate-failed events.
	 */
	public IFlow onValidateFailed(IFlow current, SRResults results)
	{
		return null;
	}
	
	/**
	 * The default implementation is to return true.
	 */
	public boolean validateInput(String input, SRResults results)
	{
		return true;
	}


	/**
	 * The default implementation is to return true;
	 */
	public boolean onComplete()
	{
		return true;
	}
	
	public IFlowRenderer createRenderer()
	{
		return new FlowBaseRenderer();
	}
	
	/**
	 * an renderer that generates minimal content. we currently require every execute to 
	 * generate some content.  So we'll generate a prompt of silence...
	 * @author Ian Rae
	 *
	 */
	private class FlowBaseRenderer implements IFlowRenderer
	{
		public void Render(ISpeechPage page, ISpeechForm form)
		{
			form.addField(new Prompt(" ")); //silent output
		}
	}

	/**
	 * The default implementation is to do nothing.
	 * @return null
	 */
	public String fixupPrompt(String item)
	{
		return null;
	}
	
	/**
	 * The default implementation is to do nothing.
	 * @return null
	 */
	public Grammar fixupGrammar(Grammar gram)
	{
		return null;
	}
	
	
	/**
	 * The number of times this flow has been executed (in the current activation).
	 * Starts at 1.
	 * executionCount is not the same as the # of times a flow object has been executed
	 * during this phone call.  We don't track that (but it would be called activationCount
	 * if we did).
	 * @return
	 */
	public int executionCount()
	{
		return m_executionCount;
	}

	/**
	 * Used internally by SRInstance.
	 * @return
	 */
	public void setExecutionCount(int count)
	{
		m_executionCount = count;
	}

	public String promptGroup() {
		return (m_promptGroup == "*") ? name() : m_promptGroup;
	}

	public void setPromptGroup(String groupPrefix) {
		m_promptGroup = groupPrefix;
	}

	public IFlow getSubFlowAfter(IFlow subFlow) {
		return null; //default behaviour is not to have any sub-flows
	}

	public boolean shouldExecute() {
		return true; //default behaviour is to always execute
	}
}

/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

import org.speakright.core.render.Grammar;
import org.speakright.core.render.IFlowRenderer;

/**
 * A wrapper flow object that does nothing except remember which of its
 * methods were called.  Uses a Trail object to build a string.
 * 
 * Used for unit tests only.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class TrailWrapper implements IFlow {
	
	public TrailWrapper(IFlow flow)
	{
		m_flow = flow;
	}
	IFlow m_flow; //the wrapped flow we're tracking the flow of
	public Trail m_trail = new Trail();
	
	public IFlow InnerFlow()
	{
		return m_flow; //needed for model binding
	}
	
	public String name()
	{
		return m_flow.name();
	}
	public void execute(IExecutionContext context)
	{
		m_trail.add("E");		
		m_flow.execute(context);	
	}
	public IFlow getFirst(IFlowContext context)
	{
		m_trail.add("F");		
		IFlow flow = m_flow.getFirst(context);
		//hide m_flow from the runtime by returning this
		return (flow == m_flow) ? this : flow;
	}
	public IFlow getNext(IFlow current, SRResults results)
	{
		m_trail.add("N");		
		IFlow flow = m_flow.getNext(current, results);
		//hide m_flow from the runtime by returning this
		return (flow == m_flow) ? this : flow;
	}
	
	public void onBegin(IFlowContext context)
	{
		m_trail.add("beg");		
		m_flow.onBegin(context);
	}
	public void onEnd(IFlowContext context)
	{
		m_trail.add("end");		
		m_flow.onEnd(context);
	}

	public IFlow onDisconnect(IFlow current, SRResults results)
	{
		IFlow flow = m_flow.onDisconnect(current, results);
		if (flow != null) {
			m_trail.add("DISC");
		}
		return flow;
	}
	public IFlow onNoInput(IFlow current, SRResults results)
	{
		IFlow flow = m_flow.onNoInput(current, results);
		if (flow != null) {
			m_trail.add("NOINPUT");		
		}
		return flow;
	}
	public IFlow onCatch(IFlow current, SRResults results, String eventName, ThrowEvent event)
	{
		IFlow flow = m_flow.onCatch(current, results, eventName, event);
		if (flow != null) {
			m_trail.add("CATCH");		
		}
		return flow;
	}
	
	public IFlow onPlatformError(IFlow current, SRResults results) {
		IFlow flow = m_flow.onPlatformError(current, results);
		if (flow != null) {
			m_trail.add("PLATFORM_ERROR");		
		}
		return flow;
	}
	public IFlow onTransferFailed(IFlow current, SRResults results) {
		IFlow flow = m_flow.onTransferFailed(current, results);
		if (flow != null) {
			m_trail.add("TRANSFER_FAILED");		
		}
		return flow;
	}
	public IFlow onValidateFailed(IFlow current, SRResults results)
	{
		IFlow flow = m_flow.onValidateFailed(current, results);
		if (flow != null) {
			m_trail.add("VALFAIL");		
		}
		return flow;
	}

	
	public boolean validateInput(String input, SRResults results)
	{
		return m_flow.validateInput(input, results);
	}
	
	public boolean onComplete()
	{
		m_trail.add("T");	//T because used to be called doTransaction	
		return m_flow.onComplete();
	}
	
	public IFlowRenderer createRenderer()
	{
		return m_flow.createRenderer();
	}
	
	public String fixupPrompt(String item)
	{
		return m_flow.fixupPrompt(item);
	}
	public Grammar fixupGrammar(Grammar gram)
	{
		return m_flow.fixupGrammar(gram);
	}
//	public void processEarlyBindings(IExecutionContext context)
//	{
//		m_flow.processEarlyBindings(context);
//	}
	
	public int executionCount()
	{
		return m_flow.executionCount();
	}
	public void setExecutionCount(int count)
	{
		m_flow.setExecutionCount(count);
	}

	public String promptGroup() {
		return m_flow.promptGroup();
	}

	public void setPromptGroup(String groupPrefix) {
		m_flow.setPromptGroup(groupPrefix);
	}

	public IFlow getSubFlowAfter(IFlow subFlow) {
		return m_flow.getSubFlowAfter(subFlow);
	}
	
	public boolean shouldExecute() {
		return m_flow.shouldExecute();
	}
}

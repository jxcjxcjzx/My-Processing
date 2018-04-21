/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.speakright.core.ExitEvent;
import org.speakright.core.IExecutionContext;
import org.speakright.core.IFlow;
import org.speakright.core.IFlowContext;
import org.speakright.core.SRResults;
import org.speakright.core.ThrowEvent;
import org.speakright.core.flows.BasicFlow;

@SuppressWarnings("serial")
public class MyFlow extends BasicFlow {

	public Model M;

	public MyFlow(String name)
	{
		super();
		setName(name);
	}
	public boolean m_shouldExecute = true;
	public boolean m_exitInGetNext = false;
	public boolean m_handleNoInput = false;
	public boolean m_throwInGetNext = false;
	public boolean m_throwInExecute = false;
	public boolean m_addBinding = false;
	public boolean m_addBindingX = false;
	
	@Override
	public boolean shouldExecute() {
		if (! m_shouldExecute) {
			this.log("skipping one!");
		}
		return m_shouldExecute;
	}

	@Override
	public void execute(IExecutionContext context)
	{
		if (m_throwInExecute) {
			context.throwEvent(new ThrowEvent());
		}
			
		if (m_addBinding) {
			//binding
			M.ModelBinder().addBinding(this, "slot1", "City");	
		}
		else if (m_addBindingX) {
			M.ModelBinder().addBinding(this, "slot1", "X");			
		}
		context.render(this);
	}
	
	@Override
	public IFlow getFirst(IFlowContext context)
	{
		return this;
	}
	@Override
	public IFlow getNext(IFlow current, SRResults results)
	{
		if (m_exitInGetNext) {
			return new ExitEvent();
		}
		else if (m_throwInGetNext) {
			return new ThrowEvent();
		}
			
		return null;
	}
	
	@Override
	public void onBegin(IFlowContext context)
	{
	}
	@Override
	public void onEnd(IFlowContext context)
	{
	}
	
	@Override
	public IFlow onNoInput(IFlow current, SRResults results)
	{
		return (m_handleNoInput) ? new ExitEvent() : null;
	}
	
	public void addSubFlow(IFlow flow)
	{
		add(flow);		
	}
}

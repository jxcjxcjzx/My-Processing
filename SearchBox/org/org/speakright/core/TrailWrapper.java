package org.speakright.core;

import org.speakright.core.render.IFlowRenderer;

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
	public IFlow getFirst()
	{
		m_trail.add("F");		
		IFlow flow = m_flow.getFirst();
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
	
	public void onBegin()
	{
		m_trail.add("beg");		
		m_flow.onBegin();
	}
	public void onEnd()
	{
		m_trail.add("end");		
		m_flow.onEnd();
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
	public IFlow onCatch(IFlow current, SRResults results, String eventName)
	{
		IFlow flow = m_flow.onCatch(current, results, eventName);
		if (flow != null) {
			m_trail.add("CATCH");		
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

	
	public boolean ValidateInput(String input, SRResults results)
	{
		return m_flow.ValidateInput(input, results);
	}
	
	public boolean doTransaction()
	{
		m_trail.add("T");		
		return m_flow.doTransaction();
	}
	
	public IFlowRenderer createRenderer()
	{
		return m_flow.createRenderer();
	}
	
	public String fixupPrompt(String item)
	{
		return null;
	}
	public void processEarlyBindings(IExecutionContext context)
	{
		m_flow.processEarlyBindings(context);
	}
	
}

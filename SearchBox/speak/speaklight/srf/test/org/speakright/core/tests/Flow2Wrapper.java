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
public class Flow2Wrapper extends BasicFlow {
	public Flow2Wrapper(String name, IFlow flow1, IFlow flow2)
	{
		super();
		setName(name);
		m_flow1 = flow1;
		m_flow2 = flow2;
	}
	public IFlow m_flow1;
	public IFlow m_flow2;

	@Override
	public IFlow getFirst(IFlowContext context) {
		return m_flow1;
	}

	@Override
	public IFlow getNext(IFlow current, SRResults results) {
		if (current == m_flow1) return m_flow2;
		return null;
	}

	@Override
	public IFlow onDisconnect(IFlow current, SRResults results)
	{
		return new ExitEvent();
	}
	@Override
	public IFlow onCatch(IFlow current, SRResults results, String eventName, ThrowEvent event)
	{
		//return new ExitFlowEvent();
		//catch all and ignore, and continue
		IFlow flow = getNext(current, results);
		if (flow == null) {
			flow = new ExitEvent();
		}
		return flow;
	}

	@Override
	public IFlow onValidateFailed(IFlow current, SRResults results)
	{
		return new ExitEvent();
	}
}

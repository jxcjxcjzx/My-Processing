/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.flows;
import java.util.ArrayList;

import org.speakright.core.FlowBase;
import org.speakright.core.IFlow;
import org.speakright.core.IFlowContext;
import org.speakright.core.SRResults;

/**
 * BasicFlow is a base class for most flow objects.  It
 * supports sub-flows which you add using the <code>add</code> method.
 * Sub-flows can be optional; if their shouldExeucte returns false, they are skipped
 * and the next sub-flow is run.  It's an error if all the sub-flows return false from their
 * shouldExecute method.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class BasicFlow extends FlowBase {
	protected ArrayList<IFlow> m_L = new ArrayList<IFlow>();

	public BasicFlow()
	{}
	
//	public BasicFlow(String name)
//	{
//		super(name);
//	}

	/**
	 * get the number of sub-flows of this flow.
	 */
	public int SubFlowCount()
	{
		return m_L.size();
	}
	
	/**
	 * add a sub-flow.  Sub-flows will be executed in
	 * the order they were added.
	 * @param flow
	 */
	public void add(IFlow flow)
	{
		m_L.add(flow);
	}
	
	/**
	 * Adds a prompt flow object.  Equivalent
	 * to add(new PromptFlow("some text"))
	 * @param ptext prompt text 
	 */
	public void addPromptFlow(String ptext)
	{
		add(new PromptFlow(ptext));
	}
	
	/**
	 * If there are sub-flows then return the first one, otherwise
	 * return self.
	 * BasicFlow supports optional sub-flows.  An optional sub-flow can
	 * return null from it's getFirst method to indicate that it doesn't wish
	 * to run.
	 * BasicFlow must still adhere to the rule that SRRunner requires getFirst never
	 * return null; therefore at least one sub-flow's getFirst must return non-null.
	 * 
	 * Detail: getFirst method of sub-flows may be called more than once.
	 */
	@Override public IFlow getFirst(IFlowContext context)
	{
		if (m_L.size() == 0)
		{
			return super.getFirst(context);
		}
		else {
			return m_L.get(0);
			//returning null will cause an error! It means all sub-flows returned null which is not allowed
			//at least one must return non-null
		}
	}
	
//	/**
//	 * Handles optional sub-flows by walking the list
//	 * until a flow is found who's getFirst is non-null.
//	 */
//	IFlow getNextFlow(int startIndex)
//	{
//		IFlow flow = m_L.get(startIndex);
//		return flow;
////		int n = m_L.size();
////		for(int i = startIndex; i < n; i++) {
////			IFlow flow = m_L.get(i);
////			if (flow.getFirst() != null) {
////				return flow;
////			}
////			log("skipping optional sub-flow " + flow.name());
//////			return flow;
////		}
////		return null; //this may cause an error (depending whether we're being called by getFirst or getNext)
//	}
	
	
	
	@Override
	public IFlow getSubFlowAfter(IFlow subFlow) 
	{
		if (m_L.size() == 0)
		{
			return null;
		}
		else {
			int n = m_L.size();
			int i = this.findSubFlow(subFlow);
			if (i >= 0) {
				if (i < (n - 1)) {
					return m_L.get(i+1);
				}		
			}
			return null;
		}
	}
	
	/**
	 * Get the index of subFlow, if its one of our sub-flows
	 * @param subFlow a possible sub-flow object.
	 * @return -1 if not one of our sub-flows, otherwise it's index
	 */
	protected int findSubFlow(IFlow subFlow) 
	{
		if (m_L.size() == 0)
		{
			return -1;
		}
		else {
			int n = m_L.size();
			for(int i = 0; i < n; i++) {
				IFlow flow = m_L.get(i);
				if (subFlow == flow) {
					return i;
				}
			}
			return -1;
		}
	}
	

	/**
	 * get the next flow. If there are sub-flows then return the
	 * next sub-flow.
	 */
	@Override public IFlow getNext(IFlow current, SRResults results)
	{
		if (m_L.size() == 0)
		{
			return super.getNext(current, results);
		}
		else {
			int n = m_L.size();
			int i = this.findSubFlow(current);
			if (i >= 0) {
				//the runtime only calls here when a sub-flow has
				//ended, so we can simply go on to the next one.
				if (i < (n - 1)) {
					return m_L.get(i+1);
				}		
			}
			return null;
		}
	}	
}

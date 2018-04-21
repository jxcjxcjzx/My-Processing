/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.flows;

import org.speakright.core.IFlow;
import org.speakright.core.IFlowContext;
import org.speakright.core.SRResults;

/**
 * Represents looping behaviour.  The given set of sub-flow objects
 * will be executed <code>numIterations</code> times.
 * 
 * Callflows often repeat things several times (three login attempts then fail),
 * or infinitely (such as a main menu).
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class LoopFlow extends BasicFlow {
	int m_numIterations = Integer.MAX_VALUE;
	int m_count = 0;
	int m_n = -1;

	public static final int FOREVER = Integer.MAX_VALUE;
	
	/**
	 * Create a loop that loops forever (unless you
	 * change this using <code>setNumIterations</code>
	 *
	 */
	public LoopFlow()
	{
		super();
	}
	/**
	 * Create a loop that iterates the given number
	 * of times.
	 * @param numIterations
	 */
	public LoopFlow(int numIterations)
	{
		super();
		m_numIterations = numIterations;
	}
	
	/**
	 * Get the number of times this loop will iterate.
	 * @return
	 */
	public int numIterations()
	{
		return m_numIterations;
	}
	/**
	 * Set (or change) the number of iterations.
	 * Can be called at any time, even after the loop has
	 * started.
	 * @param numIterations
	 */
	public void setNumIterations(int numIterations)
	{
		m_numIterations = numIterations;
		initN(); 
	}
	void initN()
	{
		if (m_n >= 0) { //already set
			return;
		}
		if (m_numIterations == FOREVER) {
			m_n = FOREVER;
		}
		else {
			m_n = m_numIterations * SubFlowCount(); 
		}
//		log("m_N " + m_n);
	}
	
	@Override
	public void add(IFlow flow) {
		super.add(flow);
//		initN(); 
	}
	@Override
	public IFlow getFirst(IFlowContext context) {
		initN(); 
		return super.getFirst(context);
	}
	
	@Override
	public IFlow getSubFlowAfter(IFlow subFlow) {
		IFlow flow = super.getSubFlowAfter(subFlow);
		if (flow != null) { //are skipping a sub-flow?
			m_n--;
//			log("n reduced to " + m_n);
		}
		else {
			int index = findSubFlow(subFlow);
			if (index >= 0) {
				//it's one of our subflows
				if (index == m_L.size() - 1) {
//					log("gsfa wrapping!");
					m_n--;
//					log("n reduced to " + m_n);
					if (m_count >= m_n) {
						this.log(String.format("oops. loop %d/%d finished", m_count, m_n));
						return null;
					}
					return m_L.get(0); //wrap-around
				}
			}
		}
		return flow;
	} 
	/**
	 * Get the next sub-flow.  If we're at the last sub-flow then iterate again by
	 * returning the first sub-flow.  If we're at the last sub-flow and have done
	 * the required number of iterations then return null.
	 */
	@Override public IFlow getNext(IFlow current, SRResults results)
	{
		m_count++;
		if (m_count >= m_n) {
			this.log(String.format("loop %d/%d finished", m_count, m_n));
			return null;
		}
		else {
			IFlow flow = super.getNext(current, results);
			if (flow == null) {
				this.log(String.format("loop %d/%d again", m_count, m_n));
				return getFirst(results); //loop again
			}
			return flow;
		}
	}
}

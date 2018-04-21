package org.speakright.core;
import java.util.ArrayList;

/**
 * BasicFlow is a base class for most flow objects.  It
 * supports sub-flows which you add using the <code>add</code> method.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class BasicFlow extends FlowBase {
	ArrayList<IFlow> m_L = new ArrayList<IFlow>();

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
	 * If there are sub-flows then return the first one, otherwise
	 * return self.
	 */
	@Override public IFlow getFirst()
	{
		if (m_L.size() == 0)
		{
			return super.getFirst();
		}
		else {
			IFlow flow = m_L.get(0);
			return flow;		
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
			for(int i = 0; i < n; i++) {
				IFlow flow = m_L.get(i);
				if (current == flow) {
					//the runtime only calls here when a sub-flow has
					//ended, so we can simply go on to the next one.
					if (i < (n - 1)) {
						IFlow next = m_L.get(i+1);
						return next;
					}		
				}
			}
			return null;
		}
	}	
}

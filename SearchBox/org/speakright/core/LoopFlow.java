package org.speakright.core;

@SuppressWarnings("serial")
public class LoopFlow extends BasicFlow {
	int m_numIterations = Integer.MAX_VALUE;
	int m_count = 0;
	int m_n;

	public static final int FOREVER = Integer.MAX_VALUE;
	
	public LoopFlow()
	{
		super();
	}
	public LoopFlow(int numIterations)
	{
		super();
		m_numIterations = numIterations;
	}
	
	public int numIterations()
	{
		return m_numIterations;
	}
	public void setNumIterations(int numIterations)
	{
		m_numIterations = numIterations;
		initN(); 
	}
	void initN()
	{
		if (m_numIterations == FOREVER) {
			m_n = FOREVER;
		}
		else {
			m_n = m_numIterations * SubFlowCount(); 
		}
	}
	
	@Override
	public void add(IFlow flow) {
		super.add(flow);
		initN(); 
	}
	@Override
	public IFlow getFirst() {
		initN(); 
		return super.getFirst();
	}
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
				return getFirst(); //loop again
			}
			return flow;
		}
	}
}

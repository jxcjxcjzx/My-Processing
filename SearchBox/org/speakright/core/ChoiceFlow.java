package org.speakright.core;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ChoiceFlow provides branching based on user input, such as in a menu.
 * ChoiceFlow contains a menu question, and some choices.  Each choice
 * is a user input string and the IFlow to execute when that user input
 * occurs.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class ChoiceFlow extends BasicFlow {
	ChoiceMap m_map = new ChoiceMap();
	IFlow m_choiceQuestion;

	public ChoiceFlow(String name) {
		super();
		setName(name);
	}
	
	/**
	 * Sets the flow object that will ask the choice question.
	 * This MUST be called before this flow object is executed.
	 * @param flow the flow object that will ask the choice question.
	 */
	protected void setChoiceQuestion(IFlow flow)
	{
		m_choiceQuestion = flow;
	}

	/**
	 * Adds a choice.  
	 * @param choice  the user input string for this choice.
	 * @param flow  IFlow to be executed when the user input equals <code>choice</code>
	 */
	protected void addChoice(String choice, IFlow flow)
	{
		m_map.add(choice, flow);
	}

	/**
	 * Returns the choice question.
	 */
	@Override public IFlow getFirst()
	{
		return m_choiceQuestion;
	}
	
	/**
	 * Returns the flow for the choice that matches the user input.
	 */
	@Override public IFlow getNext(IFlow current, SRResults results)
	{
		IFlow flow = m_map.find(results.m_input);
		if (flow != null)  {
			return flow;
		}
		return m_choiceQuestion;
	}
	
	class ChoiceMap implements Serializable
	{
		Map<String,IFlow> m_map = new HashMap<String,IFlow>();
				
		public void add(String choice, IFlow flow)
		{
			m_map.put(choice, flow);
		}
		
		public IFlow find(String choice)
		{
			return m_map.get(choice);
		}
	}
}

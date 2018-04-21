package org.speakright.sro;

import java.util.ArrayList;

import org.speakright.core.IExecutionContext;
import org.speakright.core.IFlow;
import org.speakright.core.IItemReusableFormatter;
import org.speakright.core.IModelItem;
import org.speakright.core.SRResults;
import org.speakright.core.render.PromptType;
import org.speakright.sro.gen.genSROListNavigator;

@SuppressWarnings("serial")
public class SROListNavigator extends genSROListNavigator {
	protected ArrayList<IModelItem> m_L;
	protected int m_numItems;
	protected int m_currentIndex; //starts at 0
	transient protected boolean m_wentPastEnd;
	transient protected boolean m_wentPastBeginning;
	transient IModelItem m_currentItem; //can be null
	IItemReusableFormatter m_customFormatter; //can be null. null means use the IModelItems' built-in formatter.

	//only used in execute, can be null
	transient SROOrdinalItem m_ordinal;	
	
	public SROListNavigator(String subject)
	{
		this(subject, new ArrayList<String>());
	}
	public SROListNavigator(String subject, ArrayList<String> L)
	{			
		super(subject);
		initList(L);
		addGrammar(m_navigateGrammar);
	}
	public SROListNavigator(String subject, ArrayList<IModelItem> L, int junk)
	{			
		super(subject);
		m_L = L;
		m_numItems = m_L.size();
		m_currentIndex = 0;
	}
	
	protected void initList(ArrayList<String> L)
	{
		m_L = new ArrayList<IModelItem>();
		for(String s : L) {
			SROStringItem item = new SROStringItem(s);
			m_L.add(item);
		}
		m_numItems = m_L.size();
		m_currentIndex = 0;		
	}
	
	/**
	 * Set a formatter that will be used for format the items in the list.
	 * @param formatter a reusable formatter.  This single formatter object will be used on all
	 * the items in the list.
	 */
	public void setItemFormatter(IItemReusableFormatter formatter)
	{
		m_customFormatter = formatter;
	}
	
	
	@Override
	public void initPrompts(IExecutionContext context)
	{
		super.initPrompts(context);
		//load prompt set using our prompt fields 
		initPrompt(PromptType.MAIN1, (context.ValidateFailed()) ? m_outOfRangePrompt : m_main1Prompt);
	}
	
	@Override
	public IFlow getNext(IFlow current, SRResults results) 
	{
		IFlow flow = doGetNext(current, results);
		log("index: " + m_currentIndex);		
		return flow;
	}
	
	IFlow doGetNext(IFlow current, SRResults results) {
		Command cmd = getCommand(results);
		log("cmd: " + cmd + ", index: " + m_currentIndex);
		m_wentPastEnd = false;
		m_wentPastBeginning = false;
		
		IFlow next = null;
		switch(cmd) {
		case NONE:
			break;
		case REPEAT:
			return this;
		case NEXT:
			if (m_currentIndex < (m_L.size() - 1)) {
				m_currentIndex++;
				return this;
			}
			else {
				m_wentPastEnd = true;
				return this;
			}
//			break;
		case PREVIOUS:
			if (m_currentIndex > 0) {
				m_currentIndex--;
				return this;
			}
			else {
				m_wentPastBeginning = true;
				return this;
			}
//			break;
			
		case FIRST:
			m_currentIndex = 0;
			return this;
		case LAST:
			m_currentIndex = m_L.size() - 1;
			return this;
		}
		// TODO Auto-generated method stub
		return super.getNext(current, results);
	}
	
	public enum Command {
		NONE,
		NEXT,
		PREVIOUS,
		REPEAT,
		FIRST,
		LAST
	}
	
	Command getCommand(SRResults results)
	{
		Command cmd = Command.NONE;
		if (results.m_input.equals("next")) cmd = Command.NEXT;
		else if (results.m_input.equals("prev")) cmd = Command.PREVIOUS;
		else if (results.m_input.equals("repeat")) cmd = Command.REPEAT;
		else if (results.m_input.equals("first")) cmd = Command.FIRST;
		else if (results.m_input.equals("last")) cmd = Command.LAST;
		
		return cmd;
	}

	@Override
	public void execute(IExecutionContext context) 
	{
		m_currentItem = null; //clear
		m_ordinal = new SROOrdinalItem(m_currentIndex + 1, context);
		
		//there are two prefix prompts, for singular or plural. disable the one we don't need
		if (m_numItems == 0) {
			m_numberOfItemsInListSubPrompt.setConditionCustom(false);
			m_numberOfItemsInListSingularSubPrompt.setConditionCustom(false);
		}
		else if (m_numItems == 1) {
			m_numberOfItemsInListSubPrompt.setConditionCustom(false);
		}
		else {
			m_numberOfItemsInListSingularSubPrompt.setConditionCustom(false);
		}
		setSubjectPlurality(m_numItems, context);
		
		if (m_wentPastEnd) {
			m_main1Prompt = m_cantGoNextPrompt; //"There are no more items";
		}
		else if (m_wentPastBeginning) {
			m_main1Prompt = m_cantGoPreviousPrompt; //"There are no previous items";
		}
//		else if (m_currentIndex == 0 && executionCount() == 1) {
//			m_main1Prompt = m_numberOfItemsInListPrompt + getItem(m_currentIndex); //"How many {%subject%} would you like?";
//		}
		else {
			m_main1Prompt = getItem(m_currentIndex);
		}
		super.execute(context);
	}
	
	String getItem(int index)
	{
		if (m_L.size() == 0) {
			return m_emptyListPrompt;
		}
		m_currentItem = m_L.get(index);
		if (m_customFormatter != null) {
			m_customFormatter.setItem(m_currentItem);
			m_currentItem.setFormatter(m_customFormatter);
		}
//		return "{%ordinal%} item: {%currentItem%}";
		return "id:sayCurrentItem";
	}
}

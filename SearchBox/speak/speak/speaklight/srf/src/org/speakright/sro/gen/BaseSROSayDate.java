package org.speakright.sro.gen;
import java.util.*;
import java.text.*;
import org.speakright.core.*;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.render.*;
import org.speakright.sro.*;

public class BaseSROSayDate extends PromptFlow {

	protected String m_subject = "date";
	
	protected String m_prefixPrompt = "The {%subject%} is";
	protected String m_mainPrompt = "{%prefixPrompt%}{%date%}";
	
	public enum Prompts
	{
		prefixPROMPT,
		mainPROMPT
	}
	public String getPrompt(Prompts p)
	{
		switch(p) {
		case prefixPROMPT: return m_prefixPrompt;
		case mainPROMPT: return m_mainPrompt;
		}
		return "";
	}
	public void setPrompt(Prompts p, String text)
	{
		switch(p) {
		case prefixPROMPT: 
			m_prefixPrompt = text;
			break;
		case mainPROMPT: 
			m_mainPrompt = text;
			break;
		}
	}
	
	private IModelItem m_item;
	
	public BaseSROSayDate(Date dt)
	{
		m_item = new SRODateItem(dt);
	}

	@Override
	public void execute(IExecutionContext context) {
		//context.registerPromptsFile("org.speakright.sro", "SROSayDate_prompts.xml");
		
//%date% will do this:		String text = m_item.getFormatter().format();
		Prompt prompt = firstPrompt();
		prompt.setPText(m_mainPrompt);
		
		super.execute(context);
	}
	
}

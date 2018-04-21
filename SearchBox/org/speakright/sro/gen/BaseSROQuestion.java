package org.speakright.sro.gen;


import org.speakright.core.ConfirmationWrapper;
import org.speakright.core.IExecutionContext;
import org.speakright.core.IModelItem;
import org.speakright.core.IConfirmationFlow;
import org.speakright.core.IFlow;
import org.speakright.core.QuestionFlow;
import org.speakright.core.SRResults;
import org.speakright.core.render.Prompt;
import org.speakright.core.render.Grammar;

/**
 * A generic question SRO, with flex-prompts and
 * optional confirmation.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class BaseSROQuestion extends QuestionFlow {
	protected IFlow m_flowToRun;
	protected String m_subject = "item";
	
	protected String m_prefixPrompt = "What ";
	protected String m_mainPrompt = "{%prefixPrompt%}{%subject%}?";
	
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
	
	
	public BaseSROQuestion(String gramUrl, String subject)
	{
		m_flowToRun = this;
		m_subject = subject;
		m_quest.m_gram = new Grammar(gramUrl);
		m_quest.m_promptSet.add(new Prompt(m_mainPrompt));
	}

	public void setConfirmer(IConfirmationFlow flow)
	{
		ConfirmationWrapper cw = new ConfirmationWrapper(this, flow);
		m_flowToRun = cw;
	}
	@Override
	public IFlow getFirst() {
		return m_flowToRun.getFirst();
	}
	@Override
	public IFlow getNext(IFlow current, SRResults results) {
		return m_flowToRun.getNext(current, results);
	}
}

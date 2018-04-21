package org.speakright.core.render;

import java.io.Serializable;

/**
 * Represents a single-slot single question, which basically maps to
 * a VoiceXML field tag.
 * 
 * A question consists of prompts and grammars.
 * 
 * This class implements a single-slot single question.  This
 * is the simplest form of directed dialog VUI.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class Question extends FormElement implements Serializable {

	public PromptSet m_promptSet = new PromptSet();
	public Grammar m_gram;
	
	/**
	 * # of times to try before giving up with no-input error
	 * 1 means try once, 2 means try + 1 retry, ...
	 * This value is one more than "max retries"
	 */
	public int m_maxAttempts = 3;
	
	public Question() {
		InitPrompts("item");
		m_gram = new Grammar();
	}
	public Question(Grammar gram, Prompt prompt)
	{
		InitPrompts("item");
		m_promptSet.add(prompt); //replace
		m_gram = gram;
	}
	
	public Grammar grammar()
	{
		return m_gram;
	}
	public void setMaxAttempts(int n)
	{
		m_maxAttempts = n;
	}
	
	public void InitPrompts(String subject)
	{
		add(PromptType.MAIN1, String.format("What %s do you want?", subject));
		add(PromptType.SILENCE1, String.format("I'm sorry I didn't hear anything. What %s would you like?", subject));
		add(PromptType.SILENCE2, String.format("I still didn't hear anything.  Please say an %s.", subject));
		add(PromptType.NORECO1, String.format("I didn't get that. What %s would you like?", subject));
		add(PromptType.NORECO2, String.format("I still didn't understand.  Please say an %s you would like?", subject));
	}
	void add(PromptType type, String text)
	{
		m_promptSet.add(new Prompt(type, text));
	}
	public void renderPrompts(PromptPipeline pipeline)
	{
		m_promptSet.renderPrompts(pipeline);
	}
	@Override
	public void renderGrammars(GrammarPipeline pipeline)
	{
		pipeline.render(m_gram);
	}
	
}

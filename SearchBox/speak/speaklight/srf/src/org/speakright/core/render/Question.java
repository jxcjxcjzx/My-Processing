/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

import java.io.Serializable;
import java.util.*;
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
//	public Grammar m_gram;
//	ArrayList<Grammar> m_gramL = new ArrayList<Grammar>();
	public GrammarSet m_grammarSet = new GrammarSet();
	
	/**
	 * # of times to try before giving up with no-input error
	 * 1 means try once, 2 means try + 1 retry, ...
	 * This value is one more than "max retries"
	 */
	public int m_maxAttempts = 3;
	
	public Question() {
		InitPrompts("item");
		m_grammarSet.add(new Grammar());
	}
	public Question(Grammar gram, Prompt prompt)
	{
		InitPrompts("item");
		m_promptSet.add(prompt); //replace
		m_grammarSet.add(gram);
	}

	/**
	 * Get the voice grammar.  Will return null in the
	 * rare cases that a question doesn't have a voice grammar.
	 * @return
	 */
	public Grammar grammar()
	{
		return m_grammarSet.voiceGrammar();
	}
	
	/**
	 * adds (or replaces if gram.type already in the grammar set)
	 * @param gram
	 */
	public void addGrammar(Grammar gram)
	{
		m_grammarSet.add(gram);
	}

	/*
	 * Set the max # of attemps the question will do.  This is
	 * 1 more than max-retries.  So a value of 2, for example,
	 * means ask the question and if a silence/noreco error then
	 * ask once more, then give up with NoInput result.
	 */
	public void setMaxAttempts(int n)
	{
		m_maxAttempts = n;
	}
	
	/**
	 * Init the error and escalating prompts to something
	 * reasonable, using m_subject.
	 * Apps are free to then change or replace the prompts with
	 * more suitable prompts themselves.
	 * @param subject the subject of the question, such as "tickets".
	 */
	public void InitPrompts(String subject)
	{
		add(PromptType.MAIN1, String.format("What %s do you want?", subject));
		add(PromptType.SILENCE1, String.format("I'm sorry I didn't hear anything. What %s would you like?", subject));
		add(PromptType.SILENCE2, String.format("I still didn't hear anything.  Please say an %s.", subject));
		add(PromptType.NORECO1, String.format("I didn't get that. What %s would you like?", subject));
		add(PromptType.NORECO2, String.format("I still didn't understand.  Please say an %s you would like?", subject));
	}
	
	/**
	 * Add a prompt of the given type
	 * @param type
	 * @param text
	 */
	public void add(PromptType type, String text)
	{
		if (text.length() > 0) { //ignore empty prompts
			m_promptSet.add(new Prompt(type, text));
		}
	}
	
	/**
	 * Add a prompt (low-level), but only if not already
	 * in prompt set
	 * @param prompt
	 */
	public void addPrompt(Prompt prompt)
	{
		if (m_promptSet.find(prompt.type(), prompt.subIndex()) == null) {
			m_promptSet.add(prompt);
		}
	}
	
	/**
	 * Render the prompts in this question into PromptItems,
	 * which are used to generate VoiceXML.
	 */
	public void renderPrompts(PromptPipeline pipeline)
	{
		m_promptSet.renderPrompts(pipeline);
	}
	
	/**
	 * Render the grammars in this question into GrammarItems,
	 * which are used to generated VoiceXML.
	 */
	@Override
	public void renderGrammars(GrammarPipeline pipeline)
	{
		m_grammarSet.renderGrammars(pipeline);
	}
	
}

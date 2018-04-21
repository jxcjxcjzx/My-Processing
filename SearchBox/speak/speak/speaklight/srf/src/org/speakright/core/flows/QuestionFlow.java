/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.flows;
import org.speakright.core.FlowBase;
import org.speakright.core.render.Grammar;
import org.speakright.core.render.IFlowRenderer;
import org.speakright.core.render.ISpeechForm;
import org.speakright.core.render.ISpeechPage;
import org.speakright.core.render.Prompt;
import org.speakright.core.render.PromptType;
import org.speakright.core.render.Question;

/**
 * Flow object that asks the user a question.  The user
 * can reply using either speech or DTMF digits, depending
 * on the grammar used.
 * 
 * This class implements a single-slot single question.  This
 * is the simplest form of directed dialog VUI.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class QuestionFlow extends FlowBase {
	protected Question m_quest;
	
	public QuestionFlow() {
		m_quest = new Question();
	}

	/**
	 * Constructor.
	 * @param gtext a grammar text (url, inline grammar, etc)
	 * @param ptext prompt text
	 */
	public QuestionFlow(String gtext, String ptext) {
		super();
		m_quest = new Question(new Grammar(gtext), new Prompt(ptext));
	}

	/**
	 * initialize the main, silence, and noreco prompts to
	 * default values.
	 * @param subject  the subject of the question, such as "flight".  Used to construct the prompts.
	 * Of course, simply inserting a subject into our default prompts may not always be grammatically correct.
	 * Issues such as lack of pluralization may occur.  In those cases, you should set the prompts yourself.
	 */
	protected void InitPrompts(String subject)
	{
		m_quest.InitPrompts(subject);
	}
	
	/**
	 * Add a prompt to the question.
	 * @param type type of prompt.  This includes the role (MAIN, SILENCE, NORECO) and escalation count.
	 * @param text prompt text.
	 */
	public void add(PromptType type, String text)
	{
		m_quest.m_promptSet.add(new Prompt(type, text));
	}

	/**
	 * Add a prompt to the question. (low-level)
	 * @param type type of prompt.  This includes the role (MAIN, SILENCE, NORECO) and escalation count.
	 * @param text prompt text.
	 */
	public void addPrompt(Prompt prompt)
	{
		m_quest.m_promptSet.add(prompt);
	}

	/**
	 * Add/replace a grammar to the question.
	 * Each question can have at most one VOICE and one DTMF grammar.
	 * @param gram grammar
	 */
	public void addGrammar(Grammar gram)
	{
		m_quest.addGrammar(gram);
	}
	/**
	 * Add/replace a grammar to the question.
	 * Each question can have at most one VOICE and one DTMF grammar.
	 * @param grammar text such as "inline:yes no"
	 */
	public void addGrammar(String gtext)
	{
		m_quest.addGrammar(new Grammar(gtext));
	}
	
	/**
	 * Add a model binding.
	 * @param slotName  slot in the grammar that is the value to be bound.
	 * When the speech platform sends back the user input, it sends back
	 * (slot,value) pairs.
	 * @param modelVar name of the model variable to bind to.  This variable
	 * must have get and set methods with the following signatures
	 *   <code>public {type} {modelVar}();</code>  
	 *   <code>public void set{modelVar}({type} value);</code>
	 * Examples:
	 *   <code>public String City();</code>  
	 *   <code>public void setCity(String value);</code>  
	 */
	public void addBinding(String slotName, String modelVar)
	{
		Grammar gram = m_quest.grammar();
		gram.addBinding(slotName, modelVar);
	}
	

	/**
	 * Create the type-specific renderer
	 */
	@Override public IFlowRenderer createRenderer()
	{
		return new QuestiontFlowRenderer();
	}
	
	/**
	 * an renderer for this flow object. 
	 * @author Ian Rae
	 *
	 */
	private class QuestiontFlowRenderer implements IFlowRenderer
	{
		public void Render(ISpeechPage page, ISpeechForm form)
		{
			form.addField(m_quest);
		}
	}
}

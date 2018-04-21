package org.speakright.core;
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
	 * @param gramUrl a grammar url
	 * @param text prompt text
	 */
	public QuestionFlow(String gramUrl, String text) {
		super();
		m_quest = new Question(new Grammar(gramUrl), new Prompt(text));
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
	protected void add(PromptType type, String text)
	{
		m_quest.m_promptSet.add(new Prompt(type, text));
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

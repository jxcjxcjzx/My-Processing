/**
 * Copyright (c) 2007 Ian Rae
 * All Rights Reserved.
 * Licensed under the Eclipse Public License - v 1.0
 * For more information see http://www.eclipse.org/legal/epl-v10.html
 */
package org.speakright.sro;

import static org.junit.Assert.assertEquals;

import java.io.*;

import org.speakright.core.ConfirmationWrapper;
import org.speakright.core.IConfirmationFlow;
import org.speakright.core.IExecutionContext;
import org.speakright.core.IFlow;
import org.speakright.core.SRResults;
import org.speakright.core.StringSwitch;
import org.speakright.core.flows.ChoiceFlow;
import org.speakright.core.flows.QuestionFlow;
import org.speakright.core.render.Grammar;
import org.speakright.core.render.Prompt;
import org.speakright.core.render.PromptType;
import org.speakright.core.render.Question;

/**
 * Base class for all SROs (SpeakRight Objects). SROs are re-usable "speech
 * objects" for common things like dates, times, numbers, and currency.
 * 
 * Each SRO has
 * <ul>
 * <li>a main prompt (m_main1Prompt)</li>
 * <li>3 silence prompts (m_silence1Prompt, m_silence2Prompt, m_silence3Prompt)
 * used for escalating error messages.</li>
 * <li>3 noreco prompts (m_noreco1Prompt, m_noreco2Prompt, m_noreco3Prompt)
 * used for escalating error messages.</li>
 * <li>a subject, such as 'tickets' (used to generate prompts)</li>
 * <li>an optional modelVar. If this SRO is a question then the resulting user
 * input value will be stored in the model var</li>
 * <li>an optional confirmation flow object for doing confirmation.</li>
 * </ul>
 * 
 * @author IanRaeLaptop
 * 
 */
@SuppressWarnings("serial")
public class BaseSROQuestion extends QuestionFlow {

	protected String m_prefixPrompt = "What ";

	public void set_prefixPrompt(String text) {
		m_prefixPrompt = text;
	}

	protected String m_main1Prompt = "{%prefixPrompt%}{%subject%}?";

	public void set_main1Prompt(String text) {
		m_main1Prompt = text;
	}

	// add main2, etc if needed!! I don't think they're ever used.

	protected String m_silence1Prompt = "I'm sorry I didn't hear anything. {%main1Prompt%}";

	public void set_silence1Prompt(String text) {
		m_silence1Prompt = text;
	}

	protected String m_silence2Prompt = "I still didn't hear anything. {%main1Prompt%}";

	public void set_silence2Prompt(String text) {
		m_silence2Prompt = text;
	}

	protected String m_silence3Prompt = "";

	public void set_silence3Prompt(String text) {
		m_silence3Prompt = text;
	}

	protected String m_silence4Prompt = "";

	public void set_silence4Prompt(String text) {
		m_silence4Prompt = text;
	}

	protected String m_noreco1Prompt = "I didn't get that. {%main1Prompt%}";

	public void set_noreco1Prompt(String text) {
		m_noreco1Prompt = text;
	}

	protected String m_noreco2Prompt = "I still didn't get that. {%main1Prompt%}";

	public void set_noreco2Prompt(String text) {
		m_noreco2Prompt = text;
	}

	protected String m_noreco3Prompt = "";

	public void set_noreco3Prompt(String text) {
		m_noreco3Prompt = text;
	}

	protected String m_noreco4Prompt = "";

	public void set_noreco4Prompt(String text) {
		m_noreco4Prompt = text;
	}

	protected int m_maxReExecutions = 3; // limits retries due to
											// validate-fail. Is 1 + # of
											// retries

	protected String m_subjectWord = "items"; // say the plural
	transient protected SROSubjectItem m_subject = null; //used in execute, can be null

	protected String m_slotName = "";

	protected String m_modelVar = ""; // optional, a model var to bind to
	protected ChoiceFlow m_cmds; //can be null. Contains commands and their sub-flows
	
	// cw stuff
	MyConfirmationWrapper m_cw;

	public BaseSROQuestion()
	{
		this("");
	}
	public BaseSROQuestion(String subject) {
		m_subjectWord = subject;
		m_quest.addGrammar(new Grammar(""));

		// initialize prompts here, but we do it again in execute
		// in case derived classes changed prompt fields like m_mainPrompt
		// dynamically
		initPrompts();
	}

	public void setModelVar(String modelVar) {
		m_modelVar = modelVar;
	}

	/**
	 * Adds a command such as "cancel" or "main menu".  
	 * @param cmd  the user input string for this cmd.
	 * @param flow  IFlow to be executed when the user input equals <code>choice</code>
	 */
	public void addCommand(String cmd, IFlow flow)
	{
		if (m_cmds == null) {
			m_cmds = new ChoiceFlow("");
		}
		m_cmds.addChoice(cmd, flow);
	}
	
	/**
	 * Add a cancel command.  The action of the cancel command is to
	 * terminate this SRO.  Many applications have this pattern of dialog,
	 * where the user can "enter a flight number, or say cancel".
	 * @param cmd the command user input
	 */
	public void addCancelCommand(String cmd)
	{
		if (m_cmds == null) {
			m_cmds = new ChoiceFlow("");
		}
		addCommand(cmd, new SROCancelCommand());
	}
	
	protected void initPrompts() {
		// load prompt set using our prompt fields
		initPrompt(PromptType.MAIN1, m_main1Prompt);
		initPrompt(PromptType.SILENCE1, m_silence1Prompt);
		initPrompt(PromptType.SILENCE2, m_silence2Prompt);
		initPrompt(PromptType.SILENCE3, m_silence3Prompt);
		initPrompt(PromptType.SILENCE4, m_silence4Prompt);
		initPrompt(PromptType.NORECO1, m_noreco1Prompt);
		initPrompt(PromptType.NORECO2, m_noreco2Prompt);
		initPrompt(PromptType.NORECO3, m_noreco3Prompt);
		initPrompt(PromptType.NORECO4, m_noreco4Prompt);
		
		initSubPrompts(m_quest);
	}
	
	protected void initSubPrompts(Question quest)
	{}

	protected void initPrompt(PromptType type, String text) {
		m_quest.add(type, text);
	}

	protected void initPrompts(IExecutionContext context) {
		if (m_subject == null) {
			m_subject = new SROSubjectItem(m_subjectWord, 2); //assume plural. derived classes can alter
		}
		initPrompts();
	}

	protected void setSubjectPlurality(int num, IExecutionContext context)
	{
		if (m_subject == null) {
			m_subject = new SROSubjectItem(m_subjectWord, num); 
		}
		m_subject.setNum(num);
	}

	/**
	 * A common pattern is that the main prompt changes depending on whether
	 * this is the first time we've asked the question, or we're re-asking the
	 * question because of validation-failure or confirmation-rejection.
	 * 
	 * @param context
	 * @param normal
	 * @param validationFailed
	 * @param confirmationWasRejected
	 */
	protected void initMainPrompt(IExecutionContext context, String normal,
			String validationFailed, String confirmationWasRejected) {
		// load prompt set using our prompt fields
		String text = normal;
		if (context.ConfirmationWasRejected()) {
			text = confirmationWasRejected;
		} else if (context.ValidateFailed()) {
			text = validationFailed;
		}
		initPrompt(PromptType.MAIN1, text);
	}

	/**
	 * Set a confirmer. The confirmer flow object will confirm user input (eg.
	 * "Do you want Boston?")
	 * 
	 * @param flow
	 *            a confirmer object, or null to clear the confirmer
	 */
	public void setConfirmer(IConfirmationFlow flow) {
		m_cw = new MyConfirmationWrapper(this, flow, this);
	}
	
	protected void setPromptCondition(Prompt prompt, String condition)
	{
		condition = condition.toLowerCase().trim();
		String[] ar = {	"play-once", "play-once-ever" };
		
		prompt.setConditionNone();
		int pos = StringSwitch.indexOf(ar, condition);
		switch(pos) {
		case 0:
			prompt.setConditionPlayOnce();
			break;
		case 1:
			prompt.setConditionPlayOnceEver();
			break;
		case 2:
			//log error
			break;
		}
	}

	/**
	 * A hack to avoid stack overflow when ConfirmationWrapper calls
	 * m_question.getNext, which in this case is this object (BaseSROQuestion).
	 * So ConfirmationWrapper nows calls a virtual method doQuestionGetNext
	 * which we override in MyConfirmationWrapper to call here, all so we can
	 * call super.getNext. I think in Java you can only do super. from inside
	 * the object.
	 * 
	 * @param current
	 *            passed to getNext
	 * @param results
	 *            passed to getNext
	 * @return null if we're finished, else the next flow object to run.
	 */
	IFlow doQuestionGetNext(IFlow current, SRResults results) {
		return super.getNext(current, results);
	}

	@Override
	public IFlow getNext(IFlow current, SRResults results) {
		if (m_cmds != null) {
			IFlow flow = m_cmds.getNext(current, results);
			if (flow != null) {
				log("COMMAND seen: " + results.m_input);
				
				if (flow instanceof SROCancelCommand) {
					log("COMMAND-CANCEL!");
					return null;
				}
				return flow;
			}
		}

		if (m_cw == null) {

			return super.getNext(current, results);
		} else {
			return m_cw.getNext(current, results); // confirmerGetNext(current,
													// results);
		}
	}

	@Override
	public void execute(IExecutionContext context) {
		context.registerPromptFile("$sro$\\prompts.xml");
		// generate prompts here in case derived classes changed prompt fields
		// dynamically.
		initPrompts(context);
		
		if (m_modelVar.length() > 0) {
			m_quest.grammar().addBinding(m_slotName, m_modelVar);
		}

		super.execute(context);
	}
	
	@Override
	public IFlow onValidateFailed(IFlow current, SRResults results) {
		if (executionCount() <= this.m_maxReExecutions) {
			return this; // try again
		}
		return null; // give up
	}

	/*
	 * Hack -- see BaseSROQuestion.doQuestionGetNext
	 */
	public class MyConfirmationWrapper extends ConfirmationWrapper {

		BaseSROQuestion m_self;

		public MyConfirmationWrapper(IFlow question,
				IConfirmationFlow confirmer, BaseSROQuestion self) {
			super(question, confirmer);
			m_self = self;
		}

		@Override
		public IFlow doQuestionGetNext(IFlow current, SRResults results) {
			return m_self.doQuestionGetNext(current, results);
		}
	}
}

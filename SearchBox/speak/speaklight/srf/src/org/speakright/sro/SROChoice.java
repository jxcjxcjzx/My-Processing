package org.speakright.sro;

import java.util.*;
import org.speakright.core.IExecutionContext;
import org.speakright.core.SRResults;
import org.speakright.core.SRUtils;
import org.speakright.sro.gen.genSROChoice;
import org.speakright.core.render.InlineGrammar;
import org.speakright.core.render.PromptType;

/**
 * SRO for asking for an item from a list of possible items.  The items
 * are passed in as a gtext, or you can provide a custom grammar.
 * The error prompts will list two possible values (m_sampleValue1 and
 * m_sampleValue2).
 * 
 * Features
 * <ul>
 * <li>creates an inline grammar on the fly (if you don't explicitly provide a grammar).</li>
 *  </ul>
 *  
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class SROChoice extends genSROChoice {
	protected String m_sampleValue1 = "";
	protected String m_sampleValue2 = "";
	ArrayList<String> m_choiceL = new ArrayList<String>();

	public SROChoice(String subject, String gtext)
	{
		super(subject);
		InlineGrammar gram = new InlineGrammar(gtext);
		m_choiceL = gram.wordList();
	}
	public SROChoice(String subject, ArrayList<String> choiceL)
	{
		super(subject);
		m_choiceL.addAll(choiceL);
		
		String words = "";
		for(String word : m_choiceL) {
			words += word;
			words += " ";
		}
		words = words.trim();
		addGrammar("inline: " + words);
	}
	
	public String SampleValue1() { return m_sampleValue1; }
	public String SampleValue2() { return m_sampleValue2; }
	public void setSampleValue1(String s) { m_sampleValue1 = s; }
	public void setSampleValue2(String s) { m_sampleValue2 = s; }
	
	@Override
	public void initPrompts(IExecutionContext context)
	{
		super.initPrompts(context);
//		String outOfRange = (m_min >= 1 && m_max <= 100) ? m_outOfRangeExactPrompt : m_outOfRangePrompt;
//		initMainPrompt(context, m_main1Prompt, outOfRange, m_confirmWasRejectedPrompt);
	}
	
	
}

package org.speakright.sro;

import org.speakright.core.IExecutionContext;
import org.speakright.core.SRResults;
import org.speakright.core.SRUtils;
import org.speakright.sro.gen.genSRODigitString;
import org.speakright.core.render.Grammar;
import org.speakright.core.render.PromptType;

/**
 * SRO for getting one or more digits, such as a PIN.
 * The digits can be entered as "two two two" or "two twenty-two", or
 * "two hundred and twenty-two",
 * 
 * Features
 * <ul>
 * <li>range min..max (inclusive).  If user input is outside this range then
 *  a validation-fail occurs and the question is asked again using the outOfRangePrompt.</li>
 * <li>useBuiltInGrammars.  If true (the default), uses voicexml 2.0 builtin grammar digits?length={numDigits}</li> 
 * </ul>
 *  
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class SRODigitString extends genSRODigitString {
	protected int m_numDigits;
	protected boolean m_useBuiltInGrammars = true;

	public SRODigitString(String subject)
	{
		this(subject, 4);
	}
	public SRODigitString(String subject, int numDigits)
	{			
		super(subject);
		setNumberOfDigits(numDigits);
	}
	
	public int numberOfDigits() { return m_numDigits; }
	public void setNumberOfDigits(int n) 
	{ 
		m_numDigits = n;
		selectGrammar();
	}
	
	public boolean useBuiltInGrammars() { return m_useBuiltInGrammars; }
	public void setUseBuiltInGrammars(boolean b)
	{
		m_useBuiltInGrammars = b;
		selectGrammar();
	}
	
	void selectGrammar()
	{
		if (m_useBuiltInGrammars) {
			String gtext = String.format("builtin:digits?length=%d", m_numDigits);
			addGrammar(new Grammar(gtext));
			return;
		}
		
		Grammar gram = null;
		switch(m_numDigits) {
		case 1:
			gram = this.m_digits1Grammar;
			break;
		case 2:
			gram = this.m_digits2Grammar;
			break;
		case 3:
			gram = this.m_digits3Grammar;
			break;
		case 4:
			gram = this.m_digits4Grammar;
			break;
		case 5:
			gram = this.m_digits5Grammar;
			break;
		case 6:
			gram = this.m_digits6Grammar;
			break;
		case 7:
			gram = this.m_digits7Grammar;
			break;
		case 8:
			gram = this.m_digits8Grammar;
			break;
		case 9:
			gram = this.m_digits9Grammar;
			break;
		case 10:
			gram = this.m_digits10Grammar;
			break;
		case 11:
			gram = this.m_digits11Grammar;
			break;
		case 12:
			gram = this.m_digits12Grammar;
			break;
		case 13:
			gram = this.m_digits13Grammar;
			break;
		case 14:
			gram = this.m_digits14Grammar;
			break;
		case 15:
			gram = this.m_digits15Grammar;
			break;
		case 16:
			gram = this.m_digits16Grammar;
			break;
		}
		
		if (gram != null) {
			addGrammar(gram);
		}
	}
	
	
	@Override
	public void initPrompts(IExecutionContext context)
	{
		super.initPrompts(context);
		String outOfRange = m_outOfRangePrompt;
		initMainPrompt(context, m_main1Prompt, outOfRange, m_confirmWasRejectedPrompt);
	}
	
}

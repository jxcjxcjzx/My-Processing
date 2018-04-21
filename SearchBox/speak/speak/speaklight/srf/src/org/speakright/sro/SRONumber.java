package org.speakright.sro;

import org.speakright.core.IExecutionContext;
import org.speakright.core.SRResults;
import org.speakright.core.SRUtils;
import org.speakright.sro.gen.genSRONumber;
import org.speakright.core.render.PromptType;

/**
 * SRO for getting a number from the user, such as "222"
 * which can be said as "two twenty-two" or "two hundred and twenty-two",
 * or as DTMF digits "222";
 * 
 * Features
 * <ul>
 * <li>range min..max (inclusive).  If user input is outside this range then
 *  a validation-fail occurs and the question is asked again using the outOfRangePrompt.</li>
 *  </ul>
 *  
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class SRONumber extends genSRONumber {
	protected int m_min;
	protected int m_max;

	public SRONumber(String subject)
	{
		this(subject, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	public SRONumber(String subject, int min, int max)
	{			
		super(subject);
		m_min = min;
		m_max = max;
		selectGrammar();
//		addGrammar("builtin:digits?length=1");
	}
	
	public int Min() { return m_min; }
	public int Max() { return m_max; }
	public void setMin(int n) 
	{ 
		m_min = n;
		selectGrammar();
	}
	public void setMax(int n) 
	{ 
		m_max = n; 
		selectGrammar();
	}
	
	void selectGrammar()
	{
		//we assume max >= min and min >= 0
		if (m_max <= 10) {
			addGrammar(m_toTenGrammar);
		}
		else if (m_max <= 100) {
			addGrammar(m_toHundredGrammar);
		}
		else {
			addGrammar(m_toMillionGrammar);
		}
	}
	
	@Override
	public void initPrompts(IExecutionContext context)
	{
		super.initPrompts(context);
		String outOfRange = (m_min >= 1 && m_max <= 100) ? m_outOfRangeExactPrompt : m_outOfRangePrompt;
		initMainPrompt(context, m_main1Prompt, outOfRange, m_confirmWasRejectedPrompt);
	}
	
	@Override
	public boolean validateInput(String input, SRResults results) {
		int val = SRUtils.safeToInt(results.m_input);
		boolean b = (val >= m_min) && (val <= m_max);
		this.log("val on " + results.m_input);
		return b;
	}
}

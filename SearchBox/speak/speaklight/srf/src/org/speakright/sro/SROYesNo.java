package org.speakright.sro;

import org.speakright.core.IFlow;
import org.speakright.core.SRResults;
import org.speakright.sro.gen.genSROYesNo;

@SuppressWarnings("serial")
public class SROYesNo extends genSROYesNo {

	public SROYesNo()
	{
		this("");
	}
	public SROYesNo(String subject)
	{
		super(subject);
		addGrammar(m_yesnoGrammar);
	}
	@Override
	public IFlow getNext(IFlow current, SRResults results) {
		if (results.m_input.equals("yes")) {
			return onYes();
		}
		else if (results.m_input.equals("no")) {
			return onNo();
		}
		return null; //should never get here!!
	}
	
	public IFlow onYes()
	{
		return null;
	}
	
	public IFlow onNo()
	{
		return null;
	}
}

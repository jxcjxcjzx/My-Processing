//THIS CLASS HAS BEEN AUTOMATICALLY GENERATED BY SROGEN. DO NOT MODIFY BY HAND

/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.sro.gen;
import org.speakright.core.*;
import org.speakright.core.render.*;
import org.speakright.sro.BaseSROQuestion;
import java.io.Serializable;

@SuppressWarnings("serial")
public class genSROYesNo extends BaseSROQuestion {
	
	/* prompts */
	

	/* sub-prompts */
	

	/* grammars */
	protected Grammar m_yesnoGrammar = new Grammar("inline:yes no", "result");
	public void set_yesnoGrammar(Grammar gram)
	{
		m_yesnoGrammar = gram;
	}
	
	/* constructor */
	public genSROYesNo(String subject)
	{
		super(subject);
		m_main1Prompt = "id:main1"; 	
			
		
		m_slotName = "result";
	}
	
	@Override
	protected void initSubPrompts(Question quest)
	{
			
	}
	
	@Override
	public void execute(IExecutionContext context) 
	{
		context.registerPromptFile("$sro$\\" + "SROYesNo_prompts.xml");
		
		super.execute(context);
	}
}
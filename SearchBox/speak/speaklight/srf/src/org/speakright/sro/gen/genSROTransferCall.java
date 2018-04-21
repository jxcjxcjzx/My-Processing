//THIS CLASS HAS BEEN AUTOMATICALLY GENERATED BY SROGEN. DO NOT MODIFY BY HAND

/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.sro.gen;
import org.speakright.core.*;
import org.speakright.core.flows.TransferFlow;
import org.speakright.core.render.*;
import org.speakright.sro.BaseSROQuestion;
import java.io.Serializable;

@SuppressWarnings("serial")
public class genSROTransferCall extends TransferFlow {
	
	/* prompts */
	protected String m_mainPrompt;
	public void set_mainPrompt(String text)
	{
		m_mainPrompt = text;
	}	
	protected String m_transferFailedPrompt;
	public void set_transferFailedPrompt(String text)
	{
		m_transferFailedPrompt = text;
	}	

	/* sub-prompts */
	
	

	/* grammars */
	
	/* constructor */
	public genSROTransferCall()
	{
		m_mainPrompt = "id:main"; 	
		m_transferFailedPrompt = "id:transferFailed"; 	
			
			
		
	}
	
	
	@Override
	public void execute(IExecutionContext context) 
	{
		context.registerPromptFile("$sro$\\" + "SROTransferCall_prompts.xml");
		
		super.execute(context);
	}
}
/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.flows;

import org.speakright.core.FlowBase;
import org.speakright.core.IFlow;
import org.speakright.core.SRResults;
import org.speakright.core.render.IFlowRenderer;
import org.speakright.core.render.ISpeechForm;
import org.speakright.core.render.ISpeechPage;
import org.speakright.core.render.Prompt;
import org.speakright.core.render.Transfer;

/**
 * A call control flow object that transfers the call.
 * @author IanRaeLaptop
 *
 */
public class TransferFlow extends FlowBase {
	/**
	 * tranfer types from VXML 2.1
	 * @author IanRaeLaptop
	 *
	 */
	public enum TransferType {
		Bridge,
		Blind,
		Consultation
	}
	
	Prompt m_prompt; //say goodbye or something..
	String m_destination; //DN of the party to receive the call
	TransferType m_transferType;
	
	public TransferFlow() {
		this(TransferType.Blind, "");
	}

	public TransferFlow(TransferType type, String destination) {
		this(TransferType.Blind, destination, "this call is being transferred.");
	}
	
	public TransferFlow(TransferType type, String destination, String ptext) {
		super("transfer");
		m_destination = destination;
		m_transferType = type;
		m_prompt = new Prompt(ptext);
	}
	
	public String Prompt()
	{
		return m_prompt.ptext();
	}
	public void setPrompt(String ptext) 
	{
		m_prompt.setPText(ptext);
	}
	public String Destination()
	{
		return m_destination;
	}
	public void setDestination(String destination) 
	{
		m_destination = destination;
	}
	public TransferType Type()
	{
		return m_transferType;
	}
	public void setType(TransferType type) 
	{
		m_transferType = type;
	}
		

	/**
	 * Handle a failed transfer.  results.m_transferResult will contain the reason
	 * for the failure ("busy", "rna", etc)  
	 * <p>
	 * The default implementation is to play a short error
	 * message and continue.  Rather than expose the error prompt as a field, we 
	 * encourage users to use SROTransferCall, or to override onTransferFailed.
	 */
	@Override
	public IFlow onTransferFailed(IFlow current, SRResults results) {
		PromptFlow flow = new PromptFlow("The transfer failed.");
		flow.setName(name() + "_Failed");
		return flow;
	}

	/**
	 * Create the type-specific renderer
	 */
	@Override public IFlowRenderer createRenderer()
	{
		return new TransferFlowRenderer();
	}
	
	/**
	 * an renderer for this flow object. 
	 * @author Ian Rae
	 *
	 */
	private class TransferFlowRenderer implements IFlowRenderer
	{
		public void Render(ISpeechPage page, ISpeechForm form)
		{
			form.addField(m_prompt);
			form.addField(new Transfer(m_transferType, m_destination, m_prompt));
		}
	}

}

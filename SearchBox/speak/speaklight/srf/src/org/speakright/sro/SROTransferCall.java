package org.speakright.sro;

import org.speakright.core.IExecutionContext;
import org.speakright.core.IFlow;
import org.speakright.core.SRResults;
import org.speakright.core.SRUtils;
import org.speakright.sro.gen.genSROTransferCall;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.TransferFlow;
import org.speakright.core.flows.TransferFlow.TransferType;
import org.speakright.core.render.PromptType;

/**
 * SRO for transferring a phone call.
 * Usage of this class is preferred over TransferFlow because the transfer-failed prompt
 * in SROTransferCall can be modified by external prompt XML file.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class SROTransferCall extends genSROTransferCall {

	public SROTransferCall(String destination)
	{
		this(destination, TransferFlow.TransferType.Blind);
	}
	public SROTransferCall(String destination, TransferFlow.TransferType type)
	{			
		super();
		setName("SROTransferCall");
		this.setDestination(destination);
		this.setType(type);
	}
	
	
	@Override
	public void execute(IExecutionContext context) {
		setPrompt(m_mainPrompt);
		super.execute(context);
	}
	
	/**
	 * Handle a failed transfer.  results.m_transferResult will contain the reason
	 * for the failure ("busy", "rna", etc)  
	 * <p>
	 * The default implementation is to play a short error
	 * message and continue.  To change this behaviour, override this method
	 * and either throw an event or return a sub-flow object to run. 
	 */
	@Override
	public IFlow onTransferFailed(IFlow current, SRResults results) {
		PromptFlow flow = new PromptFlow(m_transferFailedPrompt);
		flow.setName(name() + "_Failed");
		return flow;
	}
}

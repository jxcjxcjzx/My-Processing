package org.speakright.sro;
import org.speakright.core.IConfirmationFlow;
import org.speakright.core.IConfirmationNotifier;
import org.speakright.core.IExecutionContext;
import org.speakright.core.IFlow;
import org.speakright.core.SRResults;
import org.speakright.core.SRUtils;
import org.speakright.sro.gen.*;

/**
 * SRO for doing simple yes/no confirmation.  This confirmer can be used
 * with any SRO by passing an instance of SROConfirmYesNo to the SRO's
 * setConfirmer method.
 * 
 * This confirmer does explicit confirmation, accepting yes or no as the
 * answer. eg. "Do you want Boston?"  If the user answers no then a confirm-rejection
 * occurs and the SRO asks its question again.
 * 
 * Features
 * <ul>
 * <li>a confirmation threshold at which the confirmer should execute.  If the user input
 * has a confirmation above this threshold, the confirmer does not do anything. So
 * setting confirmation threshold to 100 means confirmation will always be done,
 * 0 means it will never be done.</li>
 * </ul> 
 *  
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class SROConfirmYesNo extends genSROConfirmYesNo  implements IConfirmationFlow {
	IConfirmationNotifier m_notifier;
	int m_confirmThreshold = 80;

	public SROConfirmYesNo(String subject)
	{
		super(subject);
		//addGrammar("inline:yes no");
	}
	
	public int confirmThreshold()
	{
		return m_confirmThreshold;
	}
	public void setConfirmThreshold(int threshold)
	{
		m_confirmThreshold = threshold;
	}
	
	public void setNotifier(IConfirmationNotifier notifier)
	{
		m_notifier = notifier;
	}
	
	
	public boolean needToExecute(IFlow current, SRResults results) {
		SRResults.Slot slot = results.getIthSlot(0); //first slot of previous question
		if (slot == null) {
			return results.m_overallConfidence < m_confirmThreshold;
		}
		return slot.m_confidence < m_confirmThreshold;
	}

	@Override
	public IFlow getNext(IFlow current, SRResults results) 
	{
		boolean wasRejected = results.m_input.equals("no");
		log("sroRejected: " + wasRejected);
		m_notifier.notifyConfirmationFinished(wasRejected,  results);
		return super.getNext(current, results); //better be null!
	}

}

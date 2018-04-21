/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;


/**
 * Implements confirmation in an extensible way.  The class is a flow object
 * with two sub-flows: a question and a confirmer.  The results from the
 * question are passed to the confirmer's needToExecute method.  The confirmer
 * is run if it wants to.  Then if the user rejects (says 'no') then the
 * question is asked again and the process repeats.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class ConfirmationWrapper extends FlowBase implements IConfirmationNotifier {

	IFlow m_question;
	IConfirmationFlow m_confirmer;
	State m_state; //make transient later if need to
	SRResults m_savedResults = null; //used to hold the results (in case they are accepted
								//we need to restore them)
	boolean m_wasRejected;
	
	enum State {
		ASK,
		CONFIRM,
		CONFIRM_FINISHED
	}
	
	public ConfirmationWrapper(IFlow question, IConfirmationFlow confirmer)
	{
		m_question = question;
		m_confirmer = confirmer;
		m_confirmer.setNotifier(this);
		m_state = State.ASK;
	}
	
	/**
	 * start with the question.
	 */
	@Override
	public IFlow getFirst(IFlowContext context) {
		return m_question;
	}
	
	public IFlow doQuestionGetNext(IFlow current, SRResults results)
	{
		return m_question.getNext(current, results);
	}

	/**
	 * Apply the confirmation based on the state.  In ASK state we activate the confirmer
	 * if it wants execute.  In CONFIRM_FINISHED state we switch back to ASK state if the user
	 * rejected the confirmation.
	 * Note. we don't track model vars and clear then during a rejection, because we're going
	 * to run the question again.  As long as you never this conf-wrapper before getting a good
	 * answer (possibly confirmed), you're OK. Else model may hold a rejected value.
	 */
	@Override
	public IFlow getNext(IFlow current, SRResults results) {
		IFlow flow = null;
		if (m_state == State.ASK) {
			flow = doQuestionGetNext(current, results);
			if (flow == null && m_confirmer.needToExecute(current, results)) {
				flow = m_confirmer;
				m_state = State.CONFIRM;
				m_savedResults = results;
				log("CONFIRMING");
			}
		}
//		else if (m_state == State.CONFIRM) {
//			flow = m_confirmer.getNext(current, results);
//			if (flow == null) {
//				if (m_confirmer.WasRejected()) {
//					m_state = State.ASK; //again!
//					flow = m_question;
//					log("ASK-AGAIN");
//				}
//				else {
//					log("CONFIRM WAS ACCEPTED");
//					results.loadFrom(m_savedResults);
//				}
//			}
//		}
		else if (m_state == State.CONFIRM_FINISHED) {
			if (m_wasRejected) {
				m_state = State.ASK; //again!
				flow = m_question;
				log("ASK-AGAIN");
			}
			else {
				log("old CONFIRM WAS ACCEPTED");
//					results.loadFrom(m_savedResults);
			}
		}
		return flow;
	}

	/**
	 * Callback from the confirmer.  It has finished, and is about to be popped off the flow stack, and
	 * the original question executed again.  We need to restore the saved results before that
	 * happens.
	 */
	public void notifyConfirmationFinished(boolean wasRejected, SRResults results) 
	{
		m_wasRejected = wasRejected;
		if (wasRejected) {
			log("nCONFIRM WAS REJECTED - reloading results: " + m_savedResults.m_input);
			m_savedResults.m_confirmationWasRejected = true;
		}
		else {
			log("nCONFIRM WAS ACCEPTED - reloading results: " + m_savedResults.m_input);
		}
		results.loadFrom(m_savedResults);
		m_state = State.CONFIRM_FINISHED;
	}
}

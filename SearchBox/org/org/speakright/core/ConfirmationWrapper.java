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
public class ConfirmationWrapper extends FlowBase {

	IFlow m_question;
	IConfirmationFlow m_confirmer;
	State m_state; //make transient later if need to
	
	enum State {
		ASK,
		CONFIRM
//		DONE
	}
	
	public ConfirmationWrapper(IFlow question, IConfirmationFlow confirmer)
	{
		m_question = question;
		m_confirmer = confirmer;
		m_state = State.ASK;
	}

	/**
	 * start with the question.
	 */
	@Override
	public IFlow getFirst() {
		return m_question;
	}

	/**
	 * Apply the confirmation based on the state.  In ASK state we activate the confirmer
	 * if it wants execute.  In CONFIRM state we switch back to ASK state if the user
	 * rejected the confirmation.
	 * Note. we don't track model vars and clear then during a rejection, because we're going
	 * to run the question again.  As long as you never this conf-wrapper before getting a good
	 * answer (possibly confirmed), you're OK. Else model may hold a rejected value.
	 */
	@Override
	public IFlow getNext(IFlow current, SRResults results) {
		IFlow flow = null;
		if (m_state == State.ASK) {
			flow = m_question.getNext(current, results);
			if (flow == null && m_confirmer.needToExecute(current, results)) {
				flow = m_confirmer;
				m_state = State.CONFIRM;
				log("CONFIRMING");
			}
		}
		else if (m_state == State.CONFIRM) {
			flow = m_confirmer.getNext(current, results);
			if (flow == null) {
				if (m_confirmer.WasRejected()) {
					m_state = State.ASK; //again!
					flow = m_question;
					log("ASK-AGAIN");
				}
			}
		}
		return flow;
	}
	
	
}

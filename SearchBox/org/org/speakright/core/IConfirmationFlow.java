package org.speakright.core;

/**
 * A flow that does confirmation (of a previous question).
 * Used by ConfirmationWrapper.  IConfirmationFlow is an extension point;
 * you can create your own type of confirmation objects to implement
 * various forms of confirmation such as explicit, implicit, confirm-and-correct,
 * etc.
 * @author Ian Rae
 *
 */
public interface IConfirmationFlow extends IFlow {

	/**
	 * Inspect the results to see if confirmation is needed.
	 * Some confirmers may only kick if in the confidence level of
	 * the previous answer was less than some threshold.
	 * @param current  the current flow objects
	 * @param results  results from the question for which we may do
	 * confirmation.
	 * @return true if confirmation should be done.
	 */
	boolean needToExecute(IFlow current, SRResults results);
	
	/**
	 * Did the user reject the confirmation (by saying 'no' or
	 * whatever the reject grammar word was).
	 * This is ONLY called immediately after getNext, so that the 
	 * flow object can use a transient boolean field for this.
	 * @return true if the user rejected the confirmation.
	 */
	boolean WasRejected();
}

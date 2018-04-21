/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
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
	 * Set the notifier, which MUST be called from within
	 * the confirmation flow's getNext.  
	 * Used to notify the confirmation wrapper.  MUST call this from getNext when
	 * the confirmation finishes.
	 * @param notifier the confirmation wrapper
	 */
	void setNotifier(IConfirmationNotifier notifier);
}

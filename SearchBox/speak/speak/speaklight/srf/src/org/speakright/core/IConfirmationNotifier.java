/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

/**
 * Used to notifier a confirmation wrapper, so it can restore
 * saved results.
 * Used by IConfirmationFlow
 * @author IanRaeLaptop
 *
 */
public interface IConfirmationNotifier {

	/**
	 * Did the user reject the confirmation (by saying 'no' or
	 * whatever the reject grammar word was).
	 * This method MUST be called from within the confirmation flow's
	 * getNext. 
	 * 
	 * @param wasRejected whether the confirmation was rejected or not
	 * @param results the results object passed to getNext.
	 */
	void notifyConfirmationFinished(boolean wasRejected, SRResults results);
}

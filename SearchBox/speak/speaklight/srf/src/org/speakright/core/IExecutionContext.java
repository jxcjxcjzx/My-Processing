/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

/**
 * A context object passed to the <code>execute</code> method of a flow
 * object.  It contains various things that a flow object might want
 * to do inside execute. 
 * @author IanRaeLaptop
 *
 */
public interface IExecutionContext extends IFlowContext {

	/**
	 * Throw a custom event.
	 * @param event
	 */
	void throwEvent(ThrowEvent event);
	
	/**
	 * Render the given flow object into a voicexml page.
	 * @param flow a flow object
	 */
	void render(IFlow flow);
	
	/**
	 * Register (temporarily) a prompt XML file.
	 * The registration only used during the current execute method.
	 * If you want to register a prompt file for the life of
	 * the application use SRInstance.registerPromptFile
	 * @param path  full file path to the prompts XML file.
	 */
	void registerPromptFile(String path);
	
	/**
	 * Get the results of the previous turn.
	 * MAY BE NULL (if this is the first flow in the app)
	 */
	SRResults getResults();
	
//	/**
//	 * get current resource file locations.
//	 * @return locations object
//	 */
//	SRLocations getLocations();
	
	/**
	 * Return true if the call to ValidateInput for this flow returned
	 * false.  This method avoids flow object classes to have to declare
	 * their own boolean to track validation failure.
	 * Only works in the context of the current execute.
	 * @return result of ValidateInput
	 */
	boolean ValidateFailed();
	
	/**
	 * Return true if a confirmation was rejected by the user (by saying
	 * 'no' or whatever rejection phrase was in the confirmation grammar).
	 * Set to true by ConfirmationWrapper just before the previous question
	 * (flow object) executes again.
	 * This method is used by questions to alter their prompts, to add
	 * something like "OK, let's try that again".
	 * @return result of confirmation. false if no confirmation is active.
	 */
	boolean ConfirmationWasRejected();
}
/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

/**
 * Extension point for monitoring the execution of an SR app.
 * Uses a unique call identifier (uci) to represent a phone call.
 * 
 * @author IanRaeLaptop
 *
 */
public interface ISRInstrumentation {

	/**
	 * Starting execution of an app on a call.
	 * @param uci call identifier
	 * @param flow application flow
	 */
	void onCallBegin(int uci, IFlow flow);
	
	/**
	 * Execution of an app has ended.
	 * @param uci call identifier
	 */
	void onCallEnd(int uci);
	
	/**
	 * A flow has been activated (pushed onto 
	 * the flow stack.
	 * @param uci call identifier
	 * @param flow flow being activated.
	 */
	void onActivateFlow(int uci, IFlow flow);
	
	/**
	 * A flow is being executed and will now
	 * generate a VoiceXML page.
	 * @param uci call identifier
	 * @param flow flow being executed.
	 */
	void onExecuteFlow(int uci, IFlow flow);
//	void onEvent(int uci, IFlow flow);
	
	void onUserInput(int uci, IFlow flow, SRResults results);
	
	/**
	 * An error has ocurred.
	 * @param uci unique call identifer
	 * @param flow flow in which error occured.
	 */
	void onError(int uci, IFlow flow); //err!
}

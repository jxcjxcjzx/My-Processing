/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

/**
 * An application event is is a custom event that an application can "throw" in order to transfer control
 * to another active flow object. Think of "throwing" an event as a restricted type of goto.  App events 
 * are useful for handling generic behaviour in a single place in the application.  
 * 
 * <p>
 * For example, an "operator" event could be used to cause the application to
 * transfer to a human operator.  
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class AppEvent extends ThrowEvent {

	/**
	 * Create an application event.
	 * @param eventName event name.  Applications define their own event names.
	 */
	public AppEvent(String eventName) {
		super();
		setName(eventName);
	}
}

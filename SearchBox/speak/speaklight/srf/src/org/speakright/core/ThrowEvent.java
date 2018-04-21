/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

/**
 * "Throws" an event.  SpeakRight will find a matching event handler to process
 * the event.  It is an error for an event not to be caught.
 * <p>
 * Although SpeakRight uses the term "throwing an event", there are no Java execptions
 * being thrown.  The throw is merely in terms of the flow stack that the SpeakRight
 * interpreter uses to hold the currently executing flow objects.
 * 
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class ThrowEvent extends ControlFlow {

	public ThrowEvent() {
		super("__throw");
	}
}

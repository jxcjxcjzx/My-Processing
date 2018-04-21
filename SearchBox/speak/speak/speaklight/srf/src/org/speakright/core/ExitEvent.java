/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

/**
 * ExitEvent terminates execution immediately.  It is used to stop an application.
 * For example, the default implementation of OnDisconnect returns an ExitEvent.
 * 
 *
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class ExitEvent extends ControlFlow {

	public ExitEvent()
	{
		super("__exit");
	}
}

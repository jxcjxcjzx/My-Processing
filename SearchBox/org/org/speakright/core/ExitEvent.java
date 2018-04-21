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

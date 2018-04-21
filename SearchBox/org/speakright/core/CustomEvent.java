package org.speakright.core;

/**
 * A custom event is an application-defined event.  It is a way for the application
 * to add its own events that can be "thrown".  Events are useful for providing
 * generic behaviour that is handled in a single place in the application.  Sub-flows
 * in the application can override this behaviour by providing their own event handler.
 * 
 * <p>
 * For example, an "operator" custom event could be used to cause the application to
 * transfer to a human operator.  
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class CustomEvent extends ThrowEvent {

	public CustomEvent(String eventName) {
		super();
		setName(eventName);
	}
}

package org.speakright.core;

/**
 * Base class for all "events" in SpeakRight.  Events are special type of IFlow
 * object that causes control flow outside the normal getFirst/getNext sequencing
 * to occur.  SpeakRight's event handling code searches up the flow stack for
 * a matching event handler and executes the IFlow object that it returns.
 * <p> 
 * Although a ControlFlow is an IFlow it never generates VoiceXML; it's <code>execute</code>
 * is never called.
 * 
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class ControlFlow extends FlowBase {
	
	public ControlFlow(String name)
	{
		super(name); //this flow is really an event and is never executed
	}

}

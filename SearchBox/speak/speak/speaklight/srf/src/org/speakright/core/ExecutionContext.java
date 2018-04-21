/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import java.util.ArrayList;

import org.speakright.core.render.IFlowRenderer;

/**
 * Contains the VoiceXML page renderer.  Passed by SpeakRight to a flow object's execute method.
 * @author Ian Rae
 *
 */
public class ExecutionContext implements IExecutionContext {

	public ThrowEvent m_thrownEvent = null;
	public ArrayList m_renderL = new ArrayList(); //list of IFlowRenderers
	public IModelBinder m_binder;
	public ArrayList<String> m_promptFileL;
	public SRResults m_results;
	public IFlow m_flow; //the flow being executed
	public SRLocations m_locations;
	
	/** "Throws" an event.  Although most control flow in SpeakRight is done
	 * by getFirst and getNext, there are ocassions where execute needs to.
	 * For example, if execute needs to read an external database in order
	 * to play some information to the caller.  If the database read fails,
	 * then execute can "throw" an event to a handler that may branch back
	 * to the main menu, or terminate the call.
	 * 
	 * @param event  The event to be thrown, usually a CustomEvent.
	 */
	public void throwEvent(ThrowEvent event)
	{
		m_thrownEvent = event;
	}
	
	/**
	 * Render a flow object into a voicexml page.
	 */
	public void render(IFlow flow)
	{
		IFlowRenderer r = flow.createRenderer();
		if (r != null) {
			m_renderL.add(r);
		}
	}
	
	/**
	 * add the file to the list of prompt file locations.
	 * This path is only registered for the duration of the
	 * current flow's execute method.
	 * If you want to register a prompt file for the life of
	 * the application use SRInstance.registerPromptFile
	 * @param path  full file path to the prompts XML file.
	 */	
	public void registerPromptFile(String path)
	{
		m_promptFileL.add(path);
	}
	
	/**
	 * Get the results of the previous turn.
	 * MAY BE NULL (if this is the first flow in the app)
	 */
	public SRResults getResults()
	{
		return m_results;
	}
	
	/**
	 * Return true if the call to ValidateInput for this flow returned
	 * false.  This method avoids flow object classes to have to declare
	 * their own boolean to track validation failure.
	 * Only works in the context of the current execute.
	 * @return result of ValidateInput
	 */
	public boolean ValidateFailed()
	{
		//remember that m_results can be null (especially on the first execute in an app)
		if (m_results == null) {
			return false;
		}
		return ! m_results.m_validateSucceeded;
	}

	public boolean ConfirmationWasRejected()
	{
		//remember that m_results can be null (especially on the first execute in an app)
		if (m_results == null) {
			return false;
		}
		return m_results.m_confirmationWasRejected;
	}

	public SRLocations getLocations() {
		return m_locations;
	}

}


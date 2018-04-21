package org.speakright.core;

/**
 * A context object passed to the <code>execute</code> method of a flow
 * object.  It contains various things that a flow object might want
 * to do inside execute. 
 * @author IanRaeLaptop
 *
 */
public interface IExecutionContext {

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
	 * get the model binder.  Used to bind model variables
	 * to 'slots' in a Question.  Speech rec results will be
	 * stored in the bound model variable(s).
	 * @return model binder.
	 */
	IModelBinder modelBinder();
	
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
}
package org.speakright.servlet;

import org.speakright.core.SRRunner;

/**
 * Interface for a SpeakRight servlet.
 * @author IanRaeLaptop
 *
 */
public interface ISRServlet {

	/**
	 * some paths need to be set on each GET or POST.
	 * Add run.registerPromptFile here.
	 * @param run
	 */
	void initLocations(SRRunner run);
}

package org.speakright.core.render;
import java.util.ArrayList;

import org.speakright.core.*;

/**
 * Holds all the things we need to pass down the rendering chain.
 * @author Ian Rae
 *
 */
public class RenderContext {

	public IPromptAdjuster m_promptAdjuster;
	public IModelBinder m_binder;  	//binder can be null
	public IFlow m_flow;
	public PlayOnceTracker m_playOnceTracker;
	public SRLocations m_locations;
	public SRLogger m_logger;
	public ArrayList<String> m_promptFileL;
	public String m_returnUrl = "";
	public String m_baseGrammarUrl = "";
	public String m_basePromptUrl = "";
}

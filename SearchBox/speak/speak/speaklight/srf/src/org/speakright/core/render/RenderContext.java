/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
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
	public IGrammarAdjuster m_grammarAdjuster;
	public IModelBinder m_binder;  	//binder can be null
	public FieldBinder m_fieldBinder;
	public IFlow m_flow;
	public PlayOnceTracker m_playOnceTracker;
	public SRLocations m_locations;
	public SRLogger m_logger;
	public ArrayList<String> m_promptFileL;
	public String m_returnUrl = "";
//	public String m_baseGrammarUrl = "";
//	public String m_basePromptUrl = "";
	public boolean m_doAudioMatching = false;
	public boolean m_dtmfOnlyModeIsActive = false;
	public SRResults m_results; //can be null!
	public String m_promptGroup = "";
	public boolean m_logPrompts;
}

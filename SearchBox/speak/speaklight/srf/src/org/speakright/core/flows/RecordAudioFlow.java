/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.flows;

import org.speakright.core.FlowBase;
import org.speakright.core.render.IFlowRenderer;
import org.speakright.core.render.ISpeechForm;
import org.speakright.core.render.ISpeechPage;
import org.speakright.core.render.Prompt;
import org.speakright.core.render.RecordAudio;

/**
 * A flow object that records the caller's audio to an audio
 * file, which is sent back to the application.
 * @author IanRaeLaptop
 *
 */
public class RecordAudioFlow extends FlowBase {
	Prompt m_prompt; //say goodbye or something..
	boolean m_beepBeforeRecord;
	int m_maxTime; //in msec
	int m_finalSilenceTime; //in msec
	
	public RecordAudioFlow() {
		this("record a message");
	}

	public RecordAudioFlow(String text) {
		super("Record");
		m_prompt = new Prompt(text);
		m_beepBeforeRecord = true;
		m_maxTime = 60000; //60 seconds
		m_finalSilenceTime = 2500;
	}
		

	/**
	 * Create the type-specific renderer
	 */
	@Override public IFlowRenderer createRenderer()
	{
		return new RecordFlowRenderer();
	}
	
	/**
	 * an renderer for this flow object. 
	 * @author Ian Rae
	 *
	 */
	private class RecordFlowRenderer implements IFlowRenderer
	{
		public void Render(ISpeechPage page, ISpeechForm form)
		{
			form.addField(m_prompt);
			form.addField(new RecordAudio(m_beepBeforeRecord, m_maxTime, m_finalSilenceTime));
		}
	}

}

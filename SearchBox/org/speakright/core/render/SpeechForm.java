package org.speakright.core.render;
import java.util.ArrayList;

/**
 * Represents a voicexml form 
 * @author IanRaeLaptop
 *
 */
public class SpeechForm implements ISpeechForm {
//	public SRPrompt m_prompt;
	public ArrayList<FormElement>  m_fieldL = new ArrayList<FormElement>(); 

	public void addField(FormElement el)
	{
		m_fieldL.add(el);
	}
}

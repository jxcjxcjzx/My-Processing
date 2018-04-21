/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
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

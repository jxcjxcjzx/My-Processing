/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import org.speakright.core.render.*;
import org.speakright.core.render.voicexml.*;

/**
 * Our standard extension point factory.
 * @author IanRaeLaptop
 *
 */
public class StandardFactory implements ISRExtensionFactory {

	/**
	 * We're creating voicexml!
	 */
	public ISpeechPageWriter createPageWriter()
	{
		return new VoiceXMLSpeechPageWriter();
	}
	
}

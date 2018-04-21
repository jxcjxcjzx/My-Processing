package org.speakright.core;
import org.speakright.core.render.*;
import org.speakright.core.render.voicexml.*;

/**
 * Our standard extension point factory.
 * @author IanRaeLaptop
 *
 */
public class StandardFactory implements ISRFactory {

	/**
	 * We're creating voicexml!
	 */
	public ISpeechPageWriter createPageWriter()
	{
		return new VoiceXMLSpeechPageWriter();
	}
	
}

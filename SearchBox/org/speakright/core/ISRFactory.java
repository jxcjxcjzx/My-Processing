package org.speakright.core;
import org.speakright.core.render.*;

/**
 * A factory for all extension points in SpeakRight.
 * 
 * @author Ian Rae
 *
 */
public interface ISRFactory {

	/**
	 * Create a page writer that renders SpeakRight output content
	 * into VoiceXML or whatever markup text you want.
	 * @return
	 */
	ISpeechPageWriter createPageWriter();
}

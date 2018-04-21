/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import org.speakright.core.render.*;

/**
 * A factory for all extension points in SpeakRight.
 * 
 * @author Ian Rae
 *
 */
public interface ISRExtensionFactory {

	/**
	 * Create a page writer that renders SpeakRight output content
	 * into VoiceXML or whatever markup text you want.
	 * @return
	 */
	ISpeechPageWriter createPageWriter();
}

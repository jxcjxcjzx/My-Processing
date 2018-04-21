/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;
import org.speakright.core.SRError;

/**
 * All errors in the render package should have an enum value here.
 * @author Ian Rae
 *
 */
public enum RenderErrors {

	UnknownPromptId,
	UnknownModelVar,
	UnknownFieldVar,
	FlowHasNoModel,
	PromptXMLLookupFailed,
	AudioMatchXMLLookupFailed,
	InvalidPromptString
	;
	
	
	public static void logError(SRError err, RenderErrors code, String description)
	{
		int codeOffset = 1000; //render errors are in range 1000-1200
		err.logError(codeOffset + code.ordinal(), String.format("{%s} %s", code.toString(),description));
	}
}

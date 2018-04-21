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

package org.speakright.core;


/**
 * All errors in the org.speakright.core package should have an enum value here.
 * @author Ian Rae
 *
 */
public enum CoreErrors {
	UnknownModelMethod,
	UnknownFieldMethod,
	ModelMethodWrongParameters,
	ModelMethodFailed,
	BadInterpreterState,
	GetFirstError,
	ExecuteCaughtOwnException,
	NoContentGenerated,
	RunawayInterpreter,
	EventNotCaught,
	SerializationFailed,
	FlowFieldFailed
	;
	
	
	public static void logError(SRError err, CoreErrors code, String description)
	{
		int codeOffset = 1500; //render errors are in range 1500-1700
		err.logError(codeOffset + code.ordinal(), String.format("{%s} %s", code.toString(),description));
	}

}

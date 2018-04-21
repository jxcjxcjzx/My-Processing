package org.speakright.core;


/**
 * IModelBinder binds user input to values in the model.  When a flow object executes
 * it can add bindings for slots in the SML that the speech platform will return.  If
 * the speech platform returns user input, the bindings are used to assign the user input
 * data to values(s) in the model.
 * 
 * <p>For example, a flow might ask the user for a city name, and tell SpeakRight to bind
 * the SML slot "city" to the model value City (that is, M.City()).
 * @author Ian Rae
 *
 */
public interface IModelBinder {

	/**
	 * Set the flow's model field.  It must be named "M" and be
	 * an IModel-derived class.
	 * @param flow
	 */
	void injectModel(IFlow flow);

	/**
	 * Bind the given slot to a model variable.  The speech rec results of this slot
	 * will be stored in the model variable.
	 * @param flow  Flow object that creates a Question with the given slot.
	 * @param slotName slot name.
	 * @param modelVar model variable (the name of a field, eg "city")
	 */
	void addBinding(IFlow flow, String slotName, String modelVar);

	/**
	 * Inspect the results and bind any model variables that are bound
	 * to slot values in the results.
	 * @param current  currently executing flow object.
	 * @param results  speech rec results (from the voicexml platform).
	 */
	void bind(IFlow current, SRResults results);
	
	/**
	 * Get the model object.  Each instance of a SpeakRight application has a singleton model.
	 * This method is used for serialization.
	 * @return the model object.
	 */
	IModel getModel();
	/** Set the model object.  This method is used after restoring the SpeakRight persistent
	 * state from the previous HTTP request.
	 * 
	 * @param model  the application instance's model
	 */
	void setModel(IModel model);
	
	/**
	 * Get the value of the given model variable.  Uses reflection
	 * to avoid type-dependency on the app-defined model class.
	 * @param methodName name of the model variable (eg. "city")
	 * @return model variable's value, as a string.
	 */
	String getModelValue(String methodName);
	
	/**
	 * Get the value of the given field in the flow object.  Uses reflection
	 * to avoid type-dependency on the app-defined flow class.
	 * @param fieldName name of the field, eg ("m_retryCount")
	 * @return model variable's value, as a string. 
	 */
	String getFieldValue(String fieldName, IFlow flow);

	/**
	 * extracts any errors that the model binder logged.
	 * @param parent  error object to copy error info into.
	 * @return true if any errors have occured.
	 */
	boolean failed(SRError parent);

}
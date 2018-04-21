package org.speakright.core;
import java.io.Serializable;

/**
 * Interface for the application-defined model.  SpeakRight promotes an MVC architecture for
 * speech applications, where a model is used to hold application data.
 * The model is used to share data between flow objects.
 * 
 * Model objects are Serializable so SpeakRight can save & restore state between HTTP requests.
 * 
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public interface IModel extends Serializable {

	public IModelBinder ModelBinder(); 
	public void ModelBinderSet(IModelBinder binder);
}

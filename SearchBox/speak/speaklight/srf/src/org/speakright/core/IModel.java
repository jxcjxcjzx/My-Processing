/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
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

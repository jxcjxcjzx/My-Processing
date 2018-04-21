/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import java.io.Serializable;

import org.speakright.core.render.IFlowRenderer;

/**
 * Interface for the formatters that IModelItem use to
 * render themselves into a prompt.
 * @author Ian Rae
 *
 */
public interface IItemFormatter extends Serializable {

	/**
	 * Return a prompt string that says the value of a model item.
	 * @return
	 */
	String formatItem();
}

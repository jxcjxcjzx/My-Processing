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

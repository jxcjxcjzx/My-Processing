package org.speakright.core;

/**
 * Each model value is a class that implements IModelItem.
 * @author Ian Rae
 *
 */
public interface IModelItem {

	/**
	 * get raw value as object. (for internal use)
	 * @return
	 */
	Object rawValue();
	
	/**
	 * clear the value back to its default, and clear the
	 * 'set' flag.
	 *
	 */
	void clear();

	/**
	 * Has the item be set (via the set method).  Some dialog flows
	 * need to keep iterating until a model item has been set (through user
	 * input and binding).
	 * @return whether the item has been set.
	 */
	boolean isSet();
	
	/**
	 * get the current formatter for rendering this item into a prompt.
	 * Items must initialize themselves with a default renderer (which
	 * may simply return toString)
	 * @return
	 */
	IItemFormatter getFormatter();
	
	/**
	 * Set the formatter of this item.
	 * @param formatter A formatter.
	 */
	void setFormatter(IItemFormatter formatter);
}

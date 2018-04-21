package org.speakright.core;

/**
 * A formatter that isn't bound to a single instance of an IModelItem.
 * A single instance of a 'reusable' formatter can be used on multiple
 * items.
 * 
 * @author IanRaeLaptop
 *
 */
public interface IItemReusableFormatter extends IItemFormatter {

	void setItem(IModelItem item);
}

package org.speakright.core;

/**
 * Context passed to getFirst and getNext.  Allows the flow object
 * to get various aspects of its executing context.
 * @author IanRaeLaptop
 *
 */
public interface IFlowContext {

	/**
	 * get current resource file locations.
	 * @return locations object
	 */
	SRLocations getLocations();
}

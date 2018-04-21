package org.speakright.core;

import java.io.Serializable;


/**
 * Holds the information needed to bind a slot used in a given flow object, with
 * a model variable.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class ModelBindingSpec implements Serializable
{
	public IFlow m_flow;
	public String m_slotName;
	public String m_target; //name of model var, eg "city"
	
	public boolean isSame(ModelBindingSpec other)
	{
		if (m_flow == other.m_flow && m_slotName.equals(other.m_slotName) && m_target.equals(other.m_target)) {
			return true;
		}
		return false;			
	}
}

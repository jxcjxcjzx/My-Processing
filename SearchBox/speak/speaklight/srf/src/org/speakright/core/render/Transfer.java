/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

import org.speakright.core.flows.TransferFlow.TransferType;

/**
 * A call control element that transfers the call.
 * @author IanRaeLaptop
 *
 */
public class Transfer extends FormElement {

	public Prompt m_prompt; //say goodbye or something..
	public String m_destination; //DN of the party to receive the call
	public TransferType m_transferType;
	public int m_connectionTimeout = 20; //seconds

	public Transfer(TransferType type, String destination, Prompt prompt)
	{
		m_transferType = type;
		m_destination = destination;
		m_prompt = prompt;
	}

	/**
	 * used by ST
	 * @return
	 */
	public String getDestination()
	{
		return m_destination;
	}
}

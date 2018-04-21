/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.speakright.core.SRResults;
import org.speakright.core.flows.BasicFlow;

@SuppressWarnings("serial")
public class ValFlow extends BasicFlow {

	public ValFlow(String name) 
	{
		super();
		setName(name);
	}
	@Override public boolean validateInput(String input, SRResults results)
	{
		return (input == "222");
	}
}

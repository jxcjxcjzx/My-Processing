/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.speakright.core.flows.BasicFlow;

@SuppressWarnings("serial")
public class AsyncFlow extends BasicFlow {
	
	public AsyncFlow(String name)
	{
		super();
		setName(name);
	}

	@Override public boolean onComplete()
	{
		return false;
	}
}

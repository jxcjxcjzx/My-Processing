/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.speakright.core.*;
import org.speakright.core.flows.BasicFlow;
import org.speakright.core.flows.LoopFlow;

@SuppressWarnings("serial")
public class AppWithLoop extends BasicFlow {
	MyFlow m_flow1 = new MyFlow("a");
	LoopFlow m_loop = new LoopFlow(3);
	
	MyFlow m_flow2 = new MyFlow("b");
	public Model M;
	
	public AppWithLoop()
	{
		m_loop.add(new MyFlow("e"));
		m_loop.add(new MyFlow("f"));
		add(m_flow1);
		add(m_loop);
		add(m_flow2);
	}

}

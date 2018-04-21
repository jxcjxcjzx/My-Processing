/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.speakright.core.ExitEvent;
import org.speakright.core.IFlow;
import org.speakright.core.SRResults;
import org.speakright.core.ThrowEvent;
import org.speakright.core.flows.BasicFlow;

@SuppressWarnings("serial")
public class App1 extends BasicFlow {
	Login m_flow1 = new Login(); //MyFlow("a");
	MyFlow m_flow2 = new MyFlow("b");
	MyMenu m_menu = new MyMenu();
	
	public Model M;  //can only access in Execute (or after)
	
	public App1()
	{
		add(m_flow1);
		add(m_flow2);
		add(m_menu);
	}
		
	@Override public IFlow onDisconnect(IFlow current, SRResults results)
	{
		return new ExitEvent();
	}
	@Override public IFlow onCatch(IFlow current, SRResults results, String eventName, ThrowEvent event)
	{
		if (eventName == "restart") {
			return m_flow1;
		}
		return null;
	}
	
	
	
	@SuppressWarnings("serial")
	class Login extends BasicFlow {
		MyFlow m_flow1 = new MyFlow("id");
		MyFlow m_flow2 = new MyFlow("pwd");

		public Login()
		{
			add(m_flow1);
			add(m_flow2);
		}
	}

}

/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.speakright.core.AppEvent;
import org.speakright.core.IFlowContext;
import org.speakright.core.flows.ChoiceFlow;



@SuppressWarnings("serial")
public class MyMenu extends ChoiceFlow {

	MyFlow m_ask = new MyFlow("ask");
	MyFlow m_choice1 = new MyFlow("choice1");
	MyFlow m_choice2 = new MyFlow("choice2");
	
	public Model M;

	public MyMenu() {
		super("mymenu");
		setChoiceQuestion(m_ask);
		addChoice("choice1", m_choice1);
		addChoice("choice2", m_choice2);
		addChoice("choice3", new AppEvent("restart"));
		
//		m_choice1.m_addBindingX = true;

	}
	
	@Override
	public void onBegin(IFlowContext context)
	{
		//hack -- we want to turn on data binding if Hack set
		//execute() never called on MyMenu so do in onBegin
		if (M.hack().get() == "1") {
			m_choice1.m_addBinding = true;
		}
		else if (M.hack().get() == "2") {
			m_choice1.m_addBindingX = true;
		}
	}
}

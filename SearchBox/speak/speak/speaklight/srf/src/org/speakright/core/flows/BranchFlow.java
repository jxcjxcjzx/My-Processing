/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.flows;
import java.io.Serializable;

import org.speakright.core.AppEvent;
import org.speakright.core.FlowBase;
import org.speakright.core.IFlow;
import org.speakright.core.IFlowContext;
import org.speakright.core.SRResults;
import org.speakright.core.ThrowEvent;

/**
 * BranchFlow provides branching of the callflow based on an application-defined condition.  Derived classes
 * override the branch method to supply the branching logic.
 * Sub-flows can jump back to the branch flow later by 'throwing' a GotoBranchEvent.
 * <p>
 * Branch flows can loop forever if m_loopForever is set in the constructor.  
 * That is, when the sub-flows end the branch flow executes again.    
 * @author Ian Rae
 */
@SuppressWarnings("serial")
public abstract class BranchFlow extends FlowBase
{
	protected boolean m_loopForever = false;

	/**
	 * Do the branching, as determined by branch()
	 */
	@Override
	public IFlow getFirst(IFlowContext context) {
		IFlow flow = branch();
		return flow;
	}
	
	/**
	 * Derived classes MUST override this and return
	 * a flow object to execute.  
	 * @return the flow object to execute.  Cannot return null.
	 */
	protected abstract IFlow branch();
	
	/**
	 * If m_loopForever set then return this, else null.
	 */
	@Override
	public IFlow getNext(IFlow current, SRResults results) {
		if (m_loopForever) {
			return getFirst(results);
		}
		return null;
	}
	
	/**
	 * Catch a GotoBranchEvent that is targeted at this object, that is, the event's
	 * m_branchName matches our name.  If we catch the event then onCatchGotoBranchEvent is
	 * invoked.   
	 */
	@Override
	public IFlow onCatch(IFlow current, SRResults results, String eventName, ThrowEvent event) 
	{
		log("ddcatching ");
		if (event instanceof GotoBranchEvent) {
			GotoBranchEvent ev = (GotoBranchEvent)event;
			if (ev.m_branchName.equals(this.name())) {
				onCatchGotoBranchEvent(ev);
				return this;
			}
		}
		return null; //didn't catch the event
	}

	/**
	 * Process the caught GotoBranchEvent.  The event's m_action usually contains data that
	 * is used to adjust the condition used by branch().  This lets the sub-flow that threw the event
	 * to control which branch is taken.
	 * @param ev the caught event.
	 */
	protected abstract void onCatchGotoBranchEvent(GotoBranchEvent ev);

	/**
	 * An event class used to jump back from a sub-flow to a branch flow.
	 * Since there may be multiple branch flows in an app, the event includes m_branchName, which
	 * is the name of the flow object to jump to.
	 * An additional m_action field is used to control the branching.  Sub-flows can set this when 'throwing'
	 * this event in in order to control which branch is taken. 
	 * 
	 * @author IanRaeLaptop
	 *
	 */
	@SuppressWarnings("serial")
	public static class GotoBranchEvent extends AppEvent {
		public String m_branchName;
		public String m_action;
		
		public GotoBranchEvent(String branchName, String action) {
			super("GotoBranch." + branchName);
			m_branchName = branchName;
			m_action = action;
		}
		//note. onbegin never called!
		//note. We cannot refer to M in the ctor because it's not injected yet. OnBegin is the first place we can use M.
	}
}


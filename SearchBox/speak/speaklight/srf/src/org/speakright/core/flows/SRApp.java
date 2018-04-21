/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.flows;

import org.speakright.core.AppEvent;
import org.speakright.core.ExitEvent;
import org.speakright.core.IFlow;
import org.speakright.core.IFlowContext;
import org.speakright.core.SRResults;
import org.speakright.core.ThrowEvent;
import org.speakright.core.tests.MyFlow;

/**
 * The base class for the outermost "app" flow object.  It is the application, and
 * it creates sub-flows that make up the callflow.  Sub-flows can of course create
 * their own sub-flows, creating a tree of flow objects with the "app" flow object
 * at the root of the tree.
 * Flow objects share data using a model, which is a field named M in each flow object
 * class.  The model contains application-defined data that needs to be shared among
 * the flow objects.  
 * 
 * This class must handle all possible events, since it's an error for an event to bubble up the flow
 * stack without being handled.
 * 
 * Also implements a default fail approach of transferring to an "operator" -- a
 * live person who can help the caller.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class SRApp extends BasicFlow {
	private LoopFlow m_mainLoop; //optional.  the return value from createMainLoop, so apps can re-run the main loop
	
	public SRApp()
	{}
	public SRApp(String name)
	{
		super();
		setName(name);
	}

	/**
	 * Invokes getWelcome, getMainLoop, and getGoodbye.
	 * If you override this method you MUST call super.OnBegin
	 */
	@Override
	public void onBegin(IFlowContext context) {
		IFlow flow = createWelcome();
		if (flow != null) {
			add(flow);
		}
		m_mainLoop = createMainLoop();
		if (m_mainLoop != null) {
			initMainLoop(m_mainLoop);
			if (m_mainLoop.SubFlowCount() > 0) {
				add(m_mainLoop);
			}
		}
		flow = createGoodbye();
		if (flow != null) {
			add(flow);
		}
		super.onBegin(context);
	}
	
	/**
	 * Return the first flow object, which is usually a welcome prompt.  Can return an entire sub-flow
	 * for welcome + login.
	 * This method is optional. The default version returns null.  If you don't override this method
	 * then you must manually build the flow objects yourself in the constructure.
	 * @return
	 */
	protected IFlow createWelcome()
	{
		return null;
	}
	
	protected LoopFlow createMainLoop()
	{
		LoopFlow loop = new LoopFlow();
		loop.setName("MainLoop");
		return loop;
	}
	/**
	 * Return the main loop sub-flow.  Most apps have an infinite loop that presents a main menu.
	 * You can use the loop param passed in, or create one yourself.  Populate it with flow objects
	 * and return the loop object.
	 * This method is optional. The default version returns null.  If you don't override this method
	 * then you must manually build the flow objects yourself in the constructure.
	 * @return
	 */
	protected void initMainLoop(LoopFlow flow)
	{
	}		
	
	/**
	 * Get the loop flow returned by createMainLoop.  Applications may want to re-run the main loop,
	 * perhaps as part of catching an event.  They can call this method to get the main loop object.
	 * @return
	 */
	protected LoopFlow getMainLoop()
	{
		return m_mainLoop;
	}

	/**
	 * Return the final flow object, that is executed after the main loop exits.  This final flow object
	 * usually is a goodbye prompt.
	 * This method is optional. The default version returns null.  If you don't override this method
	 * then you must manually build the flow objects yourself in the constructure.
	 * @return
	 */
	protected IFlow createGoodbye()
	{
		return null;
	}
	
	/**
	 * Called from onCatch to determine if we're catching a given app-event 
	 * @param event
	 * @param eventName name of the app event
	 * @return true if event matches eventName
	 */
	protected boolean isAppEvent(ThrowEvent event, String eventName) 
	{
		if (event instanceof AppEvent) {
			AppEvent ev = (AppEvent)event;
			if (ev.name().equals(eventName)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * Transfer the call to the operator.  Since there is no standard way
	 * to do this (what DN? blind or bridged? etc)
	 * The default implementation is to do nothing.
	 * Override this method and provide your own transfer.
	 * @return
	 */
	protected IFlow transferToOperator()
	{
//		return new TransferFlow(TransferFlow.TransferType.Bridge, "222", "see ya");
		return new ExitEvent(); 
	}
	
	/**
	 * The default implementation of onDisconnect is to transfer to the operator.
	 */
	public IFlow onDisconnect(IFlow current, SRResults results)
	{
		return new ExitEvent(); //discflow later!!
	}
	/**
	 * The default implementation of onNoInput is to transfer to the operator.
	 */
	public IFlow onNoInput(IFlow current, SRResults results)
	{
		return transferToOperator();
	}
	/**
	 * The default implementation of onCatch is to transfer to the operator.
	 */
	public IFlow onCatch(IFlow current, SRResults results, String eventName, ThrowEvent event)
	{
		return transferToOperator();
	}
	/**
	 * The default implementation of onPlatformError is to transfer to the operator.
	 */
	@Override
	public IFlow onPlatformError(IFlow current, SRResults results) {
		return transferToOperator();
	}
	/**
	 * The default implementation of onTransferFailed is to transfer to the operator.
	 */
	@Override
	public IFlow onTransferFailed(IFlow current, SRResults results) {
		return transferToOperator();
	}
	/**
	 * The default implementation of onValidateFailed is to transfer to the operator.
	 */
	public IFlow onValidateFailed(IFlow current, SRResults results)
	{
		return transferToOperator();
	}
	
}

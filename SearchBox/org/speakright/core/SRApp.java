package org.speakright.core;

/**
 * The base class for the outermost "app" flow object.  This class must handle
 * all possible events, since it's an error for an event to bubble up the flow
 * stack without being handled.
 * 
 * Also implements a default fail approach of transferring to an "operator" -- a
 * live person who can help the caller.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class SRApp extends BasicFlow {

	public SRApp()
	{}
	public SRApp(String name)
	{
		super();
		setName(name);
	}
	
	/**
	 * Transfer the call to the operator
	 * TBD!!
	 * @return
	 */
	IFlow transferToOperator()
	{
		return new ExitEvent(); //discflow later!!
	}
	
	/**
	 * The default implementation of onDisconnect is to return null,
	 * indicating that this object does not handle disconnect events.
	 */
	public IFlow onDisconnect(IFlow current, SRResults results)
	{
		return transferToOperator();
	}
	/**
	 * The default implementation of onNoInput is to return null,
	 * indicating that this object does not handle no-input events.
	 */
	public IFlow onNoInput(IFlow current, SRResults results)
	{
		return transferToOperator();
	}
	/**
	 * The default implementation of onCatch is to return null,
	 * indicating that this object does not handle custom events.
	 */
	public IFlow onCatch(IFlow current, SRResults results, String eventName)
	{
		return transferToOperator();
	}
	/**
	 * The default implementation of onValidateFailed is to return null,
	 * indicating that this object does not handle validate-failed events.
	 */
	public IFlow onValidateFailed(IFlow current, SRResults results)
	{
		return transferToOperator();
	}
	
}

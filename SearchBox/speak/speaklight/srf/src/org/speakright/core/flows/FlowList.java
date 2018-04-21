package org.speakright.core.flows;

import org.speakright.core.AppEvent;
import org.speakright.core.IFlow;

/**
 * A sequence of flow objects.  This is a convenience class that lets you return a set of
 * flow objects in a single line of code such as:
 *   return new FlowList(new PromptList("vote recorded"), "MainMenu")); 
 * 
 * The list can optionally can end with an app event to be thrown.
 * @author IanRaeLaptop
 *
 */
public class FlowList extends BasicFlow {
	public FlowList()
	{}
	public FlowList(IFlow flow1)
	{
		add(flow1);
	}
	public FlowList(IFlow flow1, IFlow flow2)
	{
		add(flow1);
		add(flow2);
	}
	public FlowList(IFlow flow1, IFlow flow2, IFlow flow3)
	{
		add(flow1);
		add(flow2);
		add(flow3);
	}
	public FlowList(IFlow flow1, IFlow flow2, IFlow flow3, IFlow flow4)
	{
		add(flow1);
		add(flow2);
		add(flow3);
		add(flow4);
	}
	
	public FlowList(IFlow flow1, String eventName)
	{
		add(flow1);
		add(new AppEvent(eventName));
	}
	public FlowList(IFlow flow1, IFlow flow2, String eventName)
	{
		add(flow1);
		add(flow2);
		add(new AppEvent(eventName));
	}
	public FlowList(IFlow flow1, IFlow flow2, IFlow flow3, String eventName)
	{
		add(flow1);
		add(flow2);
		add(flow3);
		add(new AppEvent(eventName));
	}
	public FlowList(IFlow flow1, IFlow flow2, IFlow flow3, IFlow flow4, String eventName)
	{
		add(flow1);
		add(flow2);
		add(flow3);
		add(flow4);
		add(new AppEvent(eventName));
	}
}

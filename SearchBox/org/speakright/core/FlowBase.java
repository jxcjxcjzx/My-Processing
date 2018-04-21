package org.speakright.core;
import org.speakright.core.render.IFlowRenderer;
import org.speakright.core.render.ISpeechForm;
import org.speakright.core.render.ISpeechPage;
import org.speakright.core.render.Prompt;
import java.util.*;

/**
 * FlowBase is an adapter for IFlow; it implements all the IFlow interface methods.
 * Most flow objects derive from FlowBase so that they need only to override a few of the
 * IFlow methods.
 * <p>
 * The default behaviour that FlowBase implements is a do-nothing flow object with no
 * sub-flows.  It returns itself in getFirst, and null in getNext, and execute does
 * nothing.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class FlowBase implements IFlow {
	String m_name = "";
	ArrayList<ModelBinder.BindingSpec> m_earlyBindingL = new ArrayList<ModelBinder.BindingSpec>(); 
	
	transient SRLogger m_logger = null; //created on-demand
	
	public FlowBase()
	{
		m_name = this.getClass().getSimpleName();
	}
	public FlowBase(String name)
	{
		m_name = name;
	}
	
	public String name()
	{
		return m_name;
	}
	
	public void log(String message)
	{
		if (m_logger == null) {
			m_logger = SRLogger.createLogger();
		}
		m_logger.log(message);
	}
	public void logError(String message)
	{
		if (m_logger == null) {
			m_logger = SRLogger.createLogger();
		}
		m_logger.logError(message);
	}
	
	/**
	 * Sets the name of the flow object.
	 * @see IFlow#name
	 * @param name
	 */
	public void setName(String name)
	{
		m_name = name;
	}

	/**
	 * Add a model binding.
	 * @param slotName  slot in the grammar that is the value to be bound.
	 * When the speech platform sends back the user input, it sends back
	 * (slot,value) pairs.
	 * @param modelVar name of the model variable to bind to.  This variable
	 * must have get and set methods with the following signatures
	 *   <code>public {type} {modelVar}();</code>  
	 *   <code>public void set{modelVar}({type} value);</code>
	 * Examples:
	 *   <code>public String City();</code>  
	 *   <code>public void setCity(String value);</code>  
	 */
	public void addBinding(String slotName, String modelVar)
	{
		ModelBinder.BindingSpec spec = new ModelBinder.BindingSpec();
		spec.m_flow = this;
		spec.m_slot = slotName;
		spec.m_target = modelVar;
		m_earlyBindingL.add(spec);
	}
	
	/**
	 * Used internally.  Model bindings were originally designed only to work
	 * from within execute().  "Early binding" means binding done earlier, such
	 * as in the flow's constructor.
	 */
	public void processEarlyBindings(IExecutionContext context)
	{
		for(ModelBinder.BindingSpec spec : m_earlyBindingL) {
			context.modelBinder().addBinding(spec.m_flow, spec.m_slot, spec.m_target);
		}		
	}
	

	/**
	 * The default implementation of execute is to render this object.
	 * However the default rendering is a 'silent' voicexml page.
	 */
	public void execute(IExecutionContext context)
	{
		context.render(this);		
	}
	/**
	 * The default implementation of getFirst is to return this. 
	 */
	public IFlow getFirst()
	{
		return this;
	}
	
	/**
	 * The default implementation of getNext is to return null.
	 */
	public IFlow getNext(IFlow current, SRResults results)
	{
		return null;
	}
	
	/**
	 * The default implementation of onBegin is to do nothing.
	 */
	public void onBegin()
	{
		
	}
	/**
	 * The default implementation of onEnd is to do nothing.
	 * 
	 */
	public void onEnd()
	{
		
	}
	
	/**
	 * The default implementation of onDisconnect is to return null,
	 * indicating that this object does not handle disconnect events.
	 */
	public IFlow onDisconnect(IFlow current, SRResults results)
	{
		return null;
	}
	/**
	 * The default implementation of onNoInput is to return null,
	 * indicating that this object does not handle no-input events.
	 */
	public IFlow onNoInput(IFlow current, SRResults results)
	{
		return null;
	}
	/**
	 * The default implementation of onCatch is to return null,
	 * indicating that this object does not handle custom events.
	 */
	public IFlow onCatch(IFlow current, SRResults results, String eventName)
	{
		return null;
	}
	/**
	 * The default implementation of onValidateFailed is to return null,
	 * indicating that this object does not handle validate-failed events.
	 */
	public IFlow onValidateFailed(IFlow current, SRResults results)
	{
		return null;
	}
	
	/**
	 * The default implementation is to return true.
	 */
	public boolean ValidateInput(String input, SRResults results)
	{
		return true;
	}


	/**
	 * The default implementation is to return true;
	 */
	public boolean doTransaction()
	{
		return true;
	}
	
	public IFlowRenderer createRenderer()
	{
		return new FlowBaseRenderer();
	}
	
	/**
	 * an renderer that generates minimal content. we currently require every execute to 
	 * generate some content.  So we'll generate a prompt of silence...
	 * @author Ian Rae
	 *
	 */
	private class FlowBaseRenderer implements IFlowRenderer
	{
		public void Render(ISpeechPage page, ISpeechForm form)
		{
			form.addField(new Prompt(" ")); //silent output
		}
	}

	/**
	 * The default implementation is to do nothing.
	 * @return null
	 */
	public String fixupPrompt(String item)
	{
		return null;
	}
	
}

package org.speakright.core;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * ModelBinder binds user input to values in the model.  When a flow object executes
 * it can add bindings for slots in the SML that the speech platform will return.  If
 * the speech platform returns user input, the bindings are used to assign the user input
 * data to values(s) in the model.
 * 
 * <p>For example, a flow might ask the user for a city name, and tell SpeakRight to bind
 * the SML slot "city" to the model value City (that is, M.City()).
 * @author Ian Rae
 *
 */public class ModelBinder implements IModelBinder {

	public IModel m_model;
	public ArrayList m_L = new ArrayList();
	SRError m_err = new SRError("ModelBinder");
	SRLogger m_logger = SRLogger.createLogger();
	
	/**
	 * Constructor 
	 * @param m  the application's model object.
	 */
	public ModelBinder(IModel m)
	{
		m_model = m;
		m_model.ModelBinderSet(this);
	}
	
	/**
	 * used for serialization
	 *
	 */
	public ModelBinder()
	{
		m_model = null;  
	}
	
	public boolean failed(SRError parent)
	{
		return m_err.failed(parent);
	}
	//for serialization
	public IModel getModel()
	{
		return m_model;
	}
	public void setModel(IModel model)
	{
		m_model = model;
		m_model.ModelBinderSet(this);
	}
	/**
	 * Returns the number of errors that have ocurred.
	 * @return number of errors.
	 */
	
	
	/**
	 * A type-agnostic way to assign a model to a flow object.
	 * Flow classes can either have a model or not.  If they do,
	 * the SpeakRight convention is that the model be a field called
	 * M.  injectModel finds M and assigns it to the binder's model.
	 * <p>
	 * The injection is not done until the flow is executed.  Flow objects
	 * must only reference their M field from within getFirst, getNext,
	 * execute, or an event handler.
	 * <p>
	 * injectModel uses reflection to find and assign to M.  This frees SpeakRight
	 * from having to know about the application-defined Model class.
	 * 
	 * @param flow  the flow to inject the model into.
	 */
	public void injectModel(IFlow flow)
	{
		//do nothing if flow doesn't have M
		SetField("M", flow, m_model);
		
		if (flow instanceof TrailWrapper) {
			TrailWrapper wrap = (TrailWrapper)flow;
			SetField("M", wrap.InnerFlow(), m_model);
		}
	}

	/** adds a binding.  addBinding must only be called from within a flow's
	 * execute method.  More than one binding can be added if the VoiceXML
	 * will be collecting multi-value SML.
	 * @param flow  the flow object currently executing.
	 * @param slotName the SML slot name that will contain the user input value to be bound
	 * @param propName the model property name to bind the data to.
	 */
	public void addBinding(IFlow flow, String slotName, String propName)
	{
		BindingSpec newspec = new BindingSpec();
		newspec.m_flow = flow;
		newspec.m_slot = slotName;
		newspec.m_target = propName;

		//add if not already there
		for (int i = 0; i < m_L.size(); i++) {
			BindingSpec spec = (BindingSpec)m_L.get(i);
			if (newspec.isSame(spec)) {
				return; //already bound, nothing to do
			}
		}
		
		//add it
		m_logger.log("addbind " + flow.name() + "," + propName + ", L=" + m_L.size());
		m_L.add(newspec);	
	}

	/** Looks for and applies any data binding.
	 * @param current  the flow whose results are in <code>results</code>
	 * @param results  the results from executing <code>current</code>'s VoiceXML page.
	 */
	public void bind(IFlow current, SRResults results)
	{
		//hack to get trailwrapper working. is there a better way??
		if (current instanceof TrailWrapper) {
			TrailWrapper wrap = (TrailWrapper)current;
			current = wrap.InnerFlow();
		}
		
		for (int i = 0; i < m_L.size(); i++) {
			BindingSpec spec = (BindingSpec)m_L.get(i);
			if (spec.m_flow == current) {
				if (results.m_slotName.equals(spec.m_slot)) {
					//set m_model.<target> = results.m_input
					String target = fixupName(spec.m_target);
					
					if (!Invoke(target, "set", results.m_input))
					{
					}
					else
					{
						m_logger.log("BIND: " + spec.m_target + " = " + results.m_input);
					}
				}				
			}
		}
	}
	
	/**
	 * change the first letter to lower case so either M.City or M.city will
	 * both work, and find M.city();
	 * @param name
	 * @return
	 */
	String fixupName(String name)
	{
		if (name.length() == 0) {
			return name;
		}
		Character ch = Character.toLowerCase(name.charAt(0));
		return ch.toString() + name.substring(1);
	}
	
	public static class BindingSpec
	{
		public IFlow m_flow;
		public String m_slot;
		public String m_target;
		
		public boolean isSame(BindingSpec other)
		{
			if (m_flow == other.m_flow && m_slot.equals(other.m_slot) && m_target.equals(other.m_target)) {
				return true;
			}
			return false;			
		}
	}
	
	boolean Invoke(String methodName, String itemMethodName, String value)
	{
		//step 1 get the IModelItem object
		IModelItem item = getModelItem(methodName);
		if (item == null) {
			return false;
		}
		
		//step 2 
		Method meth = FindMethod(itemMethodName, item.getClass());
		
		if (meth.getParameterTypes().length != 1) {
			CoreErrors.logError(m_err, CoreErrors.ModelMethodWrongParameters,
					String.format("model method '%s' is supposed to take one parameter", methodName));
			return false; //setXXX must take one param
		}
		Class param = meth.getParameterTypes()[0]; //1st param
		
		meth.setAccessible(true);
		
		//invoke it
		boolean success = false;
		try {
			if (param == int.class) {
				int n = Integer.parseInt(value); //later do safe way!!
				meth.invoke(item, new Object[] { n } );				
			}
			else if (param == boolean.class) {
				int n = Integer.parseInt(value); //later do safe way!!
				boolean b = (n != 0);
				meth.invoke(item, new Object[] { b } );				
			}
			else {
				meth.invoke(item, new Object[] { value } );
			}

			success = true;
		}
		catch(InvocationTargetException e)
		{
			CoreErrors.logError(m_err, CoreErrors.ModelMethodFailed,
					String.format("model method '%s' invoke exception: %s", methodName, e.getLocalizedMessage()));			
		}
		catch(IllegalAccessException e)
		{
			CoreErrors.logError(m_err, CoreErrors.ModelMethodFailed,
					String.format("model method '%s' invoke exception: %s", methodName, e.getLocalizedMessage()));			
		}
		
		return success;
	}
	
	IModelItem getModelItem(String methodName)
	{
		Method meth = FindMethod(methodName, m_model.getClass());
		if (meth == null) {
			CoreErrors.logError(m_err, CoreErrors.UnknownModelMethod,
					String.format("can't find model method '%s'", methodName));
			return null;
		}
		
		//step 1 get the IModelItem object
		IModelItem item = null;
		try {
			Object ret = meth.invoke(m_model, new Object[] { } );
			item = (IModelItem)ret;
		}
		catch(InvocationTargetException e)
		{
			CoreErrors.logError(m_err, CoreErrors.ModelMethodFailed,
					String.format("model method '%s' invoke exception: %s", methodName, e.getLocalizedMessage()));			
		}
		catch(IllegalAccessException e)
		{
			CoreErrors.logError(m_err, CoreErrors.ModelMethodFailed,
					String.format("model method '%s' invoke exception: %s", methodName, e.getLocalizedMessage()));			
		}
		return item;
	}
	
	boolean SetField(String fieldName, IFlow flow, IModel m)
	{
		Field field = null;
		try {
			field = flow.getClass().getField(fieldName);
		}
		catch(NoSuchFieldException e)
		{
			//this is not an error. M is optional in a flow object
//			CoreErrors.logError(m_err, CoreErrors.ModelMethodFailed,
//					String.format("set field '%s' invoke exception: %s", fieldName, e.getLocalizedMessage()));			
			
		}
		if (field == null) {
			return false;
		}
		
		//invoke it
		boolean success = false;
		try {
			field.set(flow, m);
			success = true;
		}
		catch(IllegalAccessException e)
		{
			CoreErrors.logError(m_err, CoreErrors.ModelMethodFailed,
					String.format("set field '%s' invoke exception: %s", fieldName, e.getLocalizedMessage()));			
		}
		
		return success;
	}

	Method FindMethod(String methodName, Class c)
	{
		Method[] ar = c.getMethods();
		for(int i = 0; i < ar.length; i++)
		{
			String s = ar[i].getName();
			if (s.compareTo(methodName) == 0) { //why does == not work?!!
				return ar[i];
			}
		}
		return null;
	}
	Field FindField(String fieldName, Class c)
	{
		Field[] ar = c.getFields() ;
		for(int i = 0; i < ar.length; i++)
		{
			String s = ar[i].getName();
			if (s.compareTo(fieldName) == 0) { //why does == not work?!!
				return ar[i];
			}
		}
		return null;
	}
	
	public String getModelValue(String methodName)
	{
		//step 1 get the IModelItem object
		IModelItem item = getModelItem(fixupName(methodName));
		if (item == null) {
			return "";
		}
		
		//step 2 
		Method meth = FindMethod("get", item.getClass());
		
		if (meth.getParameterTypes().length != 0) {
			CoreErrors.logError(m_err, CoreErrors.ModelMethodWrongParameters,
					String.format("model method '%s' is supposed to take zero parameters", methodName));
			return ""; //setXXX must take one param
		}
		
		meth.setAccessible(true);
				
		//invoke it
		String result = null;
		try {
				Object o = meth.invoke(item, new Object[] { } );
				result = o.toString();
		}
		catch(InvocationTargetException e)
		{
			CoreErrors.logError(m_err, CoreErrors.ModelMethodFailed,
					String.format("model method '%s' invoke exception: %s", methodName, e.getLocalizedMessage()));			
		}
		catch(IllegalAccessException e)
		{
			CoreErrors.logError(m_err, CoreErrors.ModelMethodFailed,
					String.format("model method '%s' invoke exception: %s", methodName, e.getLocalizedMessage()));			
		}
		
		return result;
	}
	
	public String getFieldValue(String fieldName, IFlow flow) 
	{
		//step 1 get the IModelItem object
		Field field = FindField(fieldName, flow.getClass());

		if (field == null) {
			CoreErrors.logError(m_err, CoreErrors.UnknownFieldMethod,
					String.format("can't find flow field '%s'", fieldName));
			return null;
		}
		
//		Class[] ari = field.getClass().getInterfaces();
//		for(Class c : ari)
//		{
//			if (c == IModelItem.class)
//			{
//				return "6y6";
//			}
//		}
		
		field.setAccessible(true);
		String result = "";
		try {
			Object obj  = field.get(flow);
			result = obj.toString();
		}
//		catch(InvocationTargetException e)
//		{
//			CoreErrors.logError(m_err, CoreErrors.FlowFieldFailed,
//					String.format("flow field '%s' invoke exception: %s", fieldName, e.getLocalizedMessage()));			
//		}
		catch(IllegalAccessException e)
		{
			CoreErrors.logError(m_err, CoreErrors.FlowFieldFailed,
					String.format("flow field '%s' invoke exception: %s", fieldName, e.getLocalizedMessage()));			
		}
		
		return result;
	}

	
 }

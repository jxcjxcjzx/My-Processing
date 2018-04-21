package org.speakright.core;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.io.Serializable;


/**
 * FieldBinder uses reflect to get the value of field's (aka. member variables) in a flow
 * object.  Used in prompt texts, like this "What {%subject%}".
 * @author Ian Rae
 *
 */public class FieldBinder {

	SRError m_err = new SRError("FieldBinder");
	SRLogger m_logger = SRLogger.createLogger();
	
	/**
	 * Constructor 
	 */
	public FieldBinder()
	{
	}
	
	public boolean failed(SRError parent)
	{
		return m_err.failed(parent);
	}

	Field FindField(String fieldName, Class c)
	{
		//Class.getFields only returns public fields, so we need to use
		//getDeclaredFields and walk up the inheritance chain
		while(true) {
			Field field = FindFieldInClass(fieldName, c);
			if (field == null) {
				c = c.getSuperclass();
				if (c == null) //no parent?
					return null;
			}
			else
				return field;
		}
	}
	Field FindFieldInClass(String fieldName, Class c)
	{
		Field[] ar = c.getDeclaredFields() ;
		for(int i = 0; i < ar.length; i++)
		{
			String s = ar[i].getName();
			if (s.compareTo(fieldName) == 0) { //why does == not work?!!
				return ar[i];
			}
		}
		return null;
	}

	/**
	 * Get the value of the given fieldName, such as "m_main1Prompt".  If the field
	 * is an IModelItem then the item's formmater is used, otherwise toString is used.
	 * 
	 * @param fieldName name of the field, usually starts with m_
	 * @param flow flow object in which the field is a member (or it can be in
	 * a base class of flow)
	 * @return value of the field
	 */
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
			
			/* if object is a model item the use its formatter */
			if (obj instanceof IModelItem) {
				IItemFormatter fmt = ((IModelItem)obj).getFormatter();
				result = fmt.formatItem();
			}
			else {
				result = obj.toString();
			}
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

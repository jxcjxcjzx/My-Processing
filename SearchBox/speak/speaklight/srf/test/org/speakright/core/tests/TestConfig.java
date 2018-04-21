/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.speakright.core.*;

public class TestConfig extends BaseTest {
	
	@Test public void testPropInSRConfig()
	{
		//must init first
		SRConfig.init(BaseTest.dir, "sample.properties");
		
		String s = SRConfig.getProperty("sampleProperty1");
	    assertEquals("p1", "abc", s);
	    
	    boolean b = SRConfig.getBooleanProperty("sampleBoolean2");
	    assertEquals("b", true, b);
	    b = SRConfig.getBooleanProperty("sampleBoolean3");
	    assertEquals("b", false, b);
	  }
	
	@Test public void testPropOverrides()
	{
		//should init, but since previous unit test did it we don't have to
		
		String s = SRConfig.getProperty("prop1");
	    assertEquals("p1", "", s); //missing

	    SRConfig.overrideProperty("prop1", "toronto");
		s = SRConfig.getProperty("prop1");
	    assertEquals("p1", "toronto", s);
	  }
	
	@Test(expected=IllegalStateException.class) 
	public void testUninit()
	{
		SRConfig.uninit();
		
		//calling getProperty before init will cause exception
		String s = SRConfig.getProperty("prop1");
	    //never get here
	}
}

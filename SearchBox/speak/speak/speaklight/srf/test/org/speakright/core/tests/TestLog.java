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
import org.apache.log4j.*;

public class TestLog extends BaseTest {

	@Test public void test1()
	{
//		 get a logger instance named "com.foo"
		   Logger  logger = Logger.getLogger("com.foo");
		   
//		   BasicConfigurator.configure();
		   String path = dir + "config.lcf";
		   PropertyConfigurator.configure(path);

		     logger.info("Entering application.");
		     logger.debug("2nd msg.");
		  		   
//
//		   // Now set its level. Normally you do not need to set the
//		   // level of a logger programmatically. This is usually done
//		   // in configuration files.
//		   logger.setLevel(Level.INFO);
//
		
	}
}

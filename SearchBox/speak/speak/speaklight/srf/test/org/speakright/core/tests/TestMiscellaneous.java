/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import static org.junit.Assert.assertEquals;


import org.junit.Test;

public class TestMiscellaneous extends BaseTest {

	/**
	 * http://www.myjavaserver.com/signup requires
	 * you to enter a small java program.
	 *
	 */
	@Test public void test1()
	{
//		assertEquals("asf", false, true);
		String[] config = { "/", "MainServlet", "/nav", "NavigationServlet" };
		String uri = "/nav/test";

		String handler = getHandler(config, uri);		
		System.out.println("HANDLER " + handler);
		handler = getHandler(config, "jez");		
		System.out.println("HANDLER " + handler);
		handler = getHandler(config, "/");		
		System.out.println("HANDLER " + handler);
	}
	
	String getHandler(String[] config, String requestUri)
	{
	String uri = requestUri;	
	int i = 0;
	int maxlen = -1;
	String match = "";
	String handler = "";
	for(String s : config) {
		if (i % 2 == 0) {
			System.out.println("url " + s);
			int pos = uri.indexOf(s);
			if (pos == 0) {
				System.out.println("pos! " + pos);
				if (s.length() > maxlen) {
					maxlen = s.length();
					match = s;
					handler = config[i+1];
				}
			}
		}
		else {
			System.out.println("srv " + s);
		}
		i++;
	}
	
	System.out.println("match " + match + " " + maxlen);
	System.out.println("handler " + handler);
	
	if (maxlen > 0) 
	{
		return handler;
	}
	else
	{
		return "RE03F7W";
	}
	}
}

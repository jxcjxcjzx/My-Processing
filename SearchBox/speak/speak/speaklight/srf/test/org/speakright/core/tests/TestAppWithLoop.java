/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.junit.Test;
import org.speakright.core.SRInstance;
import org.speakright.core.TrailWrapper;


public class TestAppWithLoop extends BaseTest {

	@Test public void Run()
	{
		AppWithLoop flow = new AppWithLoop();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = RunIt(wrap1);
//		assertEquals("fail", false, run.isFailed());
//		assertEquals("fin", true, run.isFinished());
//		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "a;e;f;e;f;e;f;b");
		ChkTrail(wrap1, "beg;F;N;N;N;T;end");
	}
}

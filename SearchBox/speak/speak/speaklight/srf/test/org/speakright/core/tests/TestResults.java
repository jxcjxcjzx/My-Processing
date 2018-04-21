/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;
import org.speakright.core.*;
import java.io.Serializable;


public class TestResults extends BaseTest implements Serializable {
	
	Map m_map;
	
	@Test public void cgiParams()
	{
		RawCGIParams params = new RawCGIParams();
		
		m_map = params.getMap();
		assertEquals("count", 0, m_map.size());
//		assertEquals("sdf", false, true);
		
		params.add("p1", "v1");
		chk(0, "p1", "v1");
		params.add("p2", "v2");
		chk(0, "p1", "v1");
		chk(1, "p1", "v2");
		assertEquals("count", 2, m_map.size());
		
		//now lets add p1 again (cgi params can contain duplicates)
		params.add("p1", "v1a");
		chk(0, "p1", "v1a"); //2nd p1 replaces the first
	}
	
	void chk(int index, String param, String value)
	{
		int i = 0;
		for(Object obj: m_map.keySet()) {
			String s = (String)obj;
			String v = (String)m_map.get(s);
			if (index == i)
			{
				assertEquals("val", value, v);
			}
			i++;
		}
	}

	SRResults m_results;
	@Test public void addToResults()
	{
		RawCGIParams params = new RawCGIParams();
		m_map = params.getMap();
		params.add("p1", "v1");
		params.add("p2", "v2");
		
		SRResults results = new SRResults(params);
		m_results = results;
		
		assertEquals("inpt", "v1 v2", results.m_input);
		assertEquals("count", 2, results.slotCount());
		chkSlot(0, "p1", "v1");
		chkSlot(1, "p2", "v2");
	}		

	@Test public void ignoreRes()
	{
		//some cgi params are not slots and should be ignored (or processed differently)
		RawCGIParams params = new RawCGIParams();
		m_map = params.getMap();
		params.add("sr__res", "3");
		
		SRResults results = new SRResults(params);
		m_results = results;
		
		assertEquals("inpt", "", results.m_input);
		assertEquals("count", 0, results.slotCount());
		
		assertEquals("x", SRResults.ResultCode.NOINPUT, results.m_resultCode);
	}
	@Test public void ignoreMode()
	{
		//some cgi params are not slots and should be ignored (or processed differently)
		RawCGIParams params = new RawCGIParams();
		m_map = params.getMap();
		params.add("mode", "html");
		
		SRResults results = new SRResults(params);
		m_results = results;
		
		assertEquals("inpt", "", results.m_input);
		assertEquals("count", 0, results.slotCount());
		
		assertEquals("x", SRResults.ResultCode.SUCCESS, results.m_resultCode);
		
	}		
	

	void chkSlot(int index, String name, String value)
	{
		SRResults.Slot slot = m_results.getIthSlot(index);
		assertEquals("n", name, slot.m_slotName);
		assertEquals("v", value, slot.m_value);

	}
}

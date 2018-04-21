/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;

import java.util.*;
import java.io.*;
import org.antlr.stringtemplate.*;
import org.custommonkey.xmlunit.*;
import org.speakright.core.render.*;



public class TestXML extends XMLTestCase implements StringTemplateErrorListener {
    public TestXML(String name) {
        super(name);
    }
	public static String dir = BaseTest.dir;
 
    public void testForEquality() throws Exception {
        String myControlXML = "<msg><uuid>0x00435A8C</uuid></msg>";
        String myTestXML = "<msg><localId>2376</localId></msg>";
        //assertXMLEqual("comparing test xml to control xml", myControlXML, myTestXML);
        assertXMLNotEqual("test xml not similar to control xml", myControlXML, myTestXML);
    }

	public void testBlock() throws Exception
	{
		PathGroupLoader loader = new PathGroupLoader(dir, this);		
		StringTemplateGroup group = loader.loadGroup("group2");

		StringTemplate t = group.getInstanceOf("block");
		ArrayList L = new ArrayList();
		
		PSpec spec = new PSpec();
		spec.m_itemL.add(PromptItem.CreateTTS("a"));
		spec.m_itemL.add(PromptItem.CreateTTS("box"));
		add(L, spec);
		
		spec = new PSpec();
		spec.m_itemL.add(PromptItem.CreateAudio("b"));
		spec.m_itemL.add(PromptItem.CreatePause(100));
		add(L, spec);
		
		t.setAttribute("promptL", L);
		String str = t.toString();

		chkx(group, str, "ref_block1.vxml");		
	}
	
	void add(ArrayList L, PSpec spec)
	{
		spec.m_isFirst = (L.size() == 0);
		spec.m_count = L.size() + 1;
		L.add(spec);
	}

	void chkx(StringTemplateGroup group, String str, String file)
	{
		StringTemplate thdr = group.getInstanceOf("pageheader");
		StringTemplate tfooter = group.getInstanceOf("pagefooter");
		String xml = thdr.toString() + "\n";
		
		
		String ref = readFile(file);
		xml += str;
		xml += tfooter.toString();
		
		XMLUnit.setIgnoreWhitespace(true);
		try
		{
//			assertXMLEqual("xml1", ref, xml);
	        Diff diff = new Diff(ref, xml);
	        if (! diff.similar()) {
	    		System.out.println("ref:" + ref);
	    		System.out.println(" st:" + xml);
	        	assertTrue("similar " + diff, diff.similar());
		        assertTrue("identical" + diff, diff.identical());
	        }
		}
		catch(Exception e)
		{
    		System.out.println("ref:" + ref);
    		System.out.println(" st:" + xml);
			System.out.println("except: " + e.getLocalizedMessage());
			m_errCount++;
		}
		assertTrue("err", m_errCount == 0);
	}
	
	class PSpec {
		public PSpec() {}
//		public PromptItem m_item;
		ArrayList<PromptItem> m_itemL = new ArrayList<PromptItem>();
		boolean m_isFirst;
		int m_count;
		public ArrayList getItemL() 
		{ 
			return m_itemL; 
		}
		
		public String getCountNot1() 
		{ 
			if (m_isFirst)
			return null;
			
			Integer n = m_count; 
			return n.toString();
		}
	}

	//interface StringTemplateErrorListener
	int m_errCount;
	public void error(String msg, Throwable e)
	{
		m_errCount++;
	}
    
	public void warning(String msg)
	{
		System.out.println("STWARN: " + msg);
	}


	String readFile(String file)
	{
		StringBuilder sb = new StringBuilder();
		try {
			String path = dir + file;
			FileReader rr = new FileReader(path);
			BufferedReader r = new BufferedReader(rr);

			String s;
			while((s = r.readLine()) != null) {
				sb.append(s + "\n");
			}
			r.close();
		}
		catch(java.io.IOException e)
		{}
		return sb.toString();
	}
}

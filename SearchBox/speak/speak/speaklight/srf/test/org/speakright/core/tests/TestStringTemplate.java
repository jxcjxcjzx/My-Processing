/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import org.antlr.stringtemplate.*;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import java.util.*;

public class TestStringTemplate extends BaseTest implements StringTemplateErrorListener {

	@Test public void first()
	{
		StringTemplate hello = new StringTemplate("Hello, $name$");
		hello.setAttribute("name", "World");
		System.out.println(hello.toString());
		chk("Hello, World", hello);
	}
	void chk(String s1, String s2)
	{
		if (! s1.equals(s2)) {
			System.out.println(s1 + "NOT= " + s2);
			assertEquals("s", false, true);
		}
	}
	void chk(String s1, StringTemplate st)
	{
		chk(s1, st.toString());
	}
	
//	@Test public void fromFile()
//	{
//		String path = dir;
//		StringTemplateGroup group = new StringTemplateGroup("myGroup", path);
//		StringTemplate query = group.getInstanceOf("voicexml1");
//		query.setAttribute("person", "joey");
//		query.setAttribute("abc", "threeletters");
////		query.setAttribute("column", "email");
////		query.setAttribute("table", "User");		
//		
//		String s = query.toString();
//		System.out.println(s);
//	}
//	
//	@Test public void usingProperty()
//	{
//		String path = this.dir;
//		StringTemplateGroup group = new StringTemplateGroup("myGroup", path);
//		StringTemplate query = group.getInstanceOf("voicexml2");
//		query.setAttribute("someone", this);
////		query.setAttribute("column", "email");
////		query.setAttribute("table", "User");		
//		
//		String s = query.toString();
//		System.out.println("prop" + s);
//	}
//	public String getPerson()
//	{
//		return "sue";
//	}
//
//	@Test public void conditional()
//	{
//		String path = this.dir;
//		StringTemplateGroup group = new StringTemplateGroup("myGroup", path);
//		StringTemplate query = group.getInstanceOf("voicexml3");
//		query.setAttribute("someone", this);
//		
//		String s = query.toString();
//		System.out.println("COND: " + s);
//
//		m_isMember = true;
//		s = query.toString();
//		System.out.println("COND2: " + s);
//	}
//	boolean m_isMember;
//	public boolean getMember()
//	{
//		return m_isMember;
//	}
//	
	//getStringTemplateErrorListener
	public void 	error(java.lang.String msg, java.lang.Throwable e)
	{
		System.out.println("sterror:" + msg);
	}
    
	public  void 	warning(java.lang.String msg)
	 {
		System.out.println("stwarn:" + msg);
		
	 }
//	           	
//	
//	@Test public void simple()
//	{
////		CommonGroupLoader loader = new CommonGroupLoader("/org/speakright/template", this);
////		CommonGroupLoader loader = new CommonGroupLoader("org/speakright", this);
//		CommonGroupLoader loader = new CommonGroupLoader("org/speakright/core/render/template", this);
////		PathGroupLoader loader = new PathGroupLoader(dir, this);
//		
//		StringTemplateGroup group = loader.loadGroup("group1");
//		
//		StringTemplate t = group.getInstanceOf("able");
////		System.out.println(t);	
//		assertEquals("able", "a b l e", t.toString());
//		
//		t = group.getInstanceOf("xxx");
//		t.setAttribute("someone", this);
//		System.out.println(t);	
//		assertEquals("xxx", "name is sue", t.toString());
//		
//		t = group.getInstanceOf("xxxarg");
//		t.setAttribute("someone", this);
//		t.setAttribute("pname", "person");
//		System.out.println(t);	
//		assertEquals("xxx", "name is sue", t.toString());
//		
//		t = group.getInstanceOf("list2");
//		String[] ar = { "p1", "p2", "p3" };
//		t.setAttribute("names", ar);
//		System.out.println(t);	
//		//assertEquals("xxx", "name is sue", t.toString());
//	}
//	@Test public void prompts()
//	{
////		CommonGroupLoader loader = new CommonGroupLoader(dir, this);
//		PathGroupLoader loader = new PathGroupLoader(dir, this);		
//		StringTemplateGroup group = loader.loadGroup("group1");
//		
//		//single prompt
//		StringTemplate t = group.getInstanceOf("prompt");
//		t.setAttribute("cnt", 2);
//		t.setAttribute("txt", "hello vxml");
//		assertEquals("prompt", "<prompt count=\"2\">hello vxml</prompt>", t.toString());
//
//		//item list
//		t = group.getInstanceOf("prompt");
//		t.setAttribute("cnt", 2);
//		String[] ar = { "ab", "cd", "ef" };
//		t.setAttribute("txt", ar);
//		assertEquals("prompt2", "<prompt count=\"2\">ab cd ef</prompt>", t.toString());
//
////		//prompt list
////		t = group.getInstanceOf("promptlist");
////		t.setAttribute("cnt", 2);
//////		t.setAttribute("txt", ar);
//////		ArrayList L = new ArrayList();
//////		L.add(new PSpec("a"));
//////		L.add(new PSpec("b"));
//////		t.setAttribute("names", L);
////		assertEquals("promptlist", "<prompt count=\"2\">ab cd ef</prompt>", t.toString());
//	}
	
	class PSpec {
		public PSpec(String text) { m_text = text; }
		public String m_text;
		public int m_k;
		public ArrayList getTxt() 
		{ 
			ArrayList L = new ArrayList();
			L.add(m_text);
			L.add("box");
			return L; }
		public String getCnt() { return "4"; }
		
		public boolean getCountIsOne() { return m_k == 1; }
		
	}

//	@Test public void promptlist()
//	{
////		CommonGroupLoader loader = new CommonGroupLoader(dir, this);
//		PathGroupLoader loader = new PathGroupLoader(dir, this);		
//		StringTemplateGroup group = loader.loadGroup("group1");
//		
//		//prompt list
//		StringTemplate t = group.getInstanceOf("promptlist");
////		t.setAttribute("cnt", 2);
////		t.setAttribute("txt", "joe");
//		ArrayList L = new ArrayList();
//		L.add(new PSpec("a"));
//		L.add(new PSpec("b"));
//		t.setAttribute("names", L);
////		assertEquals("promptlist", "<prompt count=\"2\">ab cd ef</prompt>", t.toString());
//	}
//
//	@Test public void promptlist2()
//	{
////		CommonGroupLoader loader = new CommonGroupLoader(dir, this);
//		PathGroupLoader loader = new PathGroupLoader(dir, this);		
//		StringTemplateGroup group = loader.loadGroup("group2");
//		
//		//prompt list
//		StringTemplate t = group.getInstanceOf("prompts");
////		t.setAttribute("cnt", 2);
////		t.setAttribute("txt", "joe");
//		ArrayList L = new ArrayList();
//		L.add(new PSpec("a"));
//		L.add(new PSpec("b"));
//		t.setAttribute("promptL", L);
//		System.out.println("p2: " + t);
////		assertEquals("promptlist", "<prompt count=\"2\">ab cd ef</prompt>", t.toString());
//	}
//
//	@Test public void field()
//	{
//		PathGroupLoader loader = new PathGroupLoader(dir, this);		
//		StringTemplateGroup group = loader.loadGroup("group2");
//		
//		//prompt list
//		StringTemplate t = group.getInstanceOf("field");
////		t.setAttribute("grammarUrl", "def.grmxl");
//		t.setAttribute("dtmfGrammarUrl", "dig.grxml");
//		ArrayList L = new ArrayList();
//		L.add(new PSpec("a"));
//		L.add(new PSpec("b"));
//		t.setAttribute("promptL", L);
//		ArrayList L2 = new ArrayList();
//		L2.add(new PSpec("c"));
//		t.setAttribute("promptL", L);
//		t.setAttribute("silPromptL", L2);
//		t.setAttribute("norecoPromptL", new ArrayList()); //empty L
//		t.setAttribute("nextUrl", "myapp.jsp");
//		t.setAttribute("hasInput", true);
//		System.out.println("field: " + t);
////		assertEquals("field", "<prompt count=\"2\">ab cd ef</prompt>", t.toString());
//	}
	
	/** my blog article got comments that taught me you can say $if(it.xxx)$
	 * 
	 *
	 */
	@Test public void grok()
	{
		PathGroupLoader loader = new PathGroupLoader(dir, this);		
		StringTemplateGroup group = loader.loadGroup("group1");
		
		StringTemplate t = group.getInstanceOf("outer");
//		String[] ar = { "x", "y", "z" };
		ArrayList L = new ArrayList();

		PSpec spec = new PSpec("x");
		spec.m_k = 1;
		L.add(spec);
		L.add(new PSpec("y"));
		spec = new PSpec("z");
		spec.m_k = 1;
		L.add(spec);
		
		t.setAttribute("L", L);
		t.setAttribute("v", "dog");
		System.out.println(t);	
	}
}

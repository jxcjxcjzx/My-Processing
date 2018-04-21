/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.tools.mgen;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.antlr.stringtemplate.CommonGroupLoader;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A tool class to generate the Model.java class for an application, based on its
 * model xml file.
 * 
 * SpeakRight uses code-generation so that the Model class has maximum programmer-friendliness,
 * with full Intellisense on all the model vars.  Each model var becomes an IModelItem field, which
 * has get/set/clear methods.
 * 
 * You should add a java class called MGen.java in your application, that calls this MGenGenerator class.
 * Run MGen.java as a Java application every time you change the model xml file.
 * 
 * @author IanRaeLaptop
 *
 */
public class MGenGenerator  implements StringTemplateErrorListener {

	public String run(String packageName, String xmlPath)
	{
//		StringTemplate hello = new StringTemplate("Hello, $name$");
//		hello.setAttribute("name", "World");
//		log(hello.toString());

		//our stringtemplate template files are in our jar file
		CommonGroupLoader loader = new CommonGroupLoader("org/speakright/tools/mgen/templates", this);
		StringTemplateGroup group = loader.loadGroup("mgen"); //load mgen.stg		
		
		StringTemplate t = group.getInstanceOf("modelclass");
		t.setAttribute("package", packageName); 
		t.setAttribute("className", "Model");
		
		ArrayList<MValue> valueL = readModelValues(xmlPath);
		log("prompts: " + valueL.size());
		t.setAttribute("valueL", valueL);

		ArrayList<String> rawL = readRawValues(xmlPath);
		log("raw: " + rawL.size());
		t.setAttribute("rawL", rawL);

		//get unique list of types for item classes
		LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		for(MValue val : valueL) {
			map.put(val.getItemType(), val.getType());
		}
		log("map: " + map.size());
		
		ArrayList typeL = new ArrayList();
		for(String s : map.keySet()) {
			log(s + " " + map.get(s));
			HashMap tmp = new HashMap();
			tmp.put("class", s);
			tmp.put("vartype", map.get(s));
			
			//defaultval
			String def = "";
			if (s.equals("String")) {
				def = "\"\"";
			}
			else if (s.equals("Int")) {
				def = "0";
			}
			else if (s.equals("Boolean")) {
				def = "false";
			}
			tmp.put("defaultVal", def);
			typeL.add(tmp);
		}
		
		t.setAttribute("typeL", typeL);
		return t.toString();
		
	}
	void log(String msg)
	{
		System.out.println(msg);
	}
	
	//interface StringTemplateErrorListener
	int m_errCount;
	public void error(String msg, Throwable e)
	{
		m_errCount++;
		log("sterr: " + msg);
	}
    
	public void warning(String msg)
	{
		log("stwarn: " + msg);
	}
	
	class MValue
	{
		public String m_name;
		public String m_type;
		public String m_itemType;
		
		public String getName() { return m_name; }
		public String getMVN() { return m_name.toUpperCase(); }
		public String getType() { return m_type; }
		public String getItemType() { return m_itemType; }
	}

	//xml stuff
	ArrayList<MValue> readModelValues(String xmlPath)
	{
		ArrayList<MValue> promptL = new ArrayList<MValue>();
		
//		String dir = "C:\\Source\\Eclipse\\MGen\\src\\org\\speakright\\tools\\";
		String path = xmlPath;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try 
        {
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(path));
	        doc.getDocumentElement().normalize();
	
	        NodeList L = doc.getElementsByTagName("value");
	        
	        for(int i=0; i<L.getLength() ; i++) {
	            Node node = L.item(i);
	            if(node.getNodeType() == Node.ELEMENT_NODE){
	            	Node attr = node.getAttributes().getNamedItem("name");
	            	Node attr2 = node.getAttributes().getNamedItem("type");
	            	
	            	MValue prompt = new MValue();
	            	String s = attr.getTextContent();
	            	//make first char lower-case to conform to java naming convention
	            	if (s.length() > 0) {
	            	    String firstChar = s.substring(0,1).toLowerCase();
	            	    s = firstChar + s.substring(1);	            		
	            	}
	            	prompt.m_name = s;
	            	
	            	s = attr2.getTextContent();
	            	if (s.equals("string")) {
	            		s = "String";
	            	}
	            	prompt.m_type = s;
	            	prompt.m_itemType = fixupName(s);
	            	
	            	promptL.add(prompt);
	            }
	        }
        }

        catch(Exception e) 
        {
        	log("EXCEPTION: " + e.getLocalizedMessage());
//			RenderErrors.logError(m_err, RenderErrors.PromptXMLLookupFailed, 
//					String.format("exception %s: %s", e.getClass().getName(), e.getLocalizedMessage()));
       
        }
        return promptL;
	}

	ArrayList<String> readRawValues(String xmlPath)
	{
		ArrayList<String> rawL = new ArrayList<String>();
		
//		String dir = "C:\\Source\\Eclipse\\MGen\\src\\org\\speakright\\tools\\";
		String path = xmlPath;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try 
        {
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(path));
	        doc.getDocumentElement().normalize();
	
	        NodeList L = doc.getElementsByTagName("raw");
	        
	        for(int i=0; i<L.getLength() ; i++) {
	            Node node = L.item(i);
	            if(node.getNodeType() == Node.ELEMENT_NODE){
	            	String s = node.getTextContent();
	            	rawL.add(s);
	            }
	        }
        }

        catch(Exception e) 
        {
        	log("EXCEPTION: " + e.getLocalizedMessage());
//			RenderErrors.logError(m_err, RenderErrors.PromptXMLLookupFailed, 
//					String.format("exception %s: %s", e.getClass().getName(), e.getLocalizedMessage()));
       
        }
        return rawL;
	}
	
	String fixdownName(String name)
	{
		if (name.length() == 0) {
			return name;
		}
		Character ch = Character.toLowerCase(name.charAt(0));
		return ch.toString() + name.substring(1);
	}
	String fixupName(String name)
	{
		if (name.length() == 0) {
			return name;
		}
		Character ch = Character.toUpperCase(name.charAt(0));
		return ch.toString() + name.substring(1);
	}
}

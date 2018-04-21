/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.tools.srogen;
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
import org.speakright.core.render.PromptType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Generates the generated code for SROs in SpeakRight.
 * 
 * To aid code consistency we use code-generation to create a base class for
 * each SRO.  The base class handles its prompts, where each prompt has a field
 * (m_outOfRangePrompt), a set method (set_outOfRangePrompt), and a prompt id ("id:outOfRange").
 * A prompt xml file ({sroname}_prompts.xml) file is created with the prompt text for each
 * id.  You must copy these xml files into your project (see copy_sro_files.cmd in SampleServlet).
 * 
 * Once the base class is generated (in a 'gen' sub-package), the real SRO class (such as SRONumber)
 * is created by hand in the main sro package.  The real class handles everything that the generated
 * code doesn't, such as validation.
 * 
 * We have a GenSRO.java class in the sro package that uses this SROGenerator object.  Every time
 * we update an SRO xml file, we run GenSRO.java as a Java application to generate the new
 * java and prompt xml files.
 * 
 * @author IanRaeLaptop
 *
 */
public class SROGenerator  implements StringTemplateErrorListener {
	PromptValue m_mostRecentParent = null;
	
	public String generateBaseClass(String sroName, String packageName, String xmlPath)
	{
		//our stringtemplate template files are in our jar file
		CommonGroupLoader loader = new CommonGroupLoader("org/speakright/tools/srogen/templates", this);
		StringTemplateGroup group = loader.loadGroup("srogen"); //load mgen.stg		
		
		StringTemplate t = group.getInstanceOf("genclass");
		t.setAttribute("package", packageName + ".gen");
//		String sroName = "SRONumber";
		t.setAttribute("sroName", sroName);
		t.setAttribute("className", "gen" + sroName);
		String s = readBaseClass(xmlPath);
		t.setAttribute("baseClassName", s);
		t.setAttribute("baseIsSROBaseQuestion", s.equals("BaseSROQuestion"));
		
//		ArrayList<String> promptL = new ArrayList<String>();
//		promptL.add("outOfRange");
//		promptL.add("confirm");
		ArrayList<PromptValue> promptL = readPrompts(xmlPath);
		t.setAttribute("promptL", promptL);
		
		ArrayList<GrammarValue> grammarL = readGrammars(xmlPath);
		t.setAttribute("grammarL", grammarL);
		String slotName = null;
		if (grammarL.size() > 0) {
			GrammarValue gval = grammarL.get(0);
			slotName = gval.m_slotName;
		}
		t.setAttribute("slotName", slotName);
		
		return t.toString();
	}

	
	public String generatePromptsXML(String sroName, String xmlPath)
	{
		//our stringtemplate template files are in our jar file
		CommonGroupLoader loader = new CommonGroupLoader("org/speakright/tools/srogen/templates", this);
		StringTemplateGroup group = loader.loadGroup("srogen"); //load mgen.stg		
		
		StringTemplate t = group.getInstanceOf("genxml");
		t.setAttribute("sroName", sroName);
		
		ArrayList<PromptValue> promptL = readPrompts(xmlPath);
		t.setAttribute("promptL", promptL);
		
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
	
	class PromptValue
	{
		public String m_name;
		public boolean m_declare; //false means this field already defined in a base class
		public String m_text;
		
		//subprompt stuff
		public boolean m_isSubPrompt;
		public String m_promptType;
		public int m_subIndex;
		public String m_condition = ""; //eg "play-once"
		
		public String getName() { return m_name; }
		public boolean getDeclare() { return m_declare; }
		public boolean getIsSubPrompt() { return m_isSubPrompt; }
		public String getPromptType() { return m_promptType; }
		public String getSubIndex() 
		{ 
			return Integer.toString(m_subIndex);
		}
		public String getCondition() { return m_condition; }
		
//		public String getMVN() { return m_name.toUpperCase(); }
		public String getText() { return m_text; }
//		public String getItemType() { return m_itemType; }
	}

	class GrammarValue
	{
		public String m_name;
		public String m_src;
		public String m_slotName;
		
		public String getName() { return m_name; }
		public String getSrc() { return m_src; }
		public String getSlotName() { return m_slotName; }
	}

	//xml stuff
	ArrayList<PromptValue> readPrompts(String xmlPath)
	{
		ArrayList<PromptValue> promptL = new ArrayList<PromptValue>();
		
		String path = xmlPath;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try 
        {
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(path));
	        doc.getDocumentElement().normalize();
	        
	        NodeList L = doc.getElementsByTagName("prompt");
	        int nextSubIndexToUse = 0;
	        
	        for(int i=0; i<L.getLength() ; i++) {
	            Node node = L.item(i);
	            
	            if(node.getNodeType() == Node.ELEMENT_NODE){
	            	Node attr = node.getAttributes().getNamedItem("name");
	            	Node attr2 = node.getAttributes().getNamedItem("def");
	            	
	            	PromptValue prompt = new PromptValue();
	            	prompt.m_name = attr.getTextContent();
	
	            	prompt.m_declare = false;
	            	String s = attr2.getTextContent();
	            	if (s.equals("true")) {
	            		prompt.m_declare = true;
	            	}
	            	
	            	prompt.m_text = MyGetTextContent(node);
	            	
	            	if (isSubPrompt(node)) {
	            		prompt.m_declare = false;
	            		prompt.m_isSubPrompt = true;
	            		++nextSubIndexToUse; //start at 1
	            		prompt.m_subIndex = nextSubIndexToUse;
	            		Node attrPrefix = node.getAttributes().getNamedItem("prefix");
	            		if (attrPrefix != null && attrPrefix.getTextContent().equals("true")) {
		            		prompt.m_subIndex = -1; //only one prefix for now
	            		}
	            		
	            		setPromptType(prompt, node);
	            		
	            		Node attrCond = node.getAttributes().getNamedItem("cond");
	            		if (attrCond != null) {
	            			prompt.m_condition = attrCond.getTextContent();
	            		}
	            	}
	            	else {
	            		m_mostRecentParent = prompt;
	            		nextSubIndexToUse = 0; //reset
	            	}
	            	
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

        fixupPrefixPrompts(promptL);
        return promptL;
	}
	
	void fixupPrefixPrompts(ArrayList<PromptValue> promptL)
	{
		//first count # of prefixes
		int count = 0;
		for(PromptValue prompt : promptL) {
			if (prompt.m_subIndex < 0) {
				count++;
			}
		}
		
		if (count == 0) {
			return;
		}
		
		//now adjust subIndexes to count down toward -1
		int index = -1 * count;
		for(PromptValue prompt : promptL) {
			if (prompt.m_subIndex < 0) {
				prompt.m_subIndex = index;
				index++;
			}
		}
	}

	/**
	 * Can't use getTextContent because it gets text of child nodes as well.
	 * @param node
	 * @return
	 */
	String MyGetTextContent(Node targetNode)
	{
		NodeList L = targetNode.getChildNodes();
		for(int i = 0; i < L.getLength(); i++) {
			Node node = L.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
			{
				String s = node.getTextContent();
				return s.trim();
			}
		}
		return ""; //no text in this node
	}
	
	boolean isSubPrompt(Node node)
	{
        boolean isSubPrompt = false;
        Node parent = node.getParentNode();
        if (parent != null && parent.getNodeName().equals("prompt")) {
        	isSubPrompt = true;
        }
        return isSubPrompt;
	}

	void setPromptType(PromptValue prompt, Node node)
	{
        String parentName = m_mostRecentParent.m_name;
		PromptType type = PromptType.MAIN1;
		if (parentName.equals("main1")) {
			type = PromptType.MAIN1;
		}
		else if (parentName.equals("main2")) {
			type = PromptType.MAIN2;
		}
		else if (parentName.equals("main3")) {
			type = PromptType.MAIN3;
		}
		else if (parentName.equals("silence1")) {
			type = PromptType.SILENCE1;
		}
		else if (parentName.equals("silence2")) {
			type = PromptType.SILENCE2;
		}
		else if (parentName.equals("silence3")) {
			type = PromptType.SILENCE3;
		}
		else if (parentName.equals("silence4")) {
			type = PromptType.SILENCE4;
		}
		else if (parentName.equals("noreco1")) {
			type = PromptType.NORECO1;
		}
		else if (parentName.equals("noreco2")) {
			type = PromptType.NORECO2;
		}
		else if (parentName.equals("noreco3")) {
			type = PromptType.NORECO3;
		}
		else if (parentName.equals("noreco4")) {
			type = PromptType.NORECO4;
		}
		
		prompt.m_promptType = type.toString();
	}
	 
	ArrayList<GrammarValue> readGrammars(String xmlPath)
	{
		ArrayList<GrammarValue> grammarL = new ArrayList<GrammarValue>();
		
		String path = xmlPath;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try 
        {
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(path));
	        doc.getDocumentElement().normalize();
	
	        NodeList L = doc.getElementsByTagName("grammar");
	        
	        for(int i=0; i<L.getLength() ; i++) {
	            Node node = L.item(i);
	            if(node.getNodeType() == Node.ELEMENT_NODE){
	            	Node attr = node.getAttributes().getNamedItem("name");
	            	Node attr2 = node.getAttributes().getNamedItem("src");
	            	
	            	GrammarValue gram = new GrammarValue();
	            	gram.m_name = attr.getTextContent();
	            	gram.m_src = attr2.getTextContent();
	            	gram.m_slotName = getSlot(node);
	            	
	            	grammarL.add(gram);
	            }
	        }
        }

        catch(Exception e) 
        {
        	log("EXCEPTION: " + e.getLocalizedMessage());
//			RenderErrors.logError(m_err, RenderErrors.PromptXMLLookupFailed, 
//					String.format("exception %s: %s", e.getClass().getName(), e.getLocalizedMessage()));
       
        }
        return grammarL;
	}
	
	String getSlot(Node grammarNode)
	{
    	NodeList slotL = grammarNode.getChildNodes();

        for(int i=0; i < slotL.getLength() ; i++) {
            Node node = slotL.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
            	
            	NodeList L = node.getChildNodes();
            	for(int j=0; j < L.getLength(); j++) {
            		Node tmp = L.item(j);
                    if(tmp.getNodeType() == Node.ELEMENT_NODE){
                    	Node attr = tmp.getAttributes().getNamedItem("name");
                    	String slotName = attr.getTextContent();
                    	return slotName; //for now, only use first slot
                    }
            	}
            }
        }
    	return ""; //no slot
	}
	
	ArrayList<String> readSlots(String xmlPath)
	{
		ArrayList<String> slotL = new ArrayList<String>();
		
		String path = xmlPath;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try 
        {
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(path));
	        doc.getDocumentElement().normalize();
	
	        NodeList L = doc.getElementsByTagName("slot");
	        
	        for(int i=0; i<L.getLength() ; i++) {
	            Node node = L.item(i);
	            if(node.getNodeType() == Node.ELEMENT_NODE){
	            	Node attr = node.getAttributes().getNamedItem("name");
//	            	Node attr2 = node.getAttributes().getNamedItem("def");
	            	
	            	String slotName = attr.getTextContent();
	            	slotL.add(slotName);
	            }
	        }
        }

        catch(Exception e) 
        {
        	log("EXCEPTION: " + e.getLocalizedMessage());
//			RenderErrors.logError(m_err, RenderErrors.PromptXMLLookupFailed, 
//					String.format("exception %s: %s", e.getClass().getName(), e.getLocalizedMessage()));
       
        }
        return slotL;
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


	String readBaseClass(String xmlPath)
	{
		String baseClass = "BaseSROQuestion";
		
		String path = xmlPath;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try 
        {
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(path));
	        doc.getDocumentElement().normalize();
	
	        NodeList L = doc.getElementsByTagName("sro");

	        if (L.getLength() > 0) {
	            Node node = L.item(0);
	            if(node.getNodeType() == Node.ELEMENT_NODE){
	            	Node attr = node.getAttributes().getNamedItem("base");
	            	
	            	baseClass = attr.getTextContent();
	            }
	        }
        }

        catch(Exception e) 
        {
        	log("EXCEPTION: " + e.getLocalizedMessage());
//			RenderErrors.logError(m_err, RenderErrors.PromptXMLLookupFailed, 
//					String.format("exception %s: %s", e.getClass().getName(), e.getLocalizedMessage()));
       
        }
        return baseClass;
	}
	
}

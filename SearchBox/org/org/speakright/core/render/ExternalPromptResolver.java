package org.speakright.core.render;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.speakright.core.SRError;
import org.speakright.core.SRLocations;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Finds and loads prompt XML files.  This may become an extension point in the future if people need
 * to do more than simply read local XML files.
 * @author IanRaeLaptop
 *
 */
public class ExternalPromptResolver {
	
	SRLocations m_locations;
	SRError m_err = new SRError("promptresolver");
	public ArrayList<String> m_promptFileL;

	
	public ExternalPromptResolver(SRLocations locations, ArrayList<String>promptFileL)
	{
		m_locations = locations;
		m_promptFileL = promptFileL;
	}

	/**
	 * Lookup prompt id
	 * @param id a prompt id (without the "id:" prefix)
	 * @return null if not found, or the prompt text found for that id.
	 */
	public String lookup(String id)
	{
		for(String path : m_promptFileL) {
			String s = doLookup(id, path);
			if (s != null) {
				return s;
			}
		}
		return null;
		
	}
	
	
	String doLookup(String id, String path)
	{
		System.out.println("prompt id lookup in: " + path);
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try 
        {
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(path));
	        doc.getDocumentElement().normalize();
	
	        NodeList L = doc.getElementsByTagName("prompt");
	        //int totalPersons = L.getLength();
//	        log("xxxTotal no of prompts : " + totalPersons);
	        
	        for(int i=0; i<L.getLength() ; i++) {
	            Node node = L.item(i);
	            if(node.getNodeType() == Node.ELEMENT_NODE){
	            	Node attr = node.getAttributes().getNamedItem("id");
	            	
	            	if (attr.getTextContent().equals(id)) {
	            		return node.getTextContent();
	            	}
	            }
	        }
        }

        catch(Exception e) 
        {
			RenderErrors.logError(m_err, RenderErrors.PromptXMLLookupFailed, 
					String.format("exception %s: %s", e.getClass().getName(), e.getLocalizedMessage()));
       
        }

		return null;
	}
}

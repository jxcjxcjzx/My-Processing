/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.speakright.core.SRError;
import org.speakright.core.SRLocations;
import org.speakright.core.SRLogger;
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
	SRLogger m_logger;
	
	public ExternalPromptResolver(SRLocations locations, ArrayList<String>promptFileL, SRLogger logger)
	{
		m_locations = locations;
		m_promptFileL = promptFileL;
		m_logger = logger;
	}

	/**
	 * Lookup prompt id
	 * @param id a prompt id (without the "id:" prefix)
	 * @return null if not found, or the prompt text found for that id.
	 */
	public String lookup(String id)
	{
		//do in reverse order so check the per-execute files first, then
		//the permanent ones
//		for(String path : m_promptFileL) {
		for(int i = m_promptFileL.size() - 1; i >= 0; i--) {
			String path = m_promptFileL.get(i);
			
			path = m_locations.resolvePath(path);
			String s = doLookup(id, path);
			
			boolean bLog = false; //change to true to log results
			if (bLog) {
				m_logger.log("epr: " + id + "," + path + "->" + s);
			}
			if (s != null) {
				return s;
			}
		}
		return null;
		
	}
	
	
	String doLookup(String id, String path)
	{
//		System.out.println("prompt id lookup in: " + path);
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

/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;
import java.io.File;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.speakright.core.SRError;
import org.speakright.core.SRLocations;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Audio matcher checks in an XML file for audio file replacements for TTS text.
 * An app will often send a list of phrases off for recording by professional voice talent.
 * When the audio files are received back, you only need to make an audiomatch.xml file
 * with each tts phrase and its audio file.  No changes to the flow objects are required.
 * SpeakRight's prompt rendering will use AudioMatcher to find and replace any phrases in
 * audiomatch.xml with their audio files.
 * This is done as the last step of prompt rendering, so any TTS, even from evaluated items
 * such as {$M.city} can be replaced.
 * Replacement is done at the item level; you can't do replacements across the boundary of
 * ptext items.  
 * @author IanRaeLaptop
 *
 */
public class AudioMatcher {

	SRLocations m_locations;
	SRError m_err = new SRError("audiomatcher");
	
	public AudioMatcher(SRLocations locations)
	{
		m_locations = locations;
	}

	public String lookup(String textToMatch)
	{
		String dir = m_locations.projectDir(); //  "C:\\Source\\Eclipse\\SpeakRight\\src\\org\\speakright\\core\\tests\\testfiles\\";
		String path = dir + "audiomatch.xml";
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try 
        {
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(path));
	        doc.getDocumentElement().normalize();
	
	        NodeList L = doc.getElementsByTagName("match");
	        //int totalMatches = L.getLength();
//	        log("xxxTotal no of prompts : " + totalMatches);
	        
	        for(int i=0; i<L.getLength() ; i++) {
	            Node node = L.item(i);
	            if(node.getNodeType() == Node.ELEMENT_NODE){
	            	Node attr = node.getAttributes().getNamedItem("file");
	            	String text = node.getTextContent();
	            	
	            	if (doSoftMatch(text, textToMatch)) {
	            		return "audio:" + attr.getTextContent(); //file
	            	}
	            }
	        }
        }

        catch(Exception e) 
        {
			RenderErrors.logError(m_err, RenderErrors.AudioMatchXMLLookupFailed, 
					String.format("exception %s: %s", e.getClass().getName(), e.getLocalizedMessage()));
       
        }

		return null;
	}
	
	boolean doSoftMatch(String text, String textToMatch)
	{
		text = text.toLowerCase().trim();
		textToMatch = textToMatch.toLowerCase().trim();
		
		//better split later!!
		StringTokenizer tok1 = new StringTokenizer(text);
		StringTokenizer tok2 = new StringTokenizer(textToMatch);
		
		if (tok1.countTokens() != tok2.countTokens()) {
			return false;
		}
		

		for(int i = 0; i < tok1.countTokens(); i++) {
			String s1 = tok1.nextToken();
			String s2 = tok2.nextToken();
			if (! s1.equals(s2))
				return false;	
		}
		return true;
	}
}

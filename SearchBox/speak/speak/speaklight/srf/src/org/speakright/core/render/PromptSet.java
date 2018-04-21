/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;
import java.util.ArrayList;
import java.io.Serializable;
import org.speakright.core.SRLogger;

/**
 * A set of prompts to be used for a question.  Can contain one of
 * each prompt type.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class PromptSet extends FormElement implements Serializable {

	ArrayList<Prompt> m_L = new ArrayList<Prompt>();
	
	public void add(Prompt prompt)
	{
		Prompt existing = find(prompt.m_type, prompt.m_subIndex);
		if (existing != null) {
			m_L.remove(existing); //only one of each type
		}
		m_L.add(prompt);
	}
	
	public void addIf(boolean b, Prompt prompt)
	{
		if (b) {
			add(prompt);
		}
	}
	
	public Prompt find(PromptType type, int subIndex)
	{
		for(Prompt prompt : m_L) {
			if (prompt.m_type == type && prompt.m_subIndex == subIndex)
				return prompt;
		}
		return null;
	}
	
	public ArrayList findAll(PromptType.Family family)
	{
		ArrayList L = new ArrayList();
		for(Prompt prompt : m_L) {
			if (PromptType.Family.getFamily(prompt.m_type) == family)
				L.add(prompt);
		}
		return L;
	}
	
	public void renderPrompts(PromptPipeline pipeline)
	{
		for(Prompt prompt : m_L) {
			pipeline.render(prompt);
		}
		
		//now combine all sub-indexes, so for instance, all type.MAIN1 prompt items end up in the itemL of
		//subIndex 0.
		for(Prompt prompt : m_L) {
			if (prompt.m_subIndex == 0) {
				loadItemsFromOtherSubIndexes(prompt);
			}
		}
	}
	
	void loadItemsFromOtherSubIndexes(Prompt promptToLoad)
	{
		PromptType type = promptToLoad.m_type;

		//first do the prefixes (which are negative subIndex values)
		int subIndex = -1; 
		while(subIndex > -4) { //max of 4 prefixes
			Prompt prompt = find(type, subIndex);
			if (prompt == null) {
				break; //we're done
			}
			else
			{
				SRLogger logger = SRLogger.createLogger();
				logger.log(String.format("zloading %d items from %s subIndex %d", prompt.m_itemL.size(), type, subIndex));
				//add prompt first				
				promptToLoad.m_itemL.addAll(0, prompt.m_itemL);
				prompt.m_itemL.clear();
				
				subIndex--; //look for next one
			}
		}
		
		//now do postfixes
		subIndex = 1;
		while(true) {
			Prompt prompt = find(type, subIndex);
			if (prompt == null) {
				break; //we're done
			}
			else
			{
				SRLogger logger = SRLogger.createLogger();
				logger.log(String.format("zloading %d items from %s subIndex %d", prompt.m_itemL.size(), type, subIndex));
				promptToLoad.m_itemL.addAll(prompt.m_itemL);
				prompt.m_itemL.clear();
				
				subIndex++; //look for next one
			}
		}
	}
	
	public ArrayList<Prompt> prompts()
	{
		return m_L;
	}
}

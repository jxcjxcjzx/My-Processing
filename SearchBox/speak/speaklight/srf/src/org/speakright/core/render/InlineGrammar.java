/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

import java.io.Serializable;
import java.util.*;

/**
 * An inline grammar is rendered as part of the VoiceXML page.
 * InlineGrammar uses a simplified GSL format.
 * For advanced grammars, use external grammar files.
 * 
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class InlineGrammar extends Grammar implements Serializable {

	ArrayList<String> m_wordL = new ArrayList<String>();  //lists of words, or groups, or any-of sets

	/**
	 * Create inline grammar.  
	 * @param gText simplified GSL such as "miami atlanta (new york) [boston beantown]"
	 */
	public InlineGrammar(String gtext)
	{
		buildWordList(gtext);
	}
	
	public ArrayList<String> wordList()
	{
		return m_wordL;
	}
	
	
	enum State {
		NORMAL,
		INGROUP, //inside a ( .. )
		INANY //inside a [ .. ]
	}
	void buildWordList(String gtext)
	{
		String words = gtext;
		String[] ar = words.split(" ");
		
		State state = State.NORMAL;
		String any = "";
		String group = "";
		for (int i = 0; i < ar.length; i++) {
			String s = ar[i];
			boolean addedYet = false;
			if (s.length() > 0) {
				
				//note that [xxx] is valid as well as [xxx yyy]
				if (s.startsWith("[")) {
					state = State.INANY;
					any = ""; //reset
				}
				if (s.endsWith("]")) {
					any += " " + s;
					addedYet = true;
					m_wordL.add(any.trim());
					state = State.NORMAL;
				}
				
				//note that (xxx) is valid as well as (xxx yyy)
				if (s.startsWith("(")) {
					state = State.INGROUP;
					group = ""; //reset
				}
				if (s.endsWith(")")) {
					group += " " + s;
					addedYet = true;
					m_wordL.add(group.trim());
					state = State.NORMAL;
				}

				switch(state)
				{
				case NORMAL:
					if (! addedYet) {
						m_wordL.add(s.trim());
					}
					break;
				case INANY:
					any += " " + s;
					break;
				case INGROUP:
					group += " " + s;
					break;
				}
			}
		}
	}
	
}

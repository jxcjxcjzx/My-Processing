/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;
import java.util.*;

/**
 * Represents a VoiceXML grammar.  GrammarItems are what the
 * grammar pipeline produces.
 * This is a helper class that is passed to StringTemplate templates.
 * @author IanRaeLaptop
 *
 */
public class GrammarItem {
	public enum ItemType
	{
		URL,
		BUILTIN,
		INLINE,
	}
	
	String m_url = ""; //url or builtin
	ArrayList<String> m_wordL = new ArrayList<String>();
	ItemType m_itemType = ItemType.URL;
	public GrammarType m_gramType = GrammarType.VOICE;
	
	private GrammarItem()
	{}
	
	public ItemType getItemType()
	{
		return m_itemType;
	}
	static public GrammarItem CreateURL(String url)
	{
		GrammarItem item = new GrammarItem();
		item.m_url = url;
		item.m_itemType = ItemType.URL;
		return item;
	}
	static public GrammarItem CreateBuiltIn(String builtIn)
	{
		GrammarItem item = new GrammarItem();
		item.m_url = builtIn;
		item.m_itemType = ItemType.BUILTIN;
		return item;
	}
	static public GrammarItem CreateInline(ArrayList wordL)
	{
		GrammarItem item = new GrammarItem();
		item.m_wordL.addAll(wordL);
		item.m_itemType = ItemType.INLINE;
		return item;
	}
	static public GrammarItem CreateInline(String words)
	{
		InlineGrammar tmp = new InlineGrammar(words);
		GrammarItem item = new GrammarItem();
		item.m_wordL.addAll(tmp.wordList());
		item.m_itemType = ItemType.INLINE;
		return item;
	}
	
	public String getUrl()
	{
		return (m_itemType == ItemType.URL) ? m_url: null;
	}
	public boolean isGRXML()
	{
		if (m_itemType != ItemType.URL)
			return false;
		int pos = m_url.toLowerCase().indexOf(".grxml");
		return (pos >= 0);
	}
	public boolean isGSL()
	{
		if (m_itemType != ItemType.URL)
			return false;
		int pos = m_url.toLowerCase().indexOf(".gsl");
		int pos2 = m_url.toLowerCase().indexOf(".grammar");
		return (pos >= 0 || pos2 >= 0);
	}
	public String getBuiltIn()
	{
		return (m_itemType == ItemType.BUILTIN) ? m_url: null;
	}
	public ArrayList getInline()
	{
		return (m_itemType == ItemType.INLINE) ? m_wordL: null;
	}
	
	public boolean getIsDTMF()
	{
		return (m_gramType == GrammarType.DTMF);
	}
}

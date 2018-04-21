package org.speakright.core.render;

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
	}
	
	String m_url = ""; //url or builtin 
	ItemType m_itemType = ItemType.URL;
	
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
	
	public String getUrl()
	{
		return (m_itemType == ItemType.URL) ? m_url: null;
	}
	public String getBuiltIn()
	{
		return (m_itemType == ItemType.BUILTIN) ? m_url: null;
	}
}

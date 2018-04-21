/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

/**
 * Represents a VoiceXML prompt tag.  PromptItems are what the
 * prompt pipeline produces.
 * This is a helper class that is passed to StringTemplate templates.
 * @author IanRaeLaptop
 *
 */
public class PromptItem {
	public enum ItemType
	{
		TTS,
		AUDIO,
		PAUSE,
		RAW
	}
	
	String m_text;
	ItemType m_itemType;
	int m_pauseMsec;
	boolean m_bargeIn;
	
	private PromptItem()
	{}
	
	public String toString()
	{
		if (m_itemType == ItemType.PAUSE) {
			return this.getPause();
		}
		return m_text;
	}
	
	public ItemType getItemType()
	{
		return m_itemType;
	}
	static public PromptItem CreateTTS(String tts)
	{
		PromptItem item = new PromptItem();
		item.m_text = tts;
		item.m_itemType = ItemType.TTS;
		return item;
	}
	static public PromptItem CreateRaw(String raw)
	{
		PromptItem item = new PromptItem();
		item.m_text = raw;
		item.m_itemType = ItemType.RAW;
		return item;
	}
	static public PromptItem CreateAudio(String url)
	{
		PromptItem item = new PromptItem();
		item.m_text = url;
		item.m_itemType = ItemType.AUDIO;
		return item;
	}
	static public PromptItem CreatePause(int msecs)
	{
		PromptItem item = new PromptItem();
		item.m_text = "";
		item.m_itemType = ItemType.PAUSE;
		item.m_pauseMsec = msecs;
		return item;
	}
	
	public String getTts()
	{
		return (m_itemType == ItemType.TTS) ? m_text : null;
	}
	public String getRaw()
	{
		return (m_itemType == ItemType.RAW) ? m_text : null;
	}
	public String getAudio()
	{
		return (m_itemType == ItemType.AUDIO) ? m_text : null;
	}
	public String getPause()
	{
		Integer k = m_pauseMsec;
		return (m_itemType == ItemType.PAUSE) ? k.toString() : null;
	}
}

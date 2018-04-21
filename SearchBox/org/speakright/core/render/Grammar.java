package org.speakright.core.render;

import java.io.Serializable;
import org.speakright.core.SRLocations;

/**
 * Represent a VoiceXML grammar.
 * 
 * Currently only external grxml URLs and builtin grammars are supported.
 * Later we'll support in-line and GSL grammars.
 *   
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class Grammar extends FormElement implements Serializable {
	String m_value = ""; //url or builtin:xxxx

	//itemlist..
	transient public GrammarItem m_item; //rendered
	
	public Grammar() {
	}
	public Grammar(String gramUrl) 
	{
		m_value = gramUrl;
	}
	
	public String Url()
	{
//		if (m_type != GrammarType.URL) return "";
		return m_value;
	}
	
	public String BuiltIn()
	{
//		if (m_type != GrammarType.BUILTIN) return "";
		return m_value;
	}
	
	@Override
	public void renderGrammars(GrammarPipeline pipeline)
	{
		pipeline.render(this);
	}
}

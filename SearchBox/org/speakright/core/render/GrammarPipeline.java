package org.speakright.core.render;

import java.util.ArrayList;

import org.speakright.core.SRError;
import org.speakright.core.SRLocations;

/**
 * Manages all the steps in converting a SpeakRight notion of a grammar into
 * a VoiceXML notion of a grammar.
 * 
 * Currently the steps are to apply any grammar condition and then make URLs
 * into absolute urls.
 * 
 * @author IanRaeLaptop
 *
 */
public class GrammarPipeline {

	SRError m_err = new SRError("GrammarPipeline"); //tracks our errors
	SRLocations m_locations;
	RenderContext m_rcontext;
	
	public GrammarPipeline(RenderContext rcontext) 
	{
		m_rcontext = rcontext;
		m_locations = rcontext.m_locations;
	}
	
	public boolean failed(SRError parent)
	{
		return m_err.failed(parent);
	}
	
	public void render(Grammar grammar)
	{
		grammar.m_item = null; //clear
		
//		//first apply the server-side condition, which
//		//may disable the grammar from being played
//		if (! grammar.applyCondition(m_rcontext)) {
//			m_rcontext.m_logger.log(String.format("%s: skipping grammar: %s", m_rcontext.m_flow.name(), grammar.text()));
//			return;
//		}
		
		String value = grammar.m_value;
		
		//step 3. evaluate value items
		if (isBuiltIn(value)) {
			String tmp = value.substring(8); //remove builtin:
			m_rcontext.m_logger.log("BUILTIN: " + tmp);
			GrammarItem item = GrammarItem.CreateBuiltIn(tmp);
			grammar.m_item = item;
		}
		else {
			String url = SRLocations.makeFullUrl(m_rcontext.m_baseGrammarUrl, grammar.m_value);
			GrammarItem item = GrammarItem.CreateURL(url);
			grammar.m_item = item;
		}
		
	}

	boolean isBuiltIn(String item)
	{
		return item.indexOf("builtin:") == 0;
	}
}

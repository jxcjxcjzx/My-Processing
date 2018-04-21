package org.speakright.core.render;

import java.util.ArrayList;

import org.speakright.core.IFlow;
import org.speakright.core.IModelBinder;
import org.speakright.core.ModelBinder;
import org.speakright.core.ModelBindingSpec;
import org.speakright.core.SRError;
import org.speakright.core.SRLocations;
import org.speakright.core.TrailWrapper;
import org.speakright.core.render.InlineGrammar;

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
	
	void log(String message)
	{
		m_rcontext.m_logger.log(message);
	}
	
	/**
	 * step 1. check condition and don't render this grammar if condition is false
	 * step 2. add any model bindings
	 * step 3. create inline or builtin grammars
	 * step 4. resolve urls into full urls
	 * @param grammar
	 */
	public void render(Grammar grammar)
	{
		grammar.m_item = null; //clear
		
		//first apply the server-side condition, which
		//may disable the grammar from being played
		if (! grammar.applyCondition(m_rcontext)) {
			m_rcontext.m_logger.log(String.format("%s: skipping grammar: %s", m_rcontext.m_flow.name(), grammar.gtext()));
			return;
		}
		
		//now let the app have one last chance to modify the grammar or grammar item
		Grammar tmpGram = m_rcontext.m_grammarAdjuster.fixupGrammar(grammar);
		if (tmpGram != null) {
			grammar = tmpGram; 
		}
		
		//step 2. add bindings
		ArrayList<ModelBindingSpec> bindingL = grammar.bindingList();
		if (bindingL.size() > 0) {
			log("grampipeline: bindings " + bindingL.size());
		}
		for(ModelBindingSpec spec : bindingL) {
			IModelBinder binder = m_rcontext.m_binder;

			IFlow flow = m_rcontext.m_flow;
			if (flow instanceof TrailWrapper) {
				TrailWrapper wrap = (TrailWrapper)flow;
				flow = wrap.InnerFlow();
			}
			
			binder.addBinding(flow, spec.m_slotName, spec.m_target);
		}		
		
		String value = grammar.m_gtext;
		
		//step 3. evaluate value items
		if (isBuiltIn(value)) {
			String tmp = value.substring(8); //remove builtin:
			m_rcontext.m_logger.log("BUILTIN: " + tmp);
			GrammarItem item = GrammarItem.CreateBuiltIn(tmp);
			grammar.m_item = item;
		}
		else if (isInline(value)) {
			String tmp = value.substring(7); //remove inline:
			m_rcontext.m_logger.log("INLINE: " + tmp);
			GrammarItem item = GrammarItem.CreateInline(tmp);
			grammar.m_item = item;
		}
		else if(grammar instanceof InlineGrammar) {
			InlineGrammar inline = (InlineGrammar)grammar;
			GrammarItem item = GrammarItem.CreateInline(inline.m_wordL);
			grammar.m_item = item;
			m_rcontext.m_logger.log("INLINE: " + item.m_wordL.size() + " phrases.");
		}
		else {
			String url = m_rcontext.m_locations.makeFullGrammarUrl(grammar.m_gtext);
			GrammarItem item = GrammarItem.CreateURL(url);
			grammar.m_item = item;
		}

		if (grammar.m_item != null) {
			grammar.m_item.m_gramType = grammar.type();
		}
	}

	boolean isBuiltIn(String item)
	{
		return item.indexOf("builtin:") == 0;
	}
	boolean isInline(String item)
	{
		return item.indexOf("inline:") == 0;
	}
}

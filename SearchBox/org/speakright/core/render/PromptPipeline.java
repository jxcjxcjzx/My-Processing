package org.speakright.core.render;
import java.util.ArrayList;

import org.speakright.core.IModelBinder;
import org.speakright.core.IFlow;
import org.speakright.core.SRError;
import org.speakright.core.SRLocations;

/**
 * Manages all the steps in converting a SpeakRight notion of a prompt into
 * a VoiceXML notion of a prompt.
 * 
 * Currently the steps are to apply any grammar condition and then:
 * 	* lookup any id: items and convert them into their prompt text values
 *  * evaluate model values or field values
 *  * call the fixup handlers
 *  * merge runs of contiguous TTS into single items
 *  * generate the appropriate promptitem
 * 
 * @author IanRaeLaptop
 *
 */
public class PromptPipeline {

	ExternalPromptResolver m_resolver;
	AudioMatcher m_audioMatcher;
	SRError m_err = new SRError("PromptPipeline"); //tracks our errors
	SRLocations m_locations;
	RenderContext m_rcontext;
	
	public PromptPipeline(RenderContext rcontext) 
	{
		m_rcontext = rcontext;
		m_locations = rcontext.m_locations;
		m_resolver = new ExternalPromptResolver(m_locations, rcontext.m_promptFileL);
		m_audioMatcher = new AudioMatcher(m_locations);
	}
	
	public boolean failed(SRError parent)
	{
		return m_err.failed(parent);
	}
	
	public void render(Prompt prompt)
	{
		if (prompt.m_itemL == null) {
			prompt.m_itemL = new ArrayList<PromptItem>(); //rendered prompt goes here
		}
		else {
			prompt.m_itemL.clear();
		}
		
		//first apply the server-side condition, which
		//may disable the prompt from being played
		if (! prompt.applyCondition(m_rcontext)) {
			m_rcontext.m_logger.log(String.format("%s: skipping prompt: %s", m_rcontext.m_flow.name(), prompt.text()));
			return;
		}
		//can contain
		//"audio:xxx"
		//"id:xxx"
		//"raw:xxx"
		//"$M.xxx"
		//..  for pause, each . is 250 msec
		//"any other text is tts"
		//can combine using { }
				
		//step 1. break into items
		ArrayList L = resolveIds(prompt.text());
		m_rcontext.m_logger.logDebug(String.format("prompt (%d items): %s", L.size(), m_rcontext.m_logger.limitString(prompt.text(), 80)));
				
		//now all ids removed, only raw,tts,audio,value,pause items left
		for(int i = 0; i < L.size(); i++) {
			String item = (String)L.get(i);
			item = item.trim();
			L.set(i, item); //save trimmed version
						
			//detect audio items without the prefix
			if (! isAudio(item) && ! isPause(item) && !isRaw(item)) {
				String tmp = addAudioPrefixIfNeeded(item);
				if (tmp != null) {
					L.set(i, tmp); //replace
				}
			}
			//step 3. evaluate value items
			if (item.indexOf("$M.") == 0) {
				String modelVar = item.substring(3);
				
				item = evaluate(modelVar);
				L.set(i, item); //replace
				//later maybe support eval returning id. for now assume its a single item audio or tts!!
			}
			else if (item.startsWith("%") && item.endsWith("%")) {
				String fieldVar = "m_" + item.substring(1, item.length() - 1);
				
				item = evaluateField(fieldVar, m_rcontext.m_flow);
				L.set(i, item); //replace
				//later maybe support eval returning id. for now assume its a single item audio or tts!!
			}
			
			//now only raw,tts,audio,pause left
			//step 4. call fixup handlers (walks call stack)
			if (! isAudio(item) && ! isPause(item) && !isRaw(item)) {
				String tmp = m_rcontext.m_promptAdjuster.fixupPrompt(item);
				if (tmp != null) {
					L.set(i, tmp); //replace
				}
			}
		}
			
		//still only raw,tts,audio,pause left
		//step 5. merge runs of tts
		L = mergeRuns(L);
		
//		//step 6. match tts with audio!!
//		for(int i = 0; i < L.size(); i++) {
//			String item = (String)L.get(i);
//						
//			if (! isAudio(item) && !isPause(item) && !isRaw(item)) { //tts?
//				String tmp = m_audioMatcher.lookup(item);
//				if (tmp != null) item = tmp;					
//			}
//		}
			
		//generate itemlist
		for(int i = 0; i < L.size(); i++) {
			String s = (String)L.get(i);
			PromptItem item = null;
			
			if (isAudio(s)) {
				//turn into full url
				s = s.replaceFirst("audio:", "");
				String url = SRLocations.makeFullUrl(this.m_rcontext.m_basePromptUrl, s);
				item = PromptItem.CreateAudio(url);
				//item = String.format("<audio src=\"%s\" />", item);
			}
			else if (isRaw(s)) {
				s = s.replaceFirst("raw:", "");
				item = PromptItem.CreateRaw(s);			
			}
			else if (isPause(s)) {
				int len = s.trim().length();
				int pauseMsec = len * 250;
				//item = String.format("<pause>%s</pause>", pauseMsec);
				item = PromptItem.CreatePause(pauseMsec);
			}
			else {
				item = PromptItem.CreateTTS(s);
			}
			
			item.m_bargeIn = prompt.bargeIn();
				 
			prompt.m_itemL.add(item);
		}
	}
	
	ArrayList resolveIds(String text)
	{
		//step 1. break into items
		ArrayList L = findItems(text);
		
		int i = 0;
		while(i < L.size()) {
			String item = (String)L.get(i);
			
			//step 2. resolve ids  "id:xx"
			if (item.indexOf("id:") == 0) {
				String id = item.substring(3);
				
				String tmp = m_resolver.lookup(id);
				if (tmp != null) {
					ArrayList L2 = findItems(tmp);
					L.remove(i);
					L.addAll(i, L2);
				}
				else {
					RenderErrors.logError(m_err, RenderErrors.UnknownPromptId,
							String.format("can't find id '%s'", id));
					i++; //err!
				}
			}
			else
				i++;
		}
		return L;
	}

	/**
	 * As syntactic sugar, we'll accept audio files without the
	 * audio: prefix.  But only if the item ends in .wav (or others !!)
	 * and contains no whitespace
	 * @param item
	 * @return
	 */
	String addAudioPrefixIfNeeded(String item)
	{
		if (item.endsWith(".wav") && item.indexOf(' ') < 0) {
			return "audio:" + item;
		}
		return null; //it's not an audio item
	}

	String evaluate(String modelVar)
	{
		IModelBinder binder = m_rcontext.m_binder;
		if (binder == null) {
			RenderErrors.logError(m_err, RenderErrors.FlowHasNoModel,
					String.format("Flow without a model used a prompt that referenced model var '%s'. ", modelVar));
			return "";
		}
		String s = binder.getModelValue(modelVar);
		if (s == null) {
			binder.failed(m_err);
			RenderErrors.logError(m_err, RenderErrors.UnknownModelVar,
					String.format("can't find model var '%s'", modelVar));
			s = "";
		}
		return s;
	}
	
	String evaluateField(String fieldVar, IFlow flow)
	{
		IModelBinder binder = m_rcontext.m_binder;
		if (binder == null) {
			RenderErrors.logError(m_err, RenderErrors.FlowHasNoModel,
					String.format("Flow has no binder, and referenced field var '%s'. ", fieldVar));
			return "";
		}
		String s = binder.getFieldValue(fieldVar, flow);
		if (s == null) {
			binder.failed(m_err);
			RenderErrors.logError(m_err, RenderErrors.UnknownFieldVar,
					String.format("can't find field var '%s'", fieldVar));
			s = "";
		}
		return s;
	}
	
	ArrayList findItems(String s)
	{
		ArrayList L = new ArrayList();
		int startPos = 0;
		boolean done = false;
		boolean areIN = false; //in a { } tag our outside
		while(!done) {
			if (! areIN) { //OUT
				int pos = s.indexOf('{', startPos);
				if (pos < 0) {
					done = true;
					String item = s.substring(startPos);
					L.add(item);					
				}
				else {
					String item = s.substring(startPos, pos);
					L.add(item);					
					areIN = true;
					startPos = pos + 1;
				}
			}
			else { //IN
				int pos = s.indexOf('}', startPos);
				if (pos < 0) {
					done = true;
					RenderErrors.logError(m_err, RenderErrors.InvalidPromptString,
							String.format("bad format in %s", s));
					String item = s.substring(startPos);
					L.add(item);					
				}
				else {
					String item = s.substring(startPos, pos);
					L.add(item);
					areIN = false;
					startPos = pos + 1;
				}
			}
			
			if (startPos >= s.length()) {
				done = true;
			}
		}
		return L;
	}

	boolean isAudio(String item)
	{
		return item.indexOf("audio:") == 0;
	}
	boolean isRaw(String item)
	{
		return item.indexOf("raw:") == 0;
	}
	boolean isPause(String item)
	{
		if (item.length() == 0) {
			return false;
		}
		else if (item.charAt(0) != '.') {
			return false;
		}
		else
		{
			for(int i = 0; i < item.length(); i++) {
				if (item.charAt(i) != '.')
					return false;
			}
			return true;
		}
	}
	
	ArrayList mergeRuns(ArrayList L)
	{
		ArrayList resultL = new ArrayList();
		boolean inRun = false;
		int startIndex = 0;
		for(int i = 0; i < L.size(); i++) {
			String item = (String)L.get(i);

			if (isAudio(item) || isPause(item) || isRaw(item)) {
				if (inRun) {
					resultL.addAll(resultL.size() , L.subList(startIndex, i));
				}
				resultL.add(item);			
				inRun = false;
			}
			else {
				if (! inRun) {
					inRun = true;
					startIndex = i;
				}				
			}			
		}
		if (inRun) {
			ArrayList tmpL = new ArrayList(L.subList(startIndex, L.size()));
			String s = "";
			for(int j = 0; j < tmpL.size(); j++) {
				s += (String)tmpL.get(j) + " ";
			}
			resultL.add(s.trim());
		}
		
		return resultL;
	}
		
}

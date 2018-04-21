/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;
import java.util.ArrayList;

import org.speakright.core.IModelBinder;
import org.speakright.core.IFlow;
import org.speakright.core.SRError;
import org.speakright.core.SRLocations;
import org.speakright.core.FieldBinder;
import org.speakright.core.SRLogger;

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
	Prompt m_prompt; //the one we're currently rendering
	
	public PromptPipeline(RenderContext rcontext) 
	{
		m_rcontext = rcontext;
		m_locations = rcontext.m_locations;
		m_resolver = new ExternalPromptResolver(m_locations, rcontext.m_promptFileL, rcontext.m_logger);
		if (rcontext.m_doAudioMatching) {
			m_audioMatcher = new AudioMatcher(m_locations);
		} //otherwise m_audioMatcher stays null
	}
	
	public boolean failed(SRError parent)
	{
		return m_err.failed(parent);
	}
	
	public void render(Prompt prompt)
	{
		m_prompt = prompt;
		if (prompt.m_itemL == null) {
			prompt.m_itemL = new ArrayList<PromptItem>(); //rendered prompt goes here
		}
		else {
			prompt.m_itemL.clear();
		}
		
		//first apply the server-side condition, which
		//may disable the prompt from being played
		if (! prompt.applyCondition(m_rcontext)) {
			m_rcontext.m_logger.log(String.format("%s: skipping prompt: %s", m_rcontext.m_flow.name(), prompt.ptext()));
			return;
		}
		//can contain
		//"id:xxx"
		//"$M.xxx" or $INPUT
		//"audio:xxx"
		//"raw:xxx"
		//..  for pause, each . is 250 msec
		//"any other text is tts"
		//can combine using { }
		
//		String ptext = prompt.ptext();
//		if (ptext.indexOf("color") >= 0) {
//			m_rcontext.m_logger.log("sdf");
//		}
				
		//step 1. break into items
		ArrayList L = resolveIds(prompt.ptext());
		if (m_rcontext.m_logPrompts) {
			m_rcontext.m_logger.logDebug(String.format("prompt %s (%d items): %s", prompt.type().toString(),
				L.size(), m_rcontext.m_logger.limitString(prompt.ptext(), 80)));
		}
		
		while(true) {
			int changeCount = renderItems(L);
			if (changeCount == 0) {
				break;
			}
//			m_rcontext.m_logger.logDebug("renderItems: changeCount=" + changeCount);
			resolveIds(L); //break again into items 
		}
				
		//still only raw,tts,audio,pause left
		//step 5. merge runs of tts
		L = mergeRuns(L);
		
		//step 6. match tts with audio!!
		if (m_audioMatcher != null) {
			for(int i = 0; i < L.size(); i++) {
				String item = (String)L.get(i);

				if (! isAudio(item) && !isPause(item) && !isRaw(item) && ! isPeriod(item)) { //tts?
					String tmp = m_audioMatcher.lookup(item);
					if (tmp != null) {
						L.set(i, tmp); //replace
					}
				}
			}
		}
		
		//generate itemlist
		for(int i = 0; i < L.size(); i++) {
			String s = (String)L.get(i);
			PromptItem item = null;
			
			if (isAudio(s)) {
				//turn into full url
				s = s.replaceFirst("audio:", "");
				String url = m_rcontext.m_locations.makeFullPromptUrl(s);
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
			else if (isPeriod(s)) {
				item = PromptItem.CreateTTS("."); //convert back to single period
			}
			else {
				item = PromptItem.CreateTTS(s);
			}
			
			item.m_bargeIn = prompt.bargeIn();
				 
			prompt.m_itemL.add(item);
		}
	}
	
	public int renderItems(ArrayList L)
	{
		//can contain
		//"id:xxx"
		//"$M.xxx" or $INPUT
		//"audio:xxx"
		//"raw:xxx"
		//..  for pause, each . is 250 msec
		//"any other text is tts"
		//can combine using { }
				
		//step 1. break into items
//		ArrayList L = resolveIds(prompt.text());
//		m_rcontext.m_logger.logDebug(String.format("prompt (%d items): %s", L.size(), m_rcontext.m_logger.limitString(prompt.text(), 80)));
		
//		m_rcontext.m_logger.logDebug("L size=" + L.size());
		int changeCount = 0; //# of evals we do
				
		//now all ids removed, only raw,tts,audio,value,pause items left
		for(int i = 0; i < L.size(); i++) {
			String item = (String)L.get(i);
			item = item.trim();
			L.set(i, item); //save trimmed version
						
			//detect audio items without the prefix
			if (! isAudio(item) && ! isPause(item) && !isRaw(item) && ! isPeriod(item)) {
				String tmp = addAudioPrefixIfNeeded(item);
				if (tmp != null) {
					L.set(i, tmp); //replace
					changeCount++;
				}
			}
			//step 3. evaluate value items
			if (item.indexOf("$M.") == 0) {
				String modelVar = item.substring(3);
				
				item = evaluate(modelVar);
				L.set(i, item); //replace
				//later maybe support eval returning id. for now assume its a single item audio or tts!!
				changeCount++;
			}
			else if (item.equals("$INPUT")) {
				if (m_rcontext.m_results == null) {
					item = "";
				}
				else {
					item = m_rcontext.m_results.m_input;
				}
				m_rcontext.m_logger.log(("$INPUT = " + item));
				L.set(i, item); //replace
				changeCount++;
			}
			else if (item.startsWith("%") && item.endsWith("%")) {
				String fieldVar = "m_" + item.substring(1, item.length() - 1);
				
				item = evaluateField(fieldVar, m_rcontext.m_flow);
				L.set(i, item); //replace
				changeCount++;
			}
			
			//now only raw,tts,audio,pause left
			//step 4. call fixup handlers (walks call stack)
			if (! isAudio(item) && ! isPause(item) && !isRaw(item) && ! isPeriod(item)) {
				String tmp = m_rcontext.m_promptAdjuster.fixupPrompt(item);
				if (tmp != null) {
					L.set(i, tmp); //replace
					changeCount++;
				}
			}
		}
			
		return changeCount;
	}
	
	void resolveIds(ArrayList L)
	{
		ArrayList resultL = new ArrayList();
		
		for(int i = 0; i < L.size(); i++) {
			String item = (String)L.get(i);
			ArrayList tmp = resolveIds(item);
			resultL.addAll(tmp);
		}
		
		//now replace L's contents with resultL's contents
		L.clear();
		L.addAll(resultL);
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
				
				String tmp = lookupPromptById(id);
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
			else if (item.equals("$PLAY_ONCE")) {
				L.remove(i);
				if (m_prompt.m_condition == null) {
					m_rcontext.m_logger.log("adding " + item);
					m_prompt.setConditionPlayOnce();
				}
			}
			else if (item.equals("$PLAY_ONCE_EVER")) {
				L.remove(i);
				if (m_prompt.m_condition == null) {
					m_rcontext.m_logger.log("adding " + item);
					m_prompt.setConditionPlayOnceEver();
				}
			}
			else
				i++;
		}
		return L;
	}
	
	/**
	 * Lookup the prompt text for the given id.  Looks first using entire
	 * id.  If not found then removes any group prefix (eg. "AskFlightNumber.")
	 * and looks again.  This feature is used by SRO objects which allow the
	 * app to set the group prefix for each instance of an SRO object.  Then the
	 * app-specific prompt XML file can contain prompts for specific flow objects.  Prompts not found
	 * in this XML file are resolved by looking in the SRO's class-specific prompt XML file.
	 * The benefit of group prefixes is that prompts for each flow object can be defined in XML files. 
	 * @param id
	 * @return
	 */
	String lookupPromptById(String id)
	{
		//add group prefix if enabled and no prefix already defined
		if (m_rcontext.m_promptGroup.length() > 0 && m_rcontext.m_promptGroup.indexOf('.') < 0) {
			String prefix = m_rcontext.m_promptGroup;
			id = prefix + "." + id;
//			m_rcontext.m_logger.log("id now: " + id);
		}
		
		String tmp = m_resolver.lookup(id);
		
		if (tmp == null) {
			int pos = id.indexOf(".");
			if (pos > 0) {
				String grp = id.substring(0, pos);
//				m_rcontext.m_logger.log("grp.." + grp);
				
				String idWithoutGrp = id.substring(pos + 1);
//				m_rcontext.m_logger.log("grp2.." + idWithoutGrp);
				tmp = m_resolver.lookup(idWithoutGrp);
			}
		}
		return tmp;
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
		else
		{
			m_rcontext.m_logger.log("eval: " + modelVar + " = " + s);
		}
		return s;
	}
	
	String evaluateField(String fieldVar, IFlow flow)
	{
		FieldBinder binder = m_rcontext.m_fieldBinder;
		String s = binder.getFieldValue(fieldVar, flow);
		if (s == null) {
			binder.failed(m_err);
			RenderErrors.logError(m_err, RenderErrors.UnknownFieldVar,
					String.format("can't find field var '%s'", fieldVar));
			s = "";
		}
		return s;
	}
	
	/**
	 * Break prompt text into items.  Items are delimited by { and }
	 * @param s the prompt text
	 * @return list of items
	 */
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
					//don't make '.' outside of {} be a pause
					if (item.equals(".")) {
						item = "__period__"; //reserved word
					}
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
	boolean isPeriod(String item)
	{
		return item.indexOf("__period__") == 0;
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

			if (isAudio(item) || isPause(item) || isRaw(item) || isPeriod(item)) {
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

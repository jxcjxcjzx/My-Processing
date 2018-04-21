package org.speakright.core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Tracks whether a certain prompt has been played already during execution of the
 * application.  Used internally to implement prompt condition play-once.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class PlayOnceTracker implements Serializable {
	ArrayList<PlayOnceSpec> m_L = new ArrayList<PlayOnceSpec>(); //of PlayOnceSpec objects

	public void add(IFlow flow, String text)
	{
		add(flow, text, false);
	}
	public void add(IFlow flow, String text, boolean onceEver)
	{
		//must not already be in list
		PlayOnceSpec spec = null;
		if (spec == null) {
			spec = new PlayOnceSpec();
			spec.m_flow = flow;
			spec.m_text = text;
			spec.m_onceEver = onceEver;
			
			m_L.add(spec);
		}
	}
	
	public void removeAllFor(IFlow flow)
	{
		ArrayList<PlayOnceSpec> copy = new ArrayList<PlayOnceSpec>();
		for(PlayOnceSpec spec : m_L) {
			if (spec.m_flow != flow || spec.m_onceEver) {
				copy.add(spec);
			}
		}
		
		m_L = copy;
	}
	
	public PlayOnceSpec find(IFlow flow, String text)
	{
		for(PlayOnceSpec spec : m_L) {
			if (spec.m_flow == flow && spec.m_text == text) {
				return spec;
			}
		}
		return null;
	}
	
	class PlayOnceSpec
	{
		public IFlow m_flow;
		public String m_text;  //prompt text
		public boolean m_onceEver; //true means once per app. false means once per m_flow's execution
	}
}

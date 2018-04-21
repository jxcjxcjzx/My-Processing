/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;


/**
 * Records audio to a file.
 * @author IanRaeLaptop
 *
 */
public class RecordAudio extends FormElement {

	boolean m_beepBeforeRecord;
	int m_maxTime; //in msec
	int m_finalSilenceTime; //in msec

	public RecordAudio(boolean beep, int maxTime, int finalSilence)
	{
		m_beepBeforeRecord = beep;
		m_maxTime = maxTime;
		m_finalSilenceTime = finalSilence;
	}

	/**
	 * used by ST
	 * @return
	 */
	public boolean getBeep()
	{
		return m_beepBeforeRecord;
	}
	
	public String getMaxTime()
	{
		return convertToString(m_maxTime);
	}
	public String getFinalSilence()
	{
		return convertToString(m_finalSilenceTime);
	}
	
	String convertToString(int time)
	{
		int k = time % 1000;
		int n = 0;
		if (k == 0) {
			n = time / 1000;
			return String.format("%ss", n); //sec
		}
		else {
			return String.format("%sms", time); //msec
		}
	}
}

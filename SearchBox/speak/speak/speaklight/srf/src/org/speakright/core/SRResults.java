/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Holds the results sent back by the VoiceXML platform.  These are the results of executing the previous
 * page, and include user input, errors, and events (like disconnect).
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class SRResults implements IFlowContext, Serializable {

	public String m_input;
	public int m_overallConfidence = 100; //of the entire utteraces. slots each have their own confidence value
//	public String m_slotName = "";
//	public int m_slotConf = 0;
	LinkedHashMap<String,Slot> m_map = new LinkedHashMap<String,Slot>();
	public ResultCode m_resultCode; //0 succ, 1 disc, 2 we hung up, 3 no-input, 4 cmd, 5 platform err,6=xfer_failed
	public String m_transferResult = ""; //if resultCode is TRANSFER_FAILED then this contains details, such
									//as "busy"
	
	public boolean m_validateSucceeded = true; //set by ValidateInput
	public boolean m_confirmationWasRejected = false; //set if a confirmation was rejected. The orignal question
						//can then check this and alter it's prompts to say 'OK, let's try again'

	public SRLocations m_locations; //may be useful to app

	/**
	 * Result code sent back by the VoiceXML platform, indicating the result of running
	 * the most recent VoiceXML page that we sent it.
	 */
	public enum ResultCode {
		SUCCESS, DISCONNECT, WEHUNGUP, NOINPUT, COMMAND, PLATFORM_ERROR, TRANSFER_FAILED
	}
	
	public SRResults()
	{
		this("", ResultCode.SUCCESS);
	}
	public SRResults(String input)
	{
		this(input, ResultCode.SUCCESS);
	}
	public SRResults(String input, ResultCode resultCode)
	{
		m_input = input;
		m_resultCode = resultCode;
	}
	
	/**
	 * Load from a set of CGI params (in a HTTP GET or POST).
	 * These include standard SpeakRight params such as sr__res,
	 * and VoiceXML field names (which map to SpeakRight slots) containing
	 * the user input.
	 * @param params
	 */
	public SRResults(RawCGIParams params)
	{
		this("");
		
		Map map = params.getMap();

		//loop first so set any values like sr__conf first
		//before adding any slots
		for(Object obj: map.keySet()) {
			String s = (String)obj;
			String v = (String)map.get(s);

			if (! shouldIgnore(s,v)) {
			}
		}
		
		//now add the slots
		int index = 0;
		String completeInput = "";
		for(Object obj: map.keySet()) {
			String s = (String)obj;
			String v = (String)map.get(s);

			if (! shouldIgnore(s,v)) {
				addSlot(s, v, m_overallConfidence);
				if (completeInput == "") {
					completeInput += v;
				}
				else
					completeInput += " " + v;
			}
			index++;
		}
		
		m_input = completeInput;
	}
	
	/**
	 * Return true if this CGI param is not a slot.
	 * @param param
	 * @param value
	 * @return
	 */
	boolean shouldIgnore(String param, String value)
	{
		if (value.length() == 0) {
			return true;
		}
		else if ((param.equals("sr__res"))) {
			int code = SRUtils.safeToInt(value); 
			setResultCode(code);
			return true;
		}
		else if ((param.equals("sr__xferres"))) {
			m_transferResult = value.toLowerCase();
			return true;
		}
		else if ((param.equals("sr__conf"))) {
			int conf = 0;
			if (value.indexOf('.') >= 0) {
				double d = SRUtils.safeToDouble(value); 
				d = d * 100; //convert to 0..100
				conf = (int)d;
			}
			else {
				conf = SRUtils.safeToInt(value);
			}
			
			this.m_overallConfidence = conf;
			return true;
		}
		else if ((param.equals("mode"))) {
			return true; //handled in srservletrunner
		}
		//later capture ANI, DNIS, etc
		
		return false;
	}

	/**
	 * Restore previous results.  Used in confirmation if
	 * user accepts (says 'yes') the confirmation.  We need
	 * to restore the results that needed to be confirmed
	 * so that the next flow to execute sees them, not
	 * the 'yes' results.
	 * @param saved saved results from a previous execute
	 */
	public void loadFrom(SRResults saved)
	{
		m_input = saved.m_input;
		m_overallConfidence = saved.m_overallConfidence;
		
		m_map.clear();
		m_map.putAll(saved.m_map);
		m_resultCode = saved.m_resultCode;
		
		m_validateSucceeded = saved.m_validateSucceeded;
		m_confirmationWasRejected = saved.m_confirmationWasRejected;
	}
	
	void setResultCode(int resultCode)
	{
		switch(resultCode) {
			default:
			case 0:
				m_resultCode = SRResults.ResultCode.SUCCESS;
				break;
			case 1:
				m_resultCode = SRResults.ResultCode.DISCONNECT;
				break;
			case 2:
				m_resultCode = SRResults.ResultCode.WEHUNGUP;
				break;
			case 3:
				m_resultCode = SRResults.ResultCode.NOINPUT;
				break;
			case 4:
				m_resultCode = SRResults.ResultCode.COMMAND;
				break;
			case 5:
				m_resultCode = SRResults.ResultCode.PLATFORM_ERROR;
				break;
			case 6:
				m_resultCode = SRResults.ResultCode.TRANSFER_FAILED;
				break;
		}
	}
	
	
	
	public SRLocations getLocations() {
		return m_locations;
	}

	/**
	 * Add a slot and its value.
	 * @param slotName  name of slot (used as field name in VoiceXML)
	 * @param value the value of the slot, which is usually the user input value
	 * returned by the grammar.
	 */
	public void addSlot(String slotName, String value)
	{
		if (slotName == "") {
			return;
		}
		addSlot(slotName, value, 100);
	}
	/**
	 * Add a slot and its value.
	 * @param slotName  name of slot (used as field name in VoiceXML)
	 * @param value the value of the slot, which is usually the user input value
	 * returned by the grammar.
	 * @param confidence confidence level (0..100) that the speech recognition engine
	 * returned for the utterance.  Sometimes there is a single overall confidence level,
	 * and sometimes each slot is given a separate confidence level.
	 */
	public void addSlot(String slotName, String value, int confidence)
	{
		if (slotName == "") {
			return;
		}
		Slot slot = new Slot();
		slot.m_slotName = slotName;
		slot.m_value = value;
		slot.m_confidence = confidence;
		
		m_map.put(slotName, slot);
	}
	
	/**
	 * Get the value of the given slot.
	 * @param slotName  slot name
	 * @return  slot's value or "" if not found.
	 */
	public String getSlot(String slotName)
	{
		Slot slot = findSlot(slotName);
		if (slot == null) {
			return "";
		}
		return slot.m_value;
	}
	
	/**
	 * Return true if the slot exists.
	 * @param slotName slot name
	 * @return
	 */
	public boolean hasSlot(String slotName)
	{
		Slot slot = findSlot(slotName);
		if (slot == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * The Slot object for the given slot
	 * @param slotName
	 * @return Slot object, or null if not found
	 */
	public Slot findSlot(String slotName)
	{
		Slot slot = m_map.get(slotName);
		if (slot == null) {
			return null;
		}
		return slot;
	}
	
	/**
	 * Get the i-th slot in these results.
	 * @param index between 0 and slotCount() - 1 inclusive
	 * @return Slot object, or null if out of range
	 */
	public Slot getIthSlot(int index)
	{
		int i = 0;
		 for (Iterator it = m_map.values().iterator(); it.hasNext();) {
			 Slot slot = (Slot)it.next();
			 if (index == i) {
				 return slot;
			 }
		      i++;
		 }
		 return null;
	}
	
	/**
	 * Get the number of slots in these results
	 * @return number of slots (can be 0)
	 */
	public int slotCount()
	{
		return m_map.size();
	}
	
	/**
	 * Holds information about a single slot.  A slot represents
	 * a piece of information to get from the user.  It gets
	 * mapped to a field in VoiceXML.
	 * Each user input question has at least one slot to fill.
	 * @author IanRaeLaptop
	 *
	 */
	@SuppressWarnings("serial")
	public class Slot implements Serializable
	{
		public String m_slotName;
		public String m_value; //user input value returned by the grammar (the SML result)
		public int m_confidence;
	}
}

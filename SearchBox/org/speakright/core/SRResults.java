package org.speakright.core;
import java.io.Serializable;

/**
 * Holds the results sent back by the VoiceXML platform.  These are the results of executing the previous
 * page, and include user input, errors, and events (like disconnect).
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class SRResults implements Serializable {

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
	public String m_input;
	public String m_slotName = "";
	public int m_slotConf = 0;
	public ResultCode m_resultCode; //0 succ, 1 disc, 2 we hung up, 3 no-input, 4 cmd, 5 platform err
	
	public enum ResultCode {
		SUCCESS, DISCONNECT, WEHUNGUP, NOINPUT, COMMAND, PLATFORM_ERROR
	}
}

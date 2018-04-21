package org.speakright.itest;

import org.speakright.core.IFlow;
import org.speakright.core.SRError;
import org.speakright.core.SRInstance;

public class SRAutoTester implements SRInteractiveTester.CommandReader, SRInteractiveTester.ContentChecker {
	SRError m_errors = new SRError(); 

	String m_expectedFlow = ""; //name of expected flow object (used in checkContent))
	boolean m_stopOnError = true; //stop when a test fails
	SRInstance m_run;
	SRInteractiveTester m_tester;
	int m_testNumber; //for logger
	
	int m_currentCommandIndex = 0;
	String[] m_cmds;
	
	public SRAutoTester(int testNumber, String[] arCmds)
	{
		m_testNumber = testNumber;
		m_cmds = arCmds;
	}
	
	public SRAutoTester(int testNumber, String cmds)
	{
		m_testNumber = testNumber;
		m_cmds = cmds.split(";");
		int len = m_cmds.length;
		if (! m_cmds[len - 1].equals("q")) {
			throw new RuntimeException("last cmd in cmd string must be q");
		}
	}
	
	/**
	 * Run the given tester.
	 * @param tester
	 * @return false if test failed, true if passed
	 */
	public boolean run(SRInteractiveTester tester)
	{
		m_tester = tester;
		m_tester.setCommandReader(this);
		m_tester.setChecker(this);
		m_run = m_tester.runner();
		m_tester.run();
		
		if (m_errors.failed()) {
			m_run.log("TEST " + m_testNumber + ": FAILED!!!!!!!!!!!!!!!!!!!!!!!!! at cmd #" + m_currentCommandIndex);
			return false;
		}
		else if (m_currentCommandIndex < m_cmds.length) {
			m_run.log("TEST " + m_testNumber + ": FAILED (ended early) !!!!!!!!!!!!!!!!!!!!!!!!! at cmd #" + m_currentCommandIndex);
			return false;			
		}
		else {
			m_run.log("TEST " + m_testNumber + ": SUCCESS. (" + m_currentCommandIndex + " cmds)");
			return true;
		}
	}

	public String readLine(int turnNumber) {
		if (m_tester.isFinished() || m_run.isFinished()) {
			return "q";
		}
		String s = m_cmds[m_currentCommandIndex++];
		
		String[] ar = s.split("~");
		m_expectedFlow = "";
		if (ar.length > 1) {
			m_expectedFlow = ar[1];
		}
		return ar[0];
	}

	public boolean checkContent(SRInstance run, String content) {
		if (m_expectedFlow != "") {
			
			IFlow peek = run.peekCurrent();
			String name = peek.name(); 
			if (! m_expectedFlow.equals(name)) {
				m_errors.logError(100, String.format("expected %s but got %s", m_expectedFlow, name));
				return ! m_stopOnError;
			}
		}
		return true;
	}
}

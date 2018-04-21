/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render.mock;

import java.util.ArrayList;

import org.speakright.core.render.*;


/**
 * Speech page writer that generates nothing.  It simply remembers its
 * SpeechPage object.  
 * 
 * For use in unit testing where we want to see the results of rendering
 * without having to do all the messy XML compare.
 * 
 * @author Ian Rae
 *
 */
public class MockSpeechPageWriter implements ISpeechPageWriter {
	
	String m_content = "";
	public SpeechForm m_form;
	public SpeechPage m_page;
	int m_fieldNum;
	boolean m_isFinPage;
	public RenderContext m_rcontext;
	
	public MockSpeechPageWriter()
	{
	}
	
	
	public void setRenderContext(RenderContext rcontext)
	{
		m_rcontext = rcontext;
		m_rcontext.m_logger.log("MockSpeechPageWriter..");
	}
	public String getContent()
	{
		return "mock"; //can't return "" m_content;
	}
	
	public void beginPage()
	{
	}
	public void render(SpeechPage page)
	{
		m_page = page;
		m_form = page.m_form;	//only one form per page now	
	}
	
	public void endPage()
	{
	}
}

/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render.voicexml;
import java.util.ArrayList;

import org.antlr.stringtemplate.CommonGroupLoader;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.speakright.core.render.*;
import org.antlr.stringtemplate.*;


/**
 * Speech page writer that generates VoiceXML.
 * 
 * This is where any voicexml-specific code goes.  The speech page classes
 * in render should be agnostic.
 * @author Ian Rae
 *
 */
public class VoiceXMLSpeechPageWriter implements ISpeechPageWriter, StringTemplateErrorListener  {
	
	String m_content = "";
	boolean m_rawContentSeen = false;
	String m_term = "\r\n"; //!!how to make this platform-independent?
	int m_fieldNum;
	public RenderContext m_rcontext;
	StringTemplateGroup m_templateGroup;
	
	/**
	 * used for unit tests only
	 */
	static public boolean m_renderHeaderAndFooter = true;
	
	public VoiceXMLSpeechPageWriter()
	{
		//our stringtemplate template files are in our jar file
		CommonGroupLoader loader = new CommonGroupLoader("org/speakright/core/render/voicexml/templates", this);
		m_templateGroup = loader.loadGroup("speakright"); //load speakright.stg		
	}
	
	//interface StringTemplateErrorListener
	int m_errCount;
	public void error(String msg, Throwable e)
	{
		m_errCount++;
	}
    
	public void warning(String msg)
	{
		System.out.println("STWARN: " + msg);
	}
	
	
	
	public void setRenderContext(RenderContext rcontext)
	{
		m_rcontext = rcontext;
	}
	public String getContent()
	{
		return m_content;
	}
	
	public void o(String s)
	{
		m_content += s;
		m_content += m_term;
	}

	public void beginPage()
	{
		if (m_renderHeaderAndFooter) {
//			m_content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
//			o("<vxml xmlns=\"http//www.w3c.org/2001/vxml\" version=\"2.1\">");
//			o("<form>");
			
			StringTemplateGroup group = m_templateGroup;
			StringTemplate t = group.getInstanceOf("pageheader");
			t.setAttribute("version", "2.0");
			t.setAttribute("url", m_rcontext.m_returnUrl);
			String str = t.toString();
			o(str);
		}
		else {
			o("<form>");
		}
	}
	public void render(SpeechPage page)
	{
		SpeechForm form = page.m_form;	//only one form per page now
		
		if (page.m_form.m_fieldL.size() == 0) //fin page?
		{
			renderFinPage();
			return;
		}
		
		for(FormElement el : form.m_fieldL) {
			
			if (el instanceof Question) {
				renderQuestion((Question)el);
			}
			else if (el instanceof Prompt) {
				int i = form.m_fieldL.indexOf(el);
				boolean isLast = (i >= form.m_fieldL.size() - 1);
				renderPrompt((Prompt)el, isLast);
			}
			else if (el instanceof Disconnect) {
				renderDisconnect((Disconnect)el);
			}
			else if (el instanceof Transfer) {
				renderTransfer((Transfer)el);
			}
			else if (el instanceof RawContent) {
				renderRawContent((RawContent)el);
			}
			else if (el instanceof GotoUrl) {
				renderGotoUrl((GotoUrl)el);
			}
			else if (el instanceof RecordAudio) {
				renderRecordAudio((RecordAudio)el);
			}
		}
	}
	
	void renderFinPage()
	{
		StringTemplateGroup group = m_templateGroup;

		StringTemplate t= group.getInstanceOf("finPage");
		
		//t.setAttribute("promptL", L);
		String str = t.toString();
		o(str);
	}
	
	void renderQuestion(Question quest)
	{
		QuestionWriter qw = new QuestionWriter(this);
		qw.render(quest, m_fieldNum++);
		
	}
	void renderPrompt(Prompt prompt, boolean isLast)
	{
		PromptWriter pw = new PromptWriter(this);
		pw.render(prompt, true);
		
		if (isLast) {
//			pw.renderSubmitBlock("http://abc.com");
			pw.renderSubmitBlock(m_rcontext.m_returnUrl);
		}
	}
	void renderDisconnect(Disconnect disc)
	{
		o("<disconnect/>");
	}
	void renderTransfer(Transfer xfer)
	{
		TransferWriter w = new TransferWriter(this);
		w.render(xfer, m_rcontext.m_returnUrl);
	}
	void renderRawContent(RawContent rawContent)
	{
		m_content = ""; //clear any previous content
		o(rawContent.getContent());
		m_rawContentSeen = true; //so we suppress page
	}
	void renderGotoUrl(GotoUrl gotoUrl)
	{
		StringTemplateGroup group = this.m_templateGroup;
		StringTemplate t = group.getInstanceOf("gotoBlock");
		t.setAttribute("url", gotoUrl.getUrl());
		String str = t.toString();
		o(str);		
	}
	void renderRecordAudio(RecordAudio record)
	{
		StringTemplateGroup group = this.m_templateGroup;
		StringTemplate t = group.getInstanceOf("recordBlock");
		t.setAttribute("beep", record.getBeep());
		t.setAttribute("max", record.getMaxTime());
		t.setAttribute("end", record.getFinalSilence());
		t.setAttribute("url", m_rcontext.m_returnUrl);
		String str = t.toString();
		o(str);		
	}
	
	
	public void endPage()
	{
		if (m_rawContentSeen) {
			return; //do nothing
		}
		if (m_renderHeaderAndFooter) {
//			o("</form>");
//			o("</vxml>");
			StringTemplateGroup group = m_templateGroup;
			StringTemplate t = group.getInstanceOf("pagefooter");
			String str = t.toString();
			o(str);
		}
		else {
			o("</form>");
		}
	}
}

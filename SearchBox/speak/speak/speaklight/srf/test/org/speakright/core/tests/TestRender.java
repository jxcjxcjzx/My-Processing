/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;
import static org.junit.Assert.assertEquals;

import java.util.*;
import java.io.*;

//import org.antlr.stringtemplate.*;
import org.custommonkey.xmlunit.*;
import org.junit.Assert;

import org.speakright.core.*;
import org.speakright.core.flows.GotoUrlFlow;
import org.speakright.core.flows.LoopFlow;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.QuestionFlow;
import org.speakright.core.flows.RawContentFlow;
import org.speakright.core.flows.RecordAudioFlow;
import org.speakright.core.flows.SRApp;
import org.speakright.core.flows.TransferFlow;
import org.speakright.core.render.*;
import org.speakright.core.render.voicexml.*;


public class TestRender extends XMLTestCase {

	public void testEmpty()
	{
		ValFlow flow = new ValFlow("a");
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		
		chkXmlFile(run, "ref_empty.vxml");
	}

	public void testHello()
	{
		PromptFlow flow = new PromptFlow("hello");
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		
		chkXmlFile(run, "ref_hello.vxml");
	}
	
	public void testYouSaid()
	{
		SRApp flow = new SRApp();
		QuestionFlow flow1 = new QuestionFlow("ghi.grxml", "what zoo?");
		YouSaidFlow flow2 = new YouSaidFlow();
		flow.add(flow1);
		flow.add(flow2);
		
		
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		base.Proceed(run, "222");
		base.Proceed(run); //b
		
		chkXmlFile(run, "ref_yousaid.vxml");
	}
	
	public void testAudioUrls()
	{
		PromptFlow flow = new PromptFlow("audio:abc.wav");
		flow.addPrompt("audio:special/def.wav");
		flow.addPrompt("audio:http://localhost/stock/ghi.wav");
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		
		chkXmlFile(run, "ref_audio.vxml");
	}
	public void testAudioUrlsBaseDir()
	{
		PromptFlow flow = new PromptFlow("audio:abc.wav");
		flow.addPrompt("audio:special/def.wav");
		flow.addPrompt("audio:http://localhost/stock/ghi.wav");
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow, "", "http://somecompany.com/app1");
		
		chkXmlFile(run, "ref_audio2.vxml");
	}
	public void testAudioItem()
	{
		PromptFlow flow = new PromptFlow("back from the question. {audio:audio/Welcome.WAV}");
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow, "", "http://somecompany.com/app1");
		
		chkXmlFile(run, "ref_audio3.vxml");
	}
	public void testNoBargeIn()
	{
		PromptFlow flow = new PromptFlow("hello");
		flow.setBargeIn(false);
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		
		chkXmlFile(run, "ref_nobargein.vxml");
	}
	public void testSubItems()
	{
		SRLogger log = SRLogger.createLogger();
		log.log("testSubItems ------------");
		
		QFlow flow1 = new QFlow("gram1.grxml", "abcd");
		Prompt p2 = new Prompt("defg");
		p2.setSubIndex(1);
		flow1.addPrompt(p2);
		
		SRApp flow = new SRApp();
		flow.add(flow1);
		
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		base.Proceed(run, "222");
//		base.Proceed(run); //b
		
		chkXmlFile(run, "ref_subitems.vxml");
		log.log("end testSubItems ------------");
	}
	
	public void testSubItemsConditionFalse()
	{
		QFlow flow1 = new QFlow("gram1.grxml", "abcd");
		Prompt p2 = new Prompt("defg");
		p2.setSubIndex(1);
		p2.setConditionCustom(false); //condition false
		
		flow1.addPrompt(p2);
		
		SRApp flow = new SRApp();
		flow.add(flow1);
		
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		base.Proceed(run, "222");
//		base.Proceed(run); //b
		
		chkXmlFile(run, "ref_subitemsconditionfalse.vxml");
	}
	
	
	public void testPromptResolver()
	{
		doPrompt("id:a11", "welcome");
	}
	public void testNestedIds()
	{
		doPrompt("id:a20", "hello");
	}
	public void testBadId()
	{
		//and try a prompt with a bad id
//		doPrompt("id:a25", "", false);

		PromptFlow flow = new PromptFlow("id:a25");
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		
		chkXmlFile(run, "ref_badid.vxml");
	}
	public void testPromptGroupId()
	{
		//this prompt exists in app_prompts.xml
		PromptFlow flow = new PromptFlow("id:group1.a10");
		
		BaseTest base = new BaseTest();
		base.log("..GROUPID..");
		
//		SRInstance run = base.StartIt(flow);
		SRInstance run = base.CreateInstance(flow, "", "");
		String path = base.dir + "app_prompts.xml";
		run.registerPromptFile(path);
		boolean b = run.start(flow);		
		Assert.assertTrue(b);
		assertEquals("err", false, run.failed(new SRError())); //suceeded
		
		chkXmlFile(run, "ref_groupid.vxml");		
	}
	public void testPromptGroupIdUseDefault()
	{
		//this prompt doesn't exists in app_prompts.xml so prompt a12 from prompts.xml should be found
		PromptFlow flow = new PromptFlow("id:a12");
		
		BaseTest base = new BaseTest();
		base.log("..GROUPID..");
		
//		SRInstance run = base.StartIt(flow);
		SRInstance run = base.CreateInstance(flow, "", "");
		String path = base.dir + "app_prompts.xml";
		run.registerPromptFile(path);
		boolean b = run.start(flow);		
		Assert.assertTrue(b);
		assertEquals("err", false, run.failed(new SRError())); //suceeded
		
		chkXmlFile(run, "ref_groupid.vxml");		
	}
	public void testTripleItem()
	{
		PromptFlow flow = new PromptFlow("Earth is home. {..}{For now!}");

		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);

		chkXmlFile(run, "ref_tripleitem.vxml");
	}
	public void testAudioPause()
	{
		PromptFlow flow = new PromptFlow("audio:audio/daisy.wav" + "{..}");

		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);

		chkXmlFile(run, "ref_audiopause.vxml");
	}
	public void testTTS()
	{
		doPrompt("hello world", "hello world");
	}
	public void testRaw()
	{
		doPrompt("raw:a$xx<loud/>", "a$xx<loud/>");
//		doPrompt("raw:abc", "a$xx<loud>");
//		doPrompt("ax", "a$xx<loud>", false);
	}
	public void testAudio()
	{
		doPrompt("audio:abc.wav", "<audio src=\"abc.wav\"/>");
	}
	public void testAudioWithoutPrefix()
	{
		doPrompt("abc.wav", "<audio src=\"abc.wav\"/>");
	}
	public void Pause()
	{
		doPrompt("..", "<break time=\"500\"/>");
	}
	public void Pause750()
	{
		doPrompt("..", "<break time=\"750\"/>");
	}

	void doPrompt(String text, String expected)
	{
		doPrompt(text, expected, true);
	}
	void doPrompt(String text, String expected, boolean success)
	{
		System.out.println("doPrompt....." + text);
		PromptFlow flow = new PromptFlow(text);	
		SRInstance run = renderBodyOnly(flow);
		SRError err = new SRError();
		run.failed(err);
		System.out.println("WWWWWWWWW" + err);
		assertEquals("err", !success, run.failed(new SRError()));
		
		if (!success) {
			assertEquals("nocontent", "", run.getContent());
		}
		else {
			String block = formatBlock(expected);
			chkXml(run, block);
		}		
	}
	
	
	String formatBlock(String body)
	{
		String block = "<form>\n";
		block += "<block>\n";
		block += "<prompt>";
		block += body;
		block += "</prompt>\n";
		block += "</block>\n";
		block += "<block>\n";
		block += "<submit next=\"http://abc.com\" namelist=\"\" method=\"get\"/>\n";
		block += "</block>\n</form>\n";
		return block;
	}
	
	SRInstance renderBodyOnly(IFlow flow)
	{
		BaseTest base = new BaseTest();
		VoiceXMLSpeechPageWriter.m_renderHeaderAndFooter = false;
		SRInstance run = base.StartIt(flow);
		VoiceXMLSpeechPageWriter.m_renderHeaderAndFooter = true; //restore
		return run;
	}
	
	public void testValues()
	{
		ZFlow flow = new ZFlow("news for {$M.City}");
	
		SRInstance run = renderBodyOnly(flow);
		assertEquals("err", false, run.failed(new SRError()));
		
		String block = formatBlock("news for boston");
		chkXml(run, block);
	}
	
	public void testFieldValues()
	{
		System.out.println("-------fieldvalues");
		ZFlow flow = new ZFlow("it's {%special%}");
	
		SRInstance run = renderBodyOnly(flow);
		assertEquals("err", false, run.failed(new SRError()));
		
		String block = formatBlock("it's magic");
		chkXml(run, block);
	}
	
	public void testFieldValuesIModelItem()
	{
		System.out.println("-------fieldvalues");
		ZFlow flow = new ZFlow("it's {%item%}");
	
		SRInstance run = renderBodyOnly(flow);
		assertEquals("err", false, run.failed(new SRError()));
		
		String block = formatBlock("it's montreal");
		chkXml(run, block);
	}
	
	public void testPlayOnceItem()
	{
		ArrayList L = new ArrayList();
		L.add("a");
		L.add("b");
		assertEquals("c", 2, L.size());
		L.remove(0);
		assertEquals("c", 1, L.size());
		
		
		ZFlow flow = new ZFlow("{$PLAY_ONCE}abcd"); //gets parsed as 3 items (but first one is empty)
	
		SRInstance run = renderBodyOnly(flow);
		assertEquals("err", false, run.failed(new SRError()));
		
		String block = formatBlock("abcd");
		chkXml(run, block);
	}
	
	@SuppressWarnings("serial")
	public class ZFlow extends PromptFlow {
		public Model M;
		public String m_special = "magic"; //used for testing %special%
		public MyItem m_item = new MyItem();
		
		public ZFlow(String text)
		{			
			super(text);
		}
		
		public void execute(IExecutionContext context)
		{
			M.city().set("boston");
			super.execute(context);
		}		
	}
	
	@SuppressWarnings("serial")
	public class MyItem extends ModelItemBase implements IItemFormatter 
	{
		MyItem()
		{
			m_formatter = this;
		}
		public void clear() {
		}

		public Object rawValue() {
			return null;
		}

		public String formatItem() {
			return "montreal";
		}
		
	}
	
	public void testMultiItemPrompts()
	{
		
		ArrayList L = FindItems("asdf");
		chk(L, "asdf", null);

		L = FindItems("abc{def}");
		chk(L, "abc", "def");

		L = FindItems("");
		chk(L, null, null);

		L = FindItems("{abc}{def}");
		chk(L, "abc", "def");
		
		ArrayList L2 = new ArrayList();
		L2.add("ghi");
		L2.add("jkl");
		L.addAll(1, L2);
		
		assertEquals("L2", "[abc, ghi, jkl, def]", L.toString());
	}
	
	void chk(ArrayList L, String s1, String s2)
	{
		int n = 0;
		if (s1 != null) n++;
		if (s2 != null) n++;
		
		this.assertTrue("len", (L.size() >= n));
		
		if (s1 != null)
		{
			assertEquals(s1, s1, (String)L.get(0));
		}
		if (s2 != null)
		{
			assertEquals(s2, s2, (String)L.get(1));
		}
	}
	
	ArrayList FindItems(String s)
	{
		ArrayList L = new ArrayList();
		int startPos = 0;
		boolean done = false;
		while(!done) {
			int pos = s.indexOf('{', startPos);
			if (pos < 0) {
				done = true;
				pos = s.indexOf('}', startPos);
				if (pos < 0) 
				{
					String item = s.substring(startPos);
					L.add(item);					
				}
				else
				{
					String item = s.substring(startPos, pos);
					L.add(item);					
				}
			}
			else if (pos == 0) {
				startPos = 1;
			}
			else {
				String item = s.substring(startPos, pos - startPos);
				L.add(item);					
				startPos = pos + 1;
			}
		}
		return L;
	}
	
	@SuppressWarnings("serial")
	public class PlayOnceFlow extends PromptFlow {
		boolean m_onceEver;
		
		public PlayOnceFlow(String text, boolean onceEver)
		{			
			super(text);
			m_onceEver = onceEver;
			Prompt prompt = firstPrompt();
			if (onceEver)
				prompt.setConditionPlayOnceEver();
			else
				prompt.setConditionPlayOnce();
		}		
		public IFlow getNext(IFlow current, SRResults results)
		{
			if (m_onceEver)
				return null;
			else
				return this; //keep executing so we can test the play-once feature
		}
	}
	
	public void testPlayOnce()
	{
		PlayOnceFlow flow = new PlayOnceFlow("abc", false);
		
		BaseTest base = new BaseTest();
		VoiceXMLSpeechPageWriter.m_renderHeaderAndFooter = false;
		SRInstance run = base.StartIt(flow);
		
		String block = formatBlock("abc");
		chkXml(run, block);

		base.Proceed(run, "");
		VoiceXMLSpeechPageWriter.m_renderHeaderAndFooter = true; //restore
		block = formatBlock(" "); //we always generate something, even an empty prompt
		chkXml(run, block);
		assertEquals("run", false, run.isFinished());
	}
	
	public void testPlayOnceEver()
	{
		LoopFlow flow = new LoopFlow(3);
		flow.add(new PlayOnceFlow("abc", true));
		flow.add(new ZFlow("news"));
		
		BaseTest base = new BaseTest();
		VoiceXMLSpeechPageWriter.m_renderHeaderAndFooter = false;
		SRInstance run = base.StartIt(flow);
		
		String block = formatBlock("abc");
		chkXml(run, block);

		base.Proceed(run, "1"); //b
		base.Proceed(run, "2"); //a
		block = formatBlock(""); //why here isn't is " " ?
		VoiceXMLSpeechPageWriter.m_renderHeaderAndFooter = true; //restore
		chkXml(run, block);
		assertEquals("run", false, run.isFinished());
	}
	
	public void testMultiPrompt()
	{
		ZFlow flow = new ZFlow("news");
		flow.setName("multi");
		flow.addPrompt("weather");
		flow.addPrompt("sports");
		
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		
		chkXmlFile(run, "ref_multiprompt.vxml");
	}

//
//	@Test public void disconnect()
//	{
//		DisconnectFlow flow = new DisconnectFlow("so long");
//		
//		SRInstance run = StartIt(flow);
//		String s = run.getContent();
//		chk(s, "so long"); 
//		chk(s, "<disconnect/>");
//	}
//
	
	
	public void testTransfer()
	{
		TransferFlow flow = new TransferFlow();
		flow.setDestination("222");
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		
		chkXmlFile(run, "ref_transfer.vxml");
	}

	
	void chkXmlFile(SRInstance run, String file)
	{
		System.out.println("compare file: " + file);
		String vxml = run.getContent();
		String ref = readFile(file);
		chkXml(vxml, ref);
	}
	
	void chkXml(SRInstance run, String expected)
	{
		String vxml = run.getContent();
		String ref = expected;
		chkXml(vxml, ref);
	}
	void chkXml(String vxml, String ref)
	{
		XMLUnit.setIgnoreWhitespace(true);
		try
		{
//			assertXMLEqual("xml1", ref, xml);
	        Diff diff = new Diff(ref, vxml);
	        if (! diff.similar()) {
	    		System.out.println("REF:" + ref);
	    		System.out.println(" ST:" + vxml);
	        	assertTrue("similar " + diff, diff.similar());
		        assertTrue("identical" + diff, diff.identical());
	        }
		}
		catch(Exception e)
		{
			System.out.println("EXCEPT: " + e.getLocalizedMessage());			
    		System.out.println("REF:" + ref);
    		System.out.println(" ST:" + vxml);
			assertTrue("exception!", false);
		}
	}
	
	String readFile(String file)
	{
		StringBuilder sb = new StringBuilder();
		try {
			String path = BaseTest.dir + file;
			FileReader rr = new FileReader(path);
			BufferedReader r = new BufferedReader(rr);

			String s;
			while((s = r.readLine()) != null) {
				sb.append(s + "\n");
			}
			r.close();
		}
		catch(java.io.IOException e)
		{}
		return sb.toString();
	}
	
	public void testField()
	{
		QFlow flow = new QFlow("gram1.grxml", "how many?");
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		
		chkXmlFile(run, "ref_field.vxml");
	}

	public void testFieldWithGrammarDir()
	{
		QFlow flow = new QFlow("gram1.gram", "how many?"); //ABNF form of SRGS grammar
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow, "grammars/");
		
		chkXmlFile(run, "ref_field_grams.vxml");
	}

	public void testFieldWithGSLDir()
	{
		QFlow flow = new QFlow("gram1.gsl", "how many?"); //gsl grammar
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow, "grammars/");
		
		chkXmlFile(run, "ref_field_gsl.vxml");
	}

	public void testFieldWithDTMFGrammarDir()
	{
		QFlow flow = new QFlow("builtin:digits?minlength=3;maxlength=9", "how many?");
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow, "grammars/"); //shouldn't be used
		
		chkXmlFile(run, "ref_field_dtmfgrams.vxml");
	}

	public void testFieldWithInlineGrammar()
	{
		QFlow flow = new QFlow("", "how many?");
		InlineGrammar gram = new InlineGrammar("yes no");
		gram.setSlotName("confirm");
		flow.addGrammar(gram);
	
		BaseTest base = new BaseTest();
		base.log("..INLINE GRAM TEST..");
		SRInstance run = base.StartIt(flow, "grammars/"); //shouldn't be used
		
		chkXmlFile(run, "ref_field_inlinegram.vxml");
	}

	public void testFieldWithInlineGrammarText()
	{
		QFlow flow = new QFlow("", "how many?");
		Grammar gram = new Grammar("inline:yes no");
		gram.setSlotName("confirm");
		flow.addGrammar(gram);
	
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow, "grammars/"); //shouldn't be used
		
		chkXmlFile(run, "ref_field_inlinegram.vxml");
	}

	public void testTwoGrammars()
	{
		QFlow flow = createTwoGrammarApp(false);

		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow, "grammars/"); 
		
		chkXmlFile(run, "ref_field_twogrammars.vxml");
	}
	public void testDTMFOnlyMode()
	{
		QFlow flow = createTwoGrammarApp(true);

		BaseTest base = new BaseTest();
		SRInstance run = base.CreateInstance(flow, "grammars/", "");
		run.m_dtmfOnlyModeIsActive = true;
		boolean b = run.start(flow);		
		Assert.assertTrue(b);
		
		chkXmlFile(run, "ref_field_dtmfonly.vxml");
	}
	QFlow createTwoGrammarApp(boolean dtmfOnlyMode)
	{
		QFlow flow = new QFlow("", "how many?");
		
		Grammar gram = new Grammar("inline:yes no");
		if (dtmfOnlyMode) {
			gram.setConditionDTMFOnlyMode();
		}
		gram.setSlotName("confirm");
		flow.addGrammar(gram);
		
		//add 2nd grammar (dtmf)
		Grammar dtmf = new Grammar("digits.grxml", GrammarType.DTMF);
		flow.addGrammar(dtmf);
		return flow;
	}
	
	public void testRawContent()
	{
		SRApp flow = new SRApp();
		RawContentFlow flow1 = new RawContentFlow();
		flow1.setOutputXMLTag(false);
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		flow1.setContent(s + "<vxml>abc</vxml>");
		flow.add(flow1);
		
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		base.Proceed(run, "");
		
		chkXmlFile(run, "ref_rawcontent.vxml");
	}
	
	public void testRawContentAutoXMLTag()
	{
		SRApp flow = new SRApp();
		RawContentFlow flow1 = new RawContentFlow();
		flow1.setContent("<vxml>abc</vxml>");
		flow.add(flow1);
		
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		base.Proceed(run, "");
		
		chkXmlFile(run, "ref_rawcontent.vxml");
	}
	
	public void testGotoUrl()
	{
		SRApp flow = new SRApp();
		GotoUrlFlow flow1 = new GotoUrlFlow("http://www.abc.com/def");
		flow.add(flow1);
		
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		base.Proceed(run, "");
		
		chkXmlFile(run, "ref_gotourl.vxml");
	}
	public void testRecord()
	{
		SRApp flow = new SRApp();
		RecordAudioFlow flow1 = new RecordAudioFlow();
		flow.add(flow1);
		
		BaseTest base = new BaseTest();
		SRInstance run = base.StartIt(flow);
		base.Proceed(run, "");
		
		chkXmlFile(run, "ref_record.vxml");
	}

	//------------------------------------------------------------------------
	
	@SuppressWarnings("serial")
	public class QFlow extends QuestionFlow {
		public Model M;

		public QFlow(String gtext, String text)
		{			
			super(gtext, text);
		}
		
	}
	@SuppressWarnings("serial")
	public class YouSaidFlow extends PromptFlow {

		public YouSaidFlow()
		{			
			super();
		}

		@Override
		public void execute(IExecutionContext context) {
			Prompt first = firstPrompt();
			first.setPText("You said " + context.getResults().m_input);
			super.execute(context);
		}
		
	}
	
//	@Test public void XML()
//	{
//		String dir = "C:\\Source\\Eclipse\\SpeakRight\\src\\org\\speakright\\core\\tests\\testfiles\\";
//		String path = dir + "prompts1.xml";
//        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
//        try {
//        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
//        Document doc = docBuilder.parse (new File(path));
//        
//        
//        doc.getDocumentElement ().normalize ();
//        log ("Root element of the doc is " + 
//             doc.getDocumentElement().getNodeName());
//
//
//        NodeList L = doc.getElementsByTagName("prompt");
//        int totalPersons = L.getLength();
//        log("Total no of prompts : " + totalPersons);
//        
//        for(int i=0; i<L.getLength() ; i++) {
//
//            Node node = L.item(i);
//            if(node.getNodeType() == Node.ELEMENT_NODE){
//            	Node attr = node.getAttributes().getNamedItem("id");
//            	
//            	log(attr.getTextContent() + "-> " + node.getTextContent());
//            	
//            	
//
////                Element firstPersonElement = (Element)node;
//
//            }
//        }
//        }
//
//        catch(Exception e) 
//        {
//        
//        }
//	}
//	
}

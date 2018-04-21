package org.speakright.core;

import org.speakright.core.render.IFlowRenderer;
import org.speakright.core.render.ISpeechForm;
import org.speakright.core.render.ISpeechPage;
import org.speakright.core.render.Prompt;
import org.speakright.core.render.Disconnect;;

/**
 * A call control flow object that terminates the call by
 * hanging up (playing a goodbye prompt first).
 * @author IanRaeLaptop
 *
 */
public class DisconnectFlow extends FlowBase {
	Prompt m_prompt; //say goodbye or something..
	
	public DisconnectFlow() {
		this("good bye");
	}

	public DisconnectFlow(String text) {
		super("disconnect");
		m_prompt = new Prompt(text);
	}
		

	/**
	 * Create the type-specific renderer
	 */
	@Override public IFlowRenderer createRenderer()
	{
		return new DisconnectFlowRenderer();
	}
	
	/**
	 * an renderer for this flow object. 
	 * @author Ian Rae
	 *
	 */
	private class DisconnectFlowRenderer implements IFlowRenderer
	{
		public void Render(ISpeechPage page, ISpeechForm form)
		{
			form.addField(m_prompt);
			form.addField(new Disconnect());
		}
	}

}

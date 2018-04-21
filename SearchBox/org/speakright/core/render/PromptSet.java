package org.speakright.core.render;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * A set of prompts to be used for a question.  Can contain one of
 * each prompt type.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class PromptSet extends FormElement implements Serializable {

	ArrayList<Prompt> m_L = new ArrayList<Prompt>();
	
	public void add(Prompt prompt)
	{
		Prompt existing = find(prompt.m_type);
		if (existing != null) {
			m_L.remove(existing); //only one of each type
		}
		m_L.add(prompt);
	}
	
	public void addIf(boolean b, Prompt prompt)
	{
		if (b) {
			add(prompt);
		}
	}
	
	public Prompt find(PromptType type)
	{
		for(Prompt prompt : m_L) {
			if (prompt.m_type == type)
				return prompt;
		}
		return null;
	}
	
	public ArrayList findAll(PromptType.Family family)
	{
		ArrayList L = new ArrayList();
		for(Prompt prompt : m_L) {
			if (PromptType.Family.getFamily(prompt.m_type) == family)
				L.add(prompt);
		}
		return L;
	}
	
	public void renderPrompts(PromptPipeline pipeline)
	{
		for(Prompt prompt : m_L) {
			pipeline.render(prompt);
		}
	}
	
	public ArrayList<Prompt> prompts()
	{
		return m_L;
	}
}

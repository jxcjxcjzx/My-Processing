/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * A set of grammars to be used for a question.  Can contain one of
 * each grammar type.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class GrammarSet extends FormElement implements Serializable {

	ArrayList<Grammar> m_L = new ArrayList<Grammar>();
	
	public void add(Grammar Grammar)
	{
		Grammar existing = find(Grammar.m_type);
		if (existing != null) {
			m_L.remove(existing); //only one of each type
		}
		m_L.add(Grammar);
	}
	
	public void addIf(boolean b, Grammar Grammar)
	{
		if (b) {
			add(Grammar);
		}
	}
	
	public Grammar find(GrammarType type)
	{
		for(Grammar Grammar : m_L) {
			if (Grammar.m_type == type)
				return Grammar;
		}
		return null;
	}
	
	public Grammar voiceGrammar()
	{
		return find(GrammarType.VOICE);
	}
	
	public void renderGrammars(GrammarPipeline pipeline)
	{
		for(Grammar Grammar : m_L) {
			pipeline.render(Grammar);
		}
	}
	
	public ArrayList<Grammar> Grammars()
	{
		return m_L;
	}
}

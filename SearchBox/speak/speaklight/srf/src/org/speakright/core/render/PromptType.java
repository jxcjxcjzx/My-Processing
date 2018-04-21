/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

import java.io.Serializable;

/**
 * Type that includes the role (MAIN, SILENCE, NORECO) and the escalation count.
 * @author IanRaeLaptop
 *
 */
public enum PromptType  implements Serializable {
	MAIN1,
	MAIN2,
	MAIN3,
	MAIN4,
	SILENCE1,
	SILENCE2,
	SILENCE3,
	SILENCE4,
	NORECO1,
	NORECO2,
	NORECO3,
	NORECO4;
	
	public enum Family {
		MAIN,
		SILENCE,
		NORECO;
		
		static public Family getFamily(PromptType type)
		{
			String s = type.toString();
			if (s.startsWith("MAIN")) {
				return Family.MAIN;
			}
			if (s.startsWith("SILENCE")) {
				return Family.SILENCE;
			}
			if (s.startsWith("NORECO")) {
				return Family.NORECO;
			}
			else {
				return Family.MAIN; //error! should never happen
			}
		}
	}
}

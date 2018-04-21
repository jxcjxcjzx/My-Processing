/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

import java.io.Serializable;

/**
 * Type that includes the input mode (VOICE, DTMF)
 * @author IanRaeLaptop
 *
 */
public enum GrammarType  implements Serializable {
	VOICE,
	DTMF;
}

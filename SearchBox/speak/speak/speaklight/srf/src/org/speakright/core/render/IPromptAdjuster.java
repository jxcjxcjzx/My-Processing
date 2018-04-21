/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

/**
 * Interface that lets flow objects participate in the prompt rendering
 * done by PromptPipeline.
 * @author IanRaeLaptop
 *
 */
public interface IPromptAdjuster {

	String fixupPrompt(String item);
}

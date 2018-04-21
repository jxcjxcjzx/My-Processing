package org.speakright.sro;
import org.speakright.core.FlowBase;

/**
 * This class is used to signify a cancel command.  SROs have an addCommand method which adds a sub-flow
 * that will be executed when the given command is spoken.  The 'cancel' command need to terminate the
 * SRO (i.e. have it's getNext return null).  Passing an SROCancelCommand to the addCommand method is
 * a special signal to the SRO that will cause it to terminate when the given cancel command word is
 * spoken.
 * @author IanRaeLaptop
 *
 */
public class SROCancelCommand extends FlowBase
{
	public SROCancelCommand()
	{}
}


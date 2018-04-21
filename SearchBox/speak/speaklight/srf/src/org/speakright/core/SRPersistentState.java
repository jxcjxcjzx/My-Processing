/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Manages saving & restoring state of an SRInstance.  This is done between each HTTP request,
 * since Java servlets are stateless.
 * 
 * Uses java serialization.
 * @author Ian Rae
 *
 */
public class SRPersistentState {

	/**
	 * The SRInstance that has been saved or restored.  Contains everything needed to
	 * resume execution of the instance.
	 */
	public SRInstance m_run; 
	
	/**
	 * a unique key that can be used to find the right saved state.  If files are used
	 * then the key can simply be the filename.
	 */
	public String m_streamId = "";
	
	SRError m_err = new SRError("SRPersistentState");

	public SRPersistentState()
	{}
	
	/**
	 * Constructor used when saving state.
	 * @param run  The instance to be saved.
	 * @param streamId  The stream id that identifies the saved state.
	 */
	public SRPersistentState(SRInstance run, String streamId)
	{
		m_run = run;
		m_streamId = streamId;
	}
	
	/** Saves the state to the given output stream.
	 * 
	 * @param outStream  output stream (can be a file, memory, etc)
	 * @param streamId  Stream id that uniquely identifies the saved state.
	 * @return success.
	 */
	public boolean passivate(OutputStream outStream, String streamId)
	{
		boolean b = false;
		try
		{
		ObjectOutputStream out = new ObjectOutputStream(outStream);
		out.writeObject(m_run);
		b = true; //success
		m_streamId = streamId;
		}
		catch(java.io.NotSerializableException e)
		{
			System.out.println("NSE: " + e.getMessage());
//			e.printStackTrace();
			Throwable thr = e.getCause();
			if (thr != null){
				System.out.println("NSEX: " + thr.getMessage());
				
			}
			
//			e.
			//System.out.println(e);
			
		}
		catch(Exception e)
		{
			CoreErrors.logError(m_err, CoreErrors.SerializationFailed, 
					String.format("exception %s: %s", e.getClass().getName(), e.getLocalizedMessage()));
		}
		return b;
	}

	/** Restore the saved state and recreate an SRInstance ready
	 *  to resume execution.
	 * 
	 * @param inStream  The input stream containing the saved state
	 * @return success
	 */
	public boolean activate(InputStream inStream)
	{
		boolean b = false;
		try
		{
		ObjectInputStream in = new ObjectInputStream(inStream);
		m_run = (SRInstance)in.readObject();
		b = true; //success
		
		}
		catch(Exception e)
		{}
		return b;
	}
}

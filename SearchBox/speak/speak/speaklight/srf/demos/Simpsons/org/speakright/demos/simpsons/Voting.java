package org.speakright.demos.simpsons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import org.speakright.core.SRLocations;
import org.speakright.core.SRLogger;
import org.speakright.core.SRUtils;

public class Voting {
	SRLocations m_loc;
	
	public Voting(SRLocations loc) {
		m_loc = loc;
	}

//	static Voting the_singleton;
//	public static Voting create(SRLocations loc)
//	{
//		if (the_singleton == null) {
//			the_singleton = new Voting(loc);
//		}
//		return the_singleton;
//	}
	
	public void recordVote(int characterId, String name)
	{
		int id = characterId;
		log(String.format("VOTE recorded for %d (%s)", id, name));
		
		String path = getPath();
		Date dt = new Date();
		write(path, String.format("%d: %s: %s", id,name, dt.toString()));
	}
	
	String getPath()
	{
		String path = SRLocations.fixupDir(m_loc.projectDir());
		String sep = java.io.File.separator;
		path += "results" + sep + "vote_results.txt";
		return path;
	}
	
	void log(String msg)
	{
		SRLogger logger = SRLogger.createLogger();
		logger.log(msg);
	}
	
	/**
	 * write the vote by appending a text file.  synchronized
	 * to make it mostly thread-safe.
	 * This is a simplistic implementation of a a database to store results.
	 * We won't have high volume on this app.
	 * @param path
	 * @param content
	 * @return
	 */
	synchronized boolean write(String path, String content)
	{
		boolean b = false;
		try
		{
			BufferedWriter w = new BufferedWriter(new FileWriter(path, true)); //append
			w.write(content);
			w.newLine();
			w.close();
			b = true;
		}
		catch(IOException e)
		{
			log("Exception: " + e.getLocalizedMessage());
		}
		return b;
	}
	
	ArrayList<String> getResults()
	{
		ArrayList<String> L = new ArrayList<String>();
		
//		L.add("3:Bart:5");
//		L.add("1:Homer:15");
//		L.add("2:Marge:3");
		
		String path = getPath();
		Hashtable<Integer,Integer> map = new Hashtable<Integer, Integer>();
		Hashtable<Integer,String> nameMap = new Hashtable<Integer, String>();
		boolean b = false;
		try
		{
			log("reading.. " + path);
			BufferedReader r = new BufferedReader(new FileReader(path));
			
			String line;
		    while ((line = r.readLine()) != null) {
		    	line = line.trim();
//		    	log("line: " + line);
		    	String[] ar = line.split(":");
		    	if (ar.length > 1) {
		    		String s = ar[0];
		    		int id = SRUtils.safeToInt(s);
		    		Object o = map.get(id);
		    		if (o == null) {
		    			map.put(id, 1);
		    			nameMap.put(id, ar[1].trim());
		    		}
		    		else {
		    			Integer votes = (Integer)o;
		    			votes++; //increment
		    			map.put(id, votes);
		    		}
		    	}
		    }
		    	  
			r.close();
			b = true;
		}
		catch(IOException e)
		{
			log("Exception: " + e.getLocalizedMessage());
		}
		
		
		//build results from map, sorted by votes (desc)
		while(map.size() > 0) {
			Integer id = getMax(map);
			Integer votes = (Integer)map.get(id);
			String name = (String)nameMap.get(id);
			
			String s = String.format("%d:%s:%s", id, name, votes);
			L.add(s);
			log("xs:" + s);
			map.remove(id);
		}
		
		return L;
	}

	/**
	 * Get the id with the most votes,
	 * @param map
	 * @return id or Integer.MIN_VALUE if map is empty
	 */
	Integer getMax(Hashtable map)
	{
		Integer maxVotes = Integer.MIN_VALUE;
		Integer idOfMaxVotes = Integer.MIN_VALUE;
		Enumeration keys = map.keys();
		while ( keys.hasMoreElements()) {
			Integer id = (Integer)keys.nextElement();
			Integer votes = (Integer)map.get(id);
			if (votes > maxVotes) {
				maxVotes = votes;
				idOfMaxVotes = id;
			}
		} 		
		return idOfMaxVotes;
	}
}

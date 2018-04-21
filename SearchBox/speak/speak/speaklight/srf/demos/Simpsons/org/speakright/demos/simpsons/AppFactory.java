package org.speakright.demos.simpsons;

import org.speakright.core.ModelBinder;
import org.speakright.core.SRFactory;
import org.speakright.core.SRInstance;
import org.speakright.core.SRLocations;
import org.speakright.core.SRRunner;
import org.speakright.itest.SRInteractiveTester;
import org.speakright.servlet.ISRServlet;

public class AppFactory extends SRFactory {

	public Model M;
	
	@Override
	public void onCreateRunner(SRRunner run) 
	{
		run.log("in onCreateRunner..");
		String path = run.locations().projectDir();
		run.registerPromptFile(path + "prompts.xml");

		M = new Model();
		run.setModelBinder(M, new ModelBinder(M));
		
		SRLocations locations = new SRLocations();
		locations.setProjectDir(path);
		M.m_voting = new Voting(locations);
	}
}

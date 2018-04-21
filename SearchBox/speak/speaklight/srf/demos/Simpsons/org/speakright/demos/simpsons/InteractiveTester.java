package org.speakright.demos.simpsons;
/*
 *
 * If you get a LOG4J startup error, be sure to have copied log4j.properties to the srf\bin directory
 */

import org.speakright.core.*;
import org.speakright.itest.SRInteractiveTester;

public class InteractiveTester {

	public static String appDir = "C:\\Source\\speakright\\srf\\demos\\Simpsons\\org\\speakright\\demos\\simpsons\\";
	public static String dir = appDir + "tests\\testfiles\\";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("simpsons demo..");
		SRInteractiveTester tester = createTester();
		tester.run();
		System.out.println("Done.");
	}

	/**
	 * Creates and initializes the app and tester.
	 * @return tester that is ready to be run.
	 */
	public static SRInteractiveTester createTester() {
		SRInteractiveTester tester = new SRInteractiveTester();
	    String currentDir = SRLocations.fixupDir(System.getProperty("user.dir"));
		tester.setOutputDir(currentDir + "tmpfiles");
		
		AppFactory factory = new AppFactory();
		SRRunner runner = factory.createRunner(appDir, "http://def.com", "", null);

		//change voting path to dir (instead of appDir)
		SRLocations locations = new SRLocations();
		locations.setProjectDir(dir);
		factory.M.m_voting = new Voting(locations);
		
		SRInstance run = runner.getImpl(); 
		App app = new App();
		tester.init(app, run); 
//			tester.start(app, M, locations);
//		run.registerPromptFile(appDir + "prompts.xml"); //our app prompts
		return tester;
	}
}

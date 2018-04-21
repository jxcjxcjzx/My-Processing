/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.demos.sampleservlet;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.speakright.core.ModelBinder;
import org.speakright.core.SRRunner;
import org.speakright.core.flows.DisconnectFlow;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.SRApp;
import org.speakright.core.tests.Model;
import org.speakright.servlet.*;
import org.speakright.sro.*;

/**
 * Servlet implementation class for Servlet: App1
 *
 */
 public class App1 extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet, ISRServlet {
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public App1() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SRServletRunner runner = new SRServletRunner(this, request, response, "GET");

		if (runner.isNewSession()) {
	        SRRunner run = runner.createNewSRRunner(this);
    
	        SRApp flow = new SRApp();
	        flow.addPromptFlow("Welcome to the Speak-Right demo application. {..}");
	        
	        DateFormat fDateFormat = DateFormat.getDateInstance(DateFormat.LONG);
	        Date now = new Date ();
	        String date_out = fDateFormat.format (now);
	       
	        flow.addPromptFlow(String.format("Today is %s. {..}", date_out));

	        flow.add(new SRONumber("number", 1, 10));
	        flow.addPromptFlow("You said {$INPUT}.");
	        
	        flow.add(new DisconnectFlow("good bye for now. {..}"));
	        runner.startApp(flow);
		}
		else {
			runner.logger().log("contine in GET!!");
			runner.continueApp();
		}
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SRServletRunner runner = new SRServletRunner(this, request, response, "POST");

		if (runner.isNewSession()) {
			//err!!
			runner.logger().log("can't get new session in a POST!!");
		}
		else {
			runner.continueApp();
		}
	}

	public void initLocations(SRRunner run) {
	}   
 }
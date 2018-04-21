package org.speakright.itest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleReader implements SRInteractiveTester.CommandReader {

	public String readLine(int turnNumber) {
		InputStreamReader stdin = new InputStreamReader(System.in);
		BufferedReader console = new BufferedReader(stdin);

		String line = "";
		try
		{
			System.out.print(String.format("%d> ", turnNumber + 1));
			line = console.readLine();
			line = line.trim();
		}
		catch(Exception e)
		{}
		return line;
	}

}

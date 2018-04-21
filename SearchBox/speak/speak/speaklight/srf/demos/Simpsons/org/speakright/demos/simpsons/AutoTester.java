package org.speakright.demos.simpsons;

import org.speakright.core.IFlow;
import org.speakright.core.SRError;
import org.speakright.core.SRInstance;
import org.speakright.core.SRLogger;
import org.speakright.itest.SRAutoTester;
import org.speakright.itest.SRInteractiveTester;

public class AutoTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SRLogger logger = SRLogger.createLogger();
		
		int numTests = tests.length;
		logger.log("Simpsons demo AUTO-TESTER..." + numTests + " tests.");
		
		boolean failed = false;
		for(int i = 1; i <= numTests; i++) {
			logger.log("=============== TEST #" + i + " ===========================");
			if (! failed) {
				String cmds = tests[i - 1];
				SRAutoTester tester = new SRAutoTester(i, cmds);
				if (!tester.run(InteractiveTester.createTester())) {
					failed = true;
				}
			}
		}
		logger.log("TESTS FINISHED. ");
	}

	static String[] tests = {
	//1: no twice and back to ask-character
	"e;g~PromptFlow;g~AskCharacter;g x:3~CharacterDescription;g~VoteYesNo;g no~RelatedCharacterYesNo;g no~AskCharacter;q",

	//2: no + yes to hear related char
	"e;g~PromptFlow;g~AskCharacter;g x:3~CharacterDescription;g~VoteYesNo;g no~RelatedCharacterYesNo;g yes~CharacterDescription;q",

	//3: yes to mainmenu
	"e;g~PromptFlow;g~AskCharacter;g x:3~CharacterDescription;g~VoteYesNo;g yes~SayVoted;g;g results~VotingResultsList;g~QuestionFlow;q",

	//4: mainmenu and back to ask-character
	"e;g;g;g x:2;g~VoteYesNo;g yes~SayVoted;g;g results~VotingResultsList;g~QuestionFlow;g character~AskCharacter;q",

	//5: voting results
	"e;g;g;g x:2;g;g yes;g;g results~VotingResultsList;g results~VotingResultsList;q",
	
	//6: cant vote twice
	"e;g;g;g x:2;g;g yes;g;g~QuestionFlow;g character;g x:3;g;g yes~AlreadyVoted;g~QuestionFlow;q",
	};		
}

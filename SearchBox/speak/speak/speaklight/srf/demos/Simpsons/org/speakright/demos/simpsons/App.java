package org.speakright.demos.simpsons;

/*
 * to do:
 * add goodbye
 * call stats
 * speakright blurb
 * finish simpsons demo
 * basesro should get its prompts from xml
 * the 's problem
 * learn ant
 */

/* improvements nov 24 2007
 * 

new things
-app.Loop
-iloop.shouldexecute
-flowlist - several ctors.  last param string that is customevent
-fix M so don't set M.alreadyVoted.get and .set
-fix so don't have to do m_loc = context.getLocations(); in voteyesno.execute
-rename CustomEvent to AppEvent
-move BasicFlow to .flows

class SimpsonsDemo extends SRApp

app()
{}

getFirst()
 return Welcome("sdfd");
// can return a complex sub-flow here or just a welcome prompt


//optional. default returns null, which means do normal srapp action
//can return the loop, or your own flow object if you like
IFlow InitLoop(LoopFlow loop)
 loop.add(new AskCharacter());
 loop.add(new CharacterDescription());
 loop.add(new VoteYesNo());
 loop.add(new RelatedCharacterYesNo());
 return loop

OnCatch(...)
  if IsGotoEvent("MainMenu", ev)
    return new MM
  
  return super.oncatch()

-----
AskCharacter
 bool shouldExecute()
   return M.CharId not empty

 
VoteYesNo

OnYes
 if M.alreadyVoted
    return new FlowList(new AlreadyVoted, "MainMenu"); 
 else
    recordVote();
    return new FlowList(new SayVoted, "MainMenu");
*/

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.speakright.core.AppEvent;
import org.speakright.core.IExecutionContext;
import org.speakright.core.IFlow;
import org.speakright.core.IFlowContext;
import org.speakright.core.IModelItem;
import org.speakright.core.SRLocations;
import org.speakright.core.SRResults;
import org.speakright.core.SRUtils;
import org.speakright.core.ThrowEvent;
import org.speakright.core.flows.BasicFlow;
import org.speakright.core.flows.BranchFlow;
import org.speakright.core.flows.ChoiceFlow;
import org.speakright.core.flows.FlowList;
import org.speakright.core.flows.LoopFlow;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.QuestionFlow;
import org.speakright.core.flows.RecordAudioFlow;
import org.speakright.core.flows.SRApp;
import org.speakright.core.render.Grammar;
import org.speakright.core.render.InlineGrammar;
import org.speakright.core.render.Prompt;
import org.speakright.core.tests.MyFlow;
import org.speakright.sro.BaseSROQuestion;
import org.speakright.sro.SROCancelCommand;
import org.speakright.sro.SROListNavigator;
import org.speakright.sro.SROStringItem;
import org.speakright.sro.SROSubjectItem;
import org.speakright.sro.SROYesNo;
import org.speakright.core.IItemReusableFormatter;

public class App extends SRApp
{
	public Model M;
		
	@Override
	protected IFlow createWelcome()
	{
		return new PromptFlow("id:welcome");
	}
	@Override
	protected void initMainLoop(LoopFlow loop)
	{
		loop.add(new AskCharacter());
		loop.add(new CharacterDescription());
		loop.add(new VoteYesNo());
		loop.add(new RelatedCharacterYesNo());
	}		
	
//	static String BRANCH_CHOOSE_CHARACTER = "chooseCharacter";
	static String BRANCH_START_AGAIN = "again";
	static String BRANCH_MAIN_MENU = "mainMenu";

	@Override
	public void onBegin(IFlowContext context) {
		M.currentCharacterId().clear();
		M.haveVoted().clear();

		super.onBegin(context);
	}
		
	@Override
	public IFlow onCatch(IFlow current, SRResults results, String eventName, ThrowEvent event) {
		if (isAppEvent(event, App.BRANCH_MAIN_MENU)) {
			return new MainMenu();
		}
		else if (isAppEvent(event, App.BRANCH_START_AGAIN)) {
			M.currentCharacterId().clear(); //so we ask-char again
			
			//catching this event causes the MainLoop flow object to be popped off the flow stack,
			//so if we want to run it again we must explicitly return it.  
			return getMainLoop(); //run main loop again
		}
		return super.onCatch(current, results, eventName, event);
	}		
		
	public static class AskCharacter extends BaseSROQuestion {
		public Model M;

		//moved initialization to onBegin so we can refer to M
		
		@Override
		public boolean shouldExecute() {
			if (M.currentCharacterId().get() == 0) { //no char selected?
				return true;
			}
			return false;
		}

		@Override
		public void onBegin(IFlowContext context) {
			m_slotName = "x";
			m_modelVar = M.ModelVarNames.CURRENTCHARACTERID;
			
			m_main1Prompt = "id:main1"; //"Say the name of a Simpson's character";
//			Grammar gram = new InlineGrammar("1 2 3 4 5 6 7 8 9 10 11 12"); //character ids
			addGrammar("grammar/simpsons.gsl#CHOOSE");
		}

		@Override
		public IFlow getNext(IFlow current, SRResults results) {
			int id = SRUtils.safeToInt(results.m_input);
			M.characterName().set(M.m_simpsons.getName(id));
			return super.getNext(current, results);
		}
	}

	public static class CharacterDescription extends PromptFlow {
		public Model M;
		
		@Override
		public void execute(IExecutionContext context) {
			int id = M.currentCharacterId().get();
			
			String s = M.m_simpsons.getDescription(id);
			log("id " + id + "," + s);
			addPrompt(s);
			super.execute(context);
		}
	}
	
	public static class VoteYesNo extends SROYesNo {
		public Model M;
		
		@Override
		public IFlow onYes() {
			if (M.haveVoted().get()) {
				String ptext = "You can only vote once per phone call.";
				return new FlowList(new PromptFlow("AlreadyVoted", ptext), App.BRANCH_MAIN_MENU);
			}
			recordVote();
			
			String ptext = "Your vote has been recorded.";
			return new FlowList(new PromptFlow("SayVoted", ptext), App.BRANCH_MAIN_MENU);
		}
		
		void recordVote()
		{
			Voting voting = M.m_voting;
			int id = M.currentCharacterId().get();
			String name = M.m_simpsons.getName(id);
			voting.recordVote(id, name);
			M.haveVoted().set(true);
		}
	}

	public static class RelatedCharacterYesNo extends SROYesNo {
		public Model M;

		@Override
		public void execute(IExecutionContext context) {
			SimpsonsData data = M.m_simpsons;
			int id = M.currentCharacterId().get();
			String name = data.getName(id);
			
			this.m_main1Prompt = String.format("Do you want to hear about %s's %s %s?", name, 
					data.getRelation(id),
					data.getName(data.getRelatedId(id)));
			super.execute(context);
		}

		@Override
		public IFlow onYes() {
			SimpsonsData data = M.m_simpsons;
			int id = M.currentCharacterId().get();
			int relatedId = data.getRelatedId(id);
			M.currentCharacterId().set(relatedId); //set related char
			M.characterName().set(data.getName(relatedId));
			return null;
		}

		@Override
		public IFlow onNo() {
			M.currentCharacterId().clear(); //so we ask-char again
			return null; //done. do mainloop again
		}
	}
	
	public static class MainMenu extends ChoiceFlow 
	{
		public Model M;

		public MainMenu() {
			super("mainmenu");
			String s = "Main Menu.  You can say: choose character, voting results, call statistics, or speakright";
			QuestionFlow quest = new QuestionFlow("inline:character results statistics speakright", s);
			setChoiceQuestion(quest);
			
			addChoice("character", new AppEvent(App.BRANCH_START_AGAIN));
			addChoice("results", new VotingResultsList());
			addChoice("statistics", new PromptFlow("This feature has not yet been implemented.")); //new CustomEvent("restart"));
			addChoice("speakright", new PromptFlow("SpeakRight is an open-source Java framework for speech applications using Voice XML."));
		}
	}
	
	public static class VotingResultsList extends SROListNavigator implements IItemReusableFormatter
	{
		public Model M;
		
		public VotingResultsList()
		{
			super("places");
	        addCommand("menu", new SROCancelCommand());
	        setItemFormatter(this); //we'll format the items
		}
		
		@Override
		public void onBegin(IFlowContext context) {
			//set the list now (at the time the user says 'results')
			Voting voting = M.m_voting;
			this.initList(voting.getResults());
		}
		
		transient SROStringItem m_item; //current item
		public void setItem(IModelItem item) {
			m_item = (SROStringItem)item;
		}
		public String formatItem() {
			String[] ar = m_item.get().split(":");
			
			//3:Bart:15
			int num = SRUtils.safeToInt(ar[2]);
			
			SROSubjectItem voteWord = new SROSubjectItem("votes", num, true);
			String s = voteWord.getFormattedItem();
			return String.format("%s for %s", s, ar[1]);
		}
	}
}

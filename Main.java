import java.util.ArrayList;

/**
 * Testing the organisms game
 * @author DannyR
 *
 */
public class Main {
//need to make a wrapper class 'organism' or something similar that will hold the state of the organism,
//move all those methods into the wrapper class
	public static void main(String[] args) {
		MyGameConfig mgc = new MyGameConfig();
//		HumanPlayer Danny = new HumanPlayer();
		RandomPlayer Banana = new RandomPlayer();
		ComputerPlayer compy = new ComputerPlayer();
		ArrayList<Player> players = new ArrayList<Player>();
//		players.add(Danny);
		players.add(Banana);
		players.add(compy);
		MyOrganismsGame myORG = new MyOrganismsGame();
		myORG.initialize(mgc, .009, .02, players);

	}
	
	//any 'instanceof' also needs to work with computer player

}

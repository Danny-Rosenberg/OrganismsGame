import java.util.ArrayList;

/**
 * Testing the organisms game
 * @author DannyR
 *
 */
public class Main {

	public static void main(String[] args) {
		MyGameConfig mgc = new MyGameConfig();
		HumanPlayer Danny = new HumanPlayer();
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(Danny);
		//gotta put the players on the board!
		MyOrganismsGame myORG = new MyOrganismsGame();
		myORG.initialize(mgc, .009, .02, players);

	}
	
	//board printing over and over
	//set the 'status' for all the moves

}

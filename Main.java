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
		HumanPlayer Banana = new HumanPlayer();
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(Danny);
		players.add(Banana);
		MyOrganismsGame myORG = new MyOrganismsGame();
		myORG.initialize(mgc, .009, .02, players);

	}
	
	//any 'instanceof' also needs to work with computer player

}

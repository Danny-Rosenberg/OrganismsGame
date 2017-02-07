import java.util.ArrayList;

public class OrganismsTester {

	public static void main(String[] args) {
		
		GameConfig testConfig = new MyGameConfig();
		ArrayList<Player> players = new ArrayList<Player>();
		//some random generation of 'keys/ids'
		RandomPlayer Danny = new RandomPlayer();
		Danny.register(testConfig, 10);
		RandomPlayer Clone = new RandomPlayer();
		Clone.register(testConfig, 20);
		players.add(Danny);
		players.add(Clone);
		
		for(int i = 0; i < 501; i++){
		//some random generation of p and q
		MyOrganismsGame MOG = new MyOrganismsGame(); 
		MOG.initialize(testConfig, .05, .05, players);
		
		}
		
		
	}

}

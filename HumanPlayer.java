import java.util.Arrays;
import java.util.Scanner;

/**
 * This is the Human Player for the Organisms game.
 * This class will provide for manual decision making of the organism
 * Let's make a game
 * @author DannyR
 *
 */
public class HumanPlayer implements Player {

	private static final String NAME = "Danny";
	private MyGameConfig myGame;
	private int key;
	
	/**
	 * This is the registration method. The game object is passed, so we know 
	 * the parameters of the round
	 */
	@Override
	public void register(GameConfig game, int key) {
		this.key = key;
		if (game instanceof MyGameConfig){
			MyGameConfig myGame = (MyGameConfig) game;
			this.myGame = myGame;
		}
		 //can I cast this to MyGameConfig somehow?
	}

	@Override
	public String name() {
		return NAME;
	}

	/**
	 * /*
	 * This is called by the Game to determine how this Organism should move.
	 * @param food a five-element array that indicates whether any food is in adjacent squares
	 * @param neighbors a five-element array that holds the details for any organism in an adjacent square. -1 is no organism present, any value >= 0 if organism present
	 * 
	 * @param foodleft how much food is left on the current square
	 * @param energyleft the organism's remaining energy
	 * @return
	 */
	@Override
	public Move move(boolean[] food, int[] neighbors, int foodleft, int energyleft) {
		
		Move m = null; //lifted from RandomPlayer. Thanks RandomPlayer!
		
		System.out.printf("Hi player. You've got %d energy left. \n", energyleft);
		System.out.printf("There's %d food left on this square. \n", foodleft);
		System.out.printf("There are players in these positions: %s \n", Arrays.toString(neighbors));
		System.out.printf("There is food in these positions: %s \n", Arrays.toString(food));
		
		System.out.println("choose your move: ");
		Scanner in = new Scanner(System.in);
		
		int direction = in.nextInt();
		
		switch (direction) {
		case 0:
			m = new Move(Constants.STAYPUT);
			break;
		case 1:
			m = new Move(Constants.WEST);
			break;
		case 2:
			m = new Move(Constants.EAST);
			break;
		case 3:
			m = new Move(Constants.NORTH);
			break;
		case 4:
			m = new Move(Constants.SOUTH);
			break;
		case 5:
			Scanner again = new Scanner(System.in);
			System.out.println("Call the wet nurse! Which direction will your baby be in?");
			int baby = again.nextInt();
			if (baby == 1)
			m = new Move(Constants.REPRODUCE, 1, key);
			else if (direction == 2)
				m = new Move(Constants.REPRODUCE, 2, key);
			else if (direction == 3)
				m = new Move(Constants.REPRODUCE, 3, key);
			else if (direction == 4)
				m = new Move(Constants.REPRODUCE, 4, key);
			else {
				System.out.println("if you can't type a valid input, you can't have a baby");
				m = new Move(Constants.STAYPUT);
			}
		}
		
		return m;		
		
		
	}
	
	
	

}

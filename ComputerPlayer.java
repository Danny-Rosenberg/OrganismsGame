import java.util.Arrays;

/**
 * Computer players will play the game automatically
 * @author DannyR
 *
 */
public class ComputerPlayer implements Player {
		
	private static final String NAME = "compy";
	private int key;
	private MyGameConfig myGame;
	
	@Override
	public void register(GameConfig game, int key) {
		this.key = key;
		if (game instanceof MyGameConfig){
			MyGameConfig myGame = (MyGameConfig) game;
			this.myGame = myGame;
		}

	}

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public Move move(boolean[] food, int[] neighbors, int foodleft, int energyleft) {
		Move m = null;
		int direction = 0;
		
		if(energyleft > 100 && energyleft < 150){
			if(Arrays.asList(neighbors).contains(-1)){
//				for()
//				int step = 
			}
			
			m = new Move(direction);
			
		}
		
		
		return m;
	}
	


}

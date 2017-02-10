import java.util.Arrays;

/**
 * A very conservative computer player
 * @author DannyR
 *
 */
public class ComputerPlayer implements Player {
		
	private static final String NAME = "compy";
	private int key;
	private GameConfig game; //you won't have this available when you play...
	
	@Override
	public void register(GameConfig game, int key) {
		this.key = key;
		this.game = game;
//		if (game instanceof MyGameConfig){
//			MyGameConfig myGame = (MyGameConfig) game;
//			this.game = myGame;
//		}

	}

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public Move move(boolean[] food, int[] neighbors, int foodleft, int energyleft) {
		Move m = null;
		int direction = 0;
		//eating food
		if(food[0] == true && energyleft < game.M()){
			m = new Move(0); //stay put
			return m;
		}
		else if (food [0] == true){ //make a baby, stay in the spot
			for(int i = 1; i < 5; i++){
				if(neighbors[i] == -1){
					 m = new Move(5, i, key); //once you find an avaialable spot, break;
					 break;
				}
				else{
					m = new Move(0); //stay put
				}
			}
			return m;
		}
	
		
		//making babies
		if(energyleft > 100 && energyleft < 150){
				for(int i = 1; i < 5; i++){
					if(neighbors[i] == -1){
						 m = new Move(5, i, key); //once you find an avaialable spot, break;
						 break;
					}
					else{
						m = new Move(0); //stay put
					}
				}
				return m;
			}
		//moving
		else{
			for(int i = 1; i < 5; i++){
				if(neighbors[i] == -1 && food[i] == true){
					m = new Move(i); //tendency to move west
					break;
				}
				else{
				  if(neighbors[i] == -1){
					m = new Move(i); //tendency to move west
					break;
				        }
				  else{
					  m = new Move(0); //stayput
				    }
				}
	
			 }
			return m;
		}
			
	
	}

}
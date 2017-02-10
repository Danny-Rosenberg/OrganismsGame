
public class Organism {
	
	private int key; //the key/id assigned by the game
	private MyGameConfig myGame; //can use this data to make decisions
	private int energyLeft; //the energy left of the player
	private boolean moveFlag; //if false, player has not moved yet this turn, true they have
	private Player player; //holding a player
	
	
	public Organism(Player player, int energyLeft, int key, MyGameConfig myGame){
		this.moveFlag = false;
		this.energyLeft = energyLeft;
		this.myGame = myGame;
		this.key = key;
		this.player = player;
	}
	
	/**
	 * @return the moveFlag
	 */
	public boolean isMoveFlag() {
		return moveFlag;
	}

	/**
	 * @param moveFlag the moveFlag to set
	 */
	public void setMoveFlag(boolean moveFlag) {
		this.moveFlag = moveFlag;
	}

	/**
	 * @return the energyLeft
	 */
	public int getEnergyLeft() {
		return energyLeft;
	}

	/**
	 * @param energyLeft the energyLeft to set
	 */
	public void setEnergyLeft(int change) {
		energyLeft += change;
	}
	
	/**
	 * Getter method for key. Exposing key so PlayerRoundData get access it
	 * @return key
	 */
	public int getKey(){
		return key;
	}
	
	/**
	 * This method adds energy when a player eats
	 * Really should be called 'feed' 
	 * 
	 */
	public void eat(){
		setEnergyLeft(myGame.getU());
	}

	public Player getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}
	
	public void setPlayer(Player player){
		this.player = player;
		
	}

	
}

/**
 * 
 * This class implements PlayerRoundData and keeps track of player stats for each turn
 * @author DannyR
 */
public class MyPlayerRoundData implements PlayerRoundData {
	//How does this class make separate copies of data for each player? 
	//The player class only knows about a single organism, not its copies
	//So the game must know this
	//So there is one of these objects for each unique ID
	//so the game will be calling these methods on the class ie. asking it for information about the players,
	//you need to fill the information accordingly
	private int playerId; //could make these public
	private int energy;
	private int count;
	private Player player;
	

	/**
	 * Setter method for playerID
	 * @param id
	 */
	public void setPlayerId(int id){
		playerId = id;
	}
	
	/**
	 * The Player's unique id
	 */
	@Override
	public int getPlayerId() {
		return playerId;
	}
	
	/**
	 * Remaining energy for this Player
	 */
	@Override
	public int getEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Setter method for energy
	 * @param energyChange, how much energy has increased or decreased for the organism in the round
	 */
	public void setEnergy(int energyChange){
		energy += energyChange;
	}

	/**
	 * The total number of organisms of this type
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Setter method for organism count
	 * @param countChange
	 */
	public void setCount(int countChange){
		count += countChange;
	}
	

}

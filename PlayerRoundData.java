/**
 * The Interface for keeping track of how each organism does in a round
 * @author swapneel
 *
 */
public interface PlayerRoundData {
	
	/**
	 * A Unique Player ID to distinguish different players
	 * @return player id
	 */
	public int getPlayerId();
	
	/**
	 * Total Energy Left for this type of organism
	 * @return the total energy
	 */
	public int getEnergy();
	
	/**
	 * The Total Count for this type of organism
	 * @return the total count
	 */
	public int getCount();

}

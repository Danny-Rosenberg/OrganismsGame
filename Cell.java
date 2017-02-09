import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains the information in each cell of the board
 * Data from cells will be provided to the game, who can then provide 
 * the data for the player 'move' method
 * @author DannyR
 *
 */
public class Cell {
	//Note: Each unit of food can double independent of the other food units on the cell
	//this doesn't seem very DRY - how to improve? Maybe don't need this
	private int s; //is it always 1?
	private int v;
	private int u; //energy per unit of food
	private int M;
	private int K; //max food units per cell
	private double p; //prob spontaneous appearance of food
	private double q; //prob of any food unit doubling
	private MyGameConfig MGC;
	private boolean covered; //leave for now
	private int food; //number of food units
	private boolean foodP; //presence of food
//	private int xCo; //the coordinates of the cells on the board -- not sure about this
//	private int yCo; //the coordinates of the cells on the boad -- not sure about this
	private int status; //0 no food no player, 1 food, 2 player
//	private HumanPlayer player; 
	private Organism organism; //this will do the same thing as Player
	
	/**
	 * @return the food
	 */
	public int getFood() {
		return food;
	}

	
	//lots of getters/setters with somewhat overlapping responsibility. Something to consider
	/**
	 * @return the foodP
	 */
	public boolean isFoodP() {
		return foodP;
	}

	/**
	 * @param foodP the foodP to set
	 */
	public void setFoodP(boolean foodP) {
		if(getOrganism() == null){
		}
		this.foodP = foodP;
	}
	
	//institute foodEaten() method, 
	//putting logic into game. If there is just gettign and setting, you decouple the game and cell
	/**
	 * This method will subtract the amount of food on this cell
	 */
	public void foodEaten(){ //maybe add some exception handling here
		food--;
		if (food == 0){
			setFoodP(false);
		}
	}
	
	/**
	 * The constructor. Takes the game configuration 
	 * @param MGC
	 */
	public Cell(MyGameConfig MGC, double p, double q){
		this.MGC = MGC;
		this.p = p;
		this.q = q;
	}
	
	/**
	 * This method will randomly determine the values of the cell, 
	 * and see that the organisms get fed
	 */
	public void generateValues(){
		if (covered == false && food == 0){ //if there's no food or organisms on the space...
			if (foodGen() == true){ //see if food spontaneously generates
				setFoodP(true);
				food = 1;
			}
		}
		if(covered == false){
			for(int i = 0; i < food; i++){
				if(foodDouble() == true){ //going through each food unit and seeing if it doubles
					food++;
				}
			}
		}
		//an organism starts their turn on the square (and by default eats) @TODO check on this
		if(covered == true && food > 0){ //'stay put' == true
			food--; //what happens first, the movement or the cell/food generation?
		}
	}
	
	/**
	 * This method dictates if food happens to appear on the square
	 */
	public boolean foodGen(){
		double chance;
		chance = Math.random();
//		chance = ThreadLocalRandom.current().nextDouble(0, 1); //@TODO test here
			if (chance < p){ //chance is something	
				return true;
			}
			else{
			return false;
			}
	}
	
	/**
	 * This method determines if food doubles
	 */
	public boolean foodDouble(){
		double chance;
		if(food < MGC.K()){
		chance = Math.random();
			if(chance < q){ //chance is something, need something real here
				return true; //and the food will double in 'generate' values
			}
			else{
				return false; //the food didn't double
			}
		}
		return false; //There is already the max amount of food on the cell
	}
	
	public double getP(){
		return p;
	}
	
	public double getQ(){
		return q;
	}
	
	public int getStatus(){
		return status;
	}
	
	public void setStatus(){
		if (getOrganism() == null && foodP == false){
			status = 0;
		}
		if (getOrganism() == null && foodP == true){
			status = 1;
		}
		if(getOrganism() != null){
			status = 2;
		}
	}
	
	
	/**
	 * Getter method for Player		
	 * @return player object
	 */
	public Organism getOrganism(){
		return organism;
	}
	
	/**
	 * Setter method for player on the cell
	 * @param organism
	 */
	public void setOrganism(Organism organism){
		if (organism != null){
			status = 1;
		}
		this.organism = organism;
	}
	
	/**
	 * Setter to update whether cell is covered or not
	 * @param cov
	 */
	public void setCovered(boolean cov){
		covered = cov;
	}
	
	
}

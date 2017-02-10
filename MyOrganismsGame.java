import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MyOrganismsGame implements OrganismsGameInterface {
	
	private int round; //will start at 5000
	private double p; //prob appearance of food
	private double q; //prob food doubling
	private ArrayList<Player> players; //must use this, but as little as possible
	private GameConfig game; //this should work in place of GameConfig b/c of liskov (?)
	private MyGameConfig MGC = new MyGameConfig();
	private MyOrganismsGame MOG;
	private ArrayList<PlayerRoundData> PRD; //gotta do something with this
	private Board board;
	private Cell currentCell; 
	private int[] organismBubble = {-1, -1, -1, -1, -1}; //Array that keeps track of which players are around the current cell
	private boolean[] foodBubble = new boolean[5]; //keeps track of which surrounding cells have food
	private Move move;
	private ArrayList<Organism> allOrgs = new ArrayList<Organism>();
	
	//need to model death, removing organisms from the board
	
	
	/**
	 * This method will initialize the game.
	 * Each game will run for 5000 rounds.
	 * Each player will start with an energy of 500 at the start of the game.
	 * @param game the GameConfig to run
	 * @param p the secret parameter p - probability of spontaneous appearance of food
	 * @param q the secret parameter q - probability of food doubling
	 * @param players the list of players
	 */
	@Override
	public void initialize(GameConfig game, double p, double q, ArrayList<Player> players) {
		setRound(5); //careful here, may not be the right place for this
		this.p = p;
		this.q = q;
		this.game = game; //shouldn't this still work with MyGameConfig?
		this.players = players;
		//assume more than 2 players
		//might need to do instanceof for myGameConfig
		this.board = new Board(10, 10, (MyGameConfig)game, this.p , this.q); //this may be a violation of liskov...
		this.PRD = new ArrayList<PlayerRoundData>(); //should I just cast this at the end?
		int i = 1;
		for(Player pro : players){ 
			pro.register((MyGameConfig)game, i); //not sure this cast is what I want here
			Organism org = new Organism(pro, 500, i, (MyGameConfig)game); //not sure this cast is what I want here
			allOrgs.add(org);
			MyPlayerRoundData myPRD = new MyPlayerRoundData();
			myPRD.setPlayerId(i); //why make things more complicated 
			myPRD.setEnergy(500); //how much food at the start?
			myPRD.setCount(1);
//			PlayerRoundData prd = (PlayerRoundData) myPRD;
			PRD.add(myPRD); //adding myPlayerRoundData types into a playerRoundData arraylist...could cause problems
			i++;
			}
		playGame();
		}
		

	/**
	 * This method will play the game for the given configuration
	 * Needs to go through and generate food for each round. 
	 * @return true if the game ended normally, false if exceptions were thrown or unexpected behavior
	 */
	@Override
	public boolean playGame() { //perhaps put methods in here that will throw excp...Or maybe in Move class
//		int rounds = 5000;
		int x = 0; //going through rows
		int y = 0; //going through columns
		Cell[][] ph = board.getBoard(); //does this 'this' matter here? Ultimately just want one board
		//setting players randomly on board:
			for(Organism org : allOrgs){
			int r = ThreadLocalRandom.current().nextInt(0, 10);
			int l = ThreadLocalRandom.current().nextInt(0, 10);
			if(ph[r][l].getOrganism() == null){
			    ph[r][l].setOrganism(org); 
			}
			else{
				if(r == 9)
				ph[r-1][l].setOrganism(org);
				else{
					ph[r+1][l].setOrganism(org);
				}
			}
			}

		
		while(round > 0){
			//creating a single round
			//laying down the food for a round
			for (x = 0; x < 10; x++){
				for(y = 0; y < 10; y++){
					ph[x][y].generateValues(); //check if this is changing the flag
					ph[x][y].setStatus(); //for printing purposes
				}
			}	
			//feeding organisms and moving them around
			for (y = 0; y < 10; y++){
				for(x = 0; x < 10; x++){
					currentCell = ph[x][y];
					Organism org = currentCell.getOrganism(); //p is always getting null, why? Why does this work?
					if(org != null){ //if there's a player in the cell
						//eating
						if(currentCell.isFoodP()){
						org.eat();
						currentCell.foodEaten();
						}
						else{
							//checking for a pulse...
							if(org.getEnergyLeft() == 0){ 
								currentCell.setOrganism(null);//remove the corpse
								currentCell.setCovered(false); //shouldn't be necessary, but to make sure
							}
							else{
								spaceCheck(board, x, y); //just switched -- what's up with the directions? 
								Move move = currentCell.getOrganism().getPlayer().move(foodBubble, 
										organismBubble, 
										currentCell.getFood(), 
										currentCell.getOrganism().getEnergyLeft()); 
								//if there are no babies
								if(move.childpos == 0){
									  if(move.type == 0){
										currentCell.getOrganism().setEnergyLeft(-1); //energy for not moving
									  }
								currentCell.getOrganism().setEnergyLeft( -MGC.v()); //energy taken even if you make a mistake	
								movePlayer(board, x, y, move.type()); 
								} 
								//There's a baby coming! where to put him?
								else{
									currentCell.getOrganism().setEnergyLeft(-MGC.v()); //subtracting energy 
									currentCell.getOrganism().setEnergyLeft(-(currentCell.getOrganism().getEnergyLeft() / 2));//pregancy is expensive
	//								
									Organism child = new Organism(org.getPlayer(), currentCell.getOrganism().getEnergyLeft(), 
											currentCell.getOrganism().getKey(), (MyGameConfig)game);
										moveChild(board, x, y, move.childpos(), child);
	
								}
								resetBubbles(); //setting all bubbles to default 
							}
						}						
					
				}
					
			  }
			}
			//A final iteration to update PlayerRoundData
					int n = 0;
					int m = 0;
			  for (m = 0; m < 10; m++){
				 for(n = 0; n < 10; n++){
					Organism org = currentCell.getOrganism(); //Pointing to same place in memory? Should be humanplayer?
					
					if(org != null){
						org.setMoveFlag(false); //resetting the movement flag
						currentCell.setCovered(true);
						currentCell.setStatus(); //for printing purposes
						updatePlayerRoundData(org); //back to normal x and y
					}
				    }	
				   }
//			  printBoard(board);
				round--;
			   }	
			  
		printResults();
		return true;
				 }
		

	/**
	 * This method will move players to different cells, and keep them in the same place if move is invalid
	 * @param board the board object
	 * @param x coordinate
	 * @param y coordinate
	 * @param direction where the player wants to go
	 */
	public void movePlayer(Board board, int x, int y, int direction){
		Cell[][] ph = this.board.getBoard(); //is this 'this' necessary? to reduce method chaining
		Organism org = ph[x][y].getOrganism(); //careful here with inheritance
		//stay put
		if(direction == 0){
			ph[x][y].getOrganism().setMoveFlag(true);
		}
		//move west
		if(direction == 1){
			if (organismBubble[1] == 1){
				ph[x][y].getOrganism().setMoveFlag(true); //you ain't goin nowhere
			}
			else{
				//checking wall
				if(x == 0){
					ph[x][y].setOrganism(null);
					ph[9][y].setOrganism(org); //check how to inheritance this shit
					ph[9][y].getOrganism().setMoveFlag(true);
					
				}
				else{
					ph[x][y].setOrganism(null);
					ph[x-1][y].setOrganism(org); //same thing
				}
			}
		}
		//move east
		if (direction == 2){
			if (organismBubble[2] == 1){
				ph[x][y].getOrganism().setMoveFlag(true); //you ain't goin nowhere
			}
			else{
				//checking wall
				if(x == 9){
					ph[x][y].setOrganism(null);
					ph[0][y].setOrganism(org); //check how to inheritance this shit
					ph[0][y].getOrganism().setMoveFlag(true);
				}
				else{
					ph[x][y].setOrganism(null);
					ph[x+1][y].setOrganism(org); //same thing
					//should be same as above??
					ph[x+1][y].getOrganism().setMoveFlag(true);
				}
			}
		}
		//move north
		if(direction == 3){
			if (organismBubble[3] == 1){
				ph[x][y].getOrganism().setMoveFlag(true); //you ain't goin nowhere
			}
			else{
				//checking wall
				if(y == 0){
					ph[x][y].setOrganism(null);
					ph[x][9].setOrganism(org); //check how to inheritance this shit
					ph[x][9].getOrganism().setMoveFlag(true);
				}
				else{
					ph[x][y].setOrganism(null);
					ph[x][y-1].setOrganism(org); //this made 10...
					ph[x][y-1].getOrganism().setMoveFlag(true);
				}
			}
		}
		//move south
		if(direction == 4){
			if (organismBubble[4] == 1){
				ph[x][y].getOrganism().setMoveFlag(true); //you ain't goin nowhere
			}
			else{
				//checking wall
				if(y == 9){
					ph[x][y].setOrganism(null);
					ph[x][0].setOrganism(org); //check how to inheritance this shit
					ph[x][0].getOrganism().setMoveFlag(true);
				}
				else{
					ph[x][y].setOrganism(null);
					ph[x][y+1].setOrganism(org); //same thing
					ph[x][y+1].getOrganism().setMoveFlag(true);
				}
			}
		}
		}
	
	
	public void moveChild(Board board, int x, int y, int direction, Organism child){
		Cell[][] ph = this.board.getBoard(); //to reduce method chaining
//		Player c = ph[x][y].getPlayer(); //Not actually creating the baby here. Baby lonely. 
		
		//move west
		if(direction == 1){
			if (organismBubble[1] == 1){
				ph[x][y].getOrganism().setMoveFlag(true); //no babymaking if you can't choose a valid direction
			}
			else{
				//checking wall
				if(x == 0){
					ph[9][y].setOrganism(child); 
					ph[9][y].getOrganism().setMoveFlag(true);
					
				}
				else{
					ph[x-1][y].setOrganism(child); 
					ph[x-1][y].getOrganism().setMoveFlag(true);
				}
			}
		}
		//move east
		if (direction == 2){
			if (organismBubble[2] == 2){
				ph[x][y].getOrganism().setMoveFlag(true); //no babymaking if you can't choose a valid direction
			}
			else{
				//checking wall
				if(x == 9){
					ph[0][y].setOrganism(child); 
					ph[0][y].getOrganism().setMoveFlag(true);
				}
				else{
					ph[x+1][y].setOrganism(child);
					ph[x+1][y].getOrganism().setMoveFlag(true);
				}
			}
		}
		//move north
		if(direction == 3){
			if (organismBubble[3] == 3){
				ph[x][y].getOrganism().setMoveFlag(true); //no babymaking if you can't choose a valid direction
			}
			else{
				//checking wall
				if(y == 0){
					ph[x][9].setOrganism(child); 
					ph[x][9].getOrganism().setMoveFlag(true);
				}
				else{
					ph[x][y-1].setOrganism(child); 
					ph[x][y-1].getOrganism().setMoveFlag(true);
				}
			}
		}
		//move south
		if(direction == 4){
			if (organismBubble[4] == 4){
				ph[x][y].getOrganism().setMoveFlag(true); //no babymaking if you can't choose a valid direction
			}
			else{
				//checking wall
				if(y == 9){
					ph[x][0].setOrganism(child);
					ph[x][0].getOrganism().setMoveFlag(true);
				}
				else{
					ph[x][y+1].setOrganism(child);
					ph[x][y+1].getOrganism().setMoveFlag(true);
				}
			}
		}
	  }
		
	
	
	/**
	 * @return the playerBubble
	 */
	public int[] getPlayerBubble() {
		return organismBubble;
	}

	/**
	 * @param playerBubble the playerBubble to set
	 */
	public void setOrganismBubble(int index, int status) {
		organismBubble[index] = status;
	}

	/**
	 * @return the foodBubble
	 */
	public boolean[] getFoodBubble() {
		return foodBubble;
	}

	/**
	 * @param foodBubble the foodBubble to set
	 */
	public void setFoodBubble(int index, boolean status) {
		foodBubble[index] = status;
	}
	

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}
	
	public void printBoard(Board board){
		int i = 0;
		int j = 0;
		Cell[][] ph = board.getBoard(); //@TODO ask about this - adding 'this' stopped infinite loop, but no printing
		for (i = 0; i < 10; i++){
			for (j = 0; j < 10; j++){
				System.out.printf(" %d ", ph[i][j].getStatus()); 
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	
	/**
	 * This method will check the cells around the player and set the movement arrays as it goes
	 * @return an int array illustrating where other organisms are located
	 * @param the current cell
	 * 
	 */
	public void spaceCheck(Board board, int x, int y){ //-1 if no organism is present, 1 is they are present
		//my board wraps directly around ie. 9,9 going east wraps to (0,9), not (0, 0)
		Cell[][] ph = this.board.getBoard(); //just to shorten the method chaining
		if(ph[x][y].isFoodP()){ //checking current space
			setFoodBubble(0, true);} //really there should be a third option
		setOrganismBubble(0, 1); 
		//check west
//		if(x == 0 && y == 0){ //check if it's on the upperwest side
//			if (ph[9][9].getOrganism() != null){
//				setFoodBubble(1, false);
//				setOrganismBubble(1, 1);
//			}
//		}
		if(x == 0){ //
			if(ph[9][y].getOrganism() != null) 
				setOrganismBubble(1, 1);
			else{
				if(ph[9][y].isFoodP()){
					setFoodBubble(1, true);
				}
			}
		}
		else{
		if(ph[x-1][y].getOrganism() != null){
			setOrganismBubble(1, 1);
		}
		
		else{
			setOrganismBubble(1, -1);
		   if(ph[x-1][y].isFoodP()){
			  setFoodBubble(1, true);
		    }
		}
		}
		//check east
		//check lower east side
//		if(x == 9 && y == 9){
//			if(ph[0][0].getOrganism() != null){
//				setOrganismBubble(2, 1);
//			}
//			else{
//				if(ph[0][0].isFoodP()){
//					setFoodBubble(2, true);
//				}
//			}
//		}	
		if(x == 9){ //checking east side wall
			if(ph[0][y].getOrganism() != null){
				setOrganismBubble(2, 1);
			}
			else{
				if(ph[0][y].isFoodP()){
					setFoodBubble(2, true);
				}
			}
		}
		
		else {
			if (ph[x+1][y].getOrganism() != null){ //out of bounds '10'
			setOrganismBubble(2, 1);
			}
			else {
				if (ph[x+1][y].isFoodP()){
				setFoodBubble(2, true);
				}
			}
		}	
		//check north
		if(y == 0){
			if(ph[x][9].getOrganism() != null){
				setOrganismBubble(3, 1);
			}
			else{
				if (ph[x][9].isFoodP()){
					setFoodBubble(3, true);
				}
			}
		}
		else{
			if(ph[x][y-1].getOrganism() != null){ //out of bounds 10
				setOrganismBubble(3, 1);
			
			}
			else{
				if (ph[x][y-1].isFoodP()){
					setFoodBubble(3, true);
				}
			}
		}
		//check south
		if(y == 9){
			if(ph[x][0].getOrganism() != null){
				setOrganismBubble(4, 1);
			}
			else{
				if(ph[x][0].isFoodP()){
					setFoodBubble(4, true);
				}
			}
		}
		else{
			if(ph[x][y+1].getOrganism() != null){ 
					setOrganismBubble(4, 1);
			}
			else{
				if (ph[x][y+1].isFoodP()){
					setFoodBubble(4, true);
					}
				}
		}
	}
		
	
	/**
	 * This method will set both bubble arrays back to default values
	 * boolean array will default to false
	 * player array will default to -1
	 */
	public void resetBubbles(){
		for(int i = 0; i < foodBubble.length; i++){
			foodBubble[i] = false;
		}
		for(int j = 0; j < organismBubble.length; j++){
			organismBubble[j] = -1;
		}
	}
	
	/**
	 * The list of results for all the players
	 * @return an ArrayList of PlayerRoundData objects
	 */
	@Override
	public ArrayList<PlayerRoundData> getResults() {
		return PRD;
	}
	
	
	/**
	 * This method takes an organism, and updates the relevant
	 * player round data
	 * @param org an organism on this cell
	 */
	public void updatePlayerRoundData(Organism org){
		int id = org.getKey();
		for(PlayerRoundData my : PRD){
			if(my instanceof MyPlayerRoundData){
				MyPlayerRoundData myPRD = (MyPlayerRoundData) my;
			
			if(id == myPRD.getPlayerId()){
				myPRD.setCount(1); 
				myPRD.setEnergy(org.getEnergyLeft()); 
				System.out.println(org.getEnergyLeft());
			}
			}
			//else, some kind of exception
		}
		
	}
	
//	/**
//	 * This method iterates through the board at the end of a round to collect data on the players
//	 * @param board
//	 * @param x coordinate
//	 * @param y coordinate
//	 */
//	public void updatePlayerRoundData(Board board, int x, int y){ //this is for a single cell!!!!!
//		Cell[][] ph = board.getBoard();
//		Organism org = ph[x][y].getOrganism();
//			int id = org.getKey(); //null pointer, how could an org not have a key?
//			for(PlayerRoundData my : PRD){
//				if(my instanceof MyPlayerRoundData){
//					MyPlayerRoundData myPRD = (MyPlayerRoundData) my;
//				
//				if(id == myPRD.getPlayerId()){
//					myPRD.setCount(1); //Inheritance!
//					myPRD.setEnergy(org.getEnergyLeft()); //Inheritance!
//				}
//				}
//				//else, some kind of exception
//			}
//		}

	
	/**
	 * A method for printing the data in player round data. A final scorecard for the game
	 */
	public void printResults(){
		for(PlayerRoundData res : PRD){
			if (res instanceof MyPlayerRoundData){
				MyPlayerRoundData myPRD = (MyPlayerRoundData) res;
			System.out.println("for player number: " + myPRD.getPlayerId());
			System.out.println(myPRD.getCount());
			System.out.println(myPRD.getEnergy());
			}
		}
	}
	
	public ArrayList<Player> getPlayers(){
		return players;
	}
	
	
	
	
}

import java.util.ArrayList;

public class MyOrganismsGame implements OrganismsGameInterface {
	
	private int round; //will start at 5000
	private double p; //prob appearance of food
	private double q; //prob food doubling
	private ArrayList<Player> players;
	private GameConfig game; //this should work in place of GameConfig b/c of liskov (?)
	private MyGameConfig MGC;
	private MyOrganismsGame MOG;
	private ArrayList<PlayerRoundData> PRD; //gotta do something with this
	private Board board;
	private Cell currentCell; 
	private int[] playerBubble; //Array that keeps track of which players are around the current cell
	private boolean[] foodBubble; //keeps track of which surrounding cells have food
	
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
		setRound(5000); //careful here, may not be the right place for this
		this.p = p;
		this.q = q;
		this.game = game; //shouldn't this still work with MyGameConfig?
		this.players = players;
		//assume more than 2 players
		//use reflections to create different arrays lists for different implementations of player
		//Then set values of myPRD, I suppose to treat arraylists of different types differently  
		//maybe should put this in playGame()...
		Board board = new Board(10, 10, (MyGameConfig)game, this.p , this.q); //this may be a violation of liskov...
		for(Player pro : players){
			if (pro instanceof HumanPlayer){
			HumanPlayer hp = (HumanPlayer) pro;	
			MyPlayerRoundData myPRD = new MyPlayerRoundData();
			myPRD.setPlayerId(hp.getKey()); //this is what it should look like, but having inheritance problems
			myPRD.setEnergy(100); //how much food at the start?
			myPRD.setCount(1);
			}
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
		int rounds = 500;
		int x = 0; //going through rows
		int y = 0; //going through columns
		Cell[][] ph = this.board.getBoard(); //does this 'this' matter here? Ultimately just want one board
		while(rounds > 0){
			//creating a single round
			//laying down the food for a round
			for (x = 0; x < 10; x++){
				for(y = 0; y < 10; y++){
					ph[x][y].generateValues();
				}
			}	
			//feeding organisms and moving them around
			for (x = 0; x < 10; x++){
				for(y = 0; y < 10; y++){
					currentCell = ph[x][y];
					if(currentCell.getPlayer() != null){ //if there's a player in the cell
						//eating
						HumanPlayer player = currentCell.getPlayer(); //Pointing to same place in memory?
						if(currentCell.isFoodP()){
						eat(player);
						currentCell.foodEaten();
						}
						else{
							//checking for a pulse...
							if(player.getEnergyLeft() == 0){ 
								//kill player - maybe remove from arraylist of players - depends how you reproduce
							}
							spaceCheck(board, x, y);
							Move move = currentCell.getPlayer().move(foodBubble, playerBubble, 
									currentCell.getFood(), currentCell.getPlayer().getEnergyLeft());
							//if there are no babies
							if(move.childpos == 0){
								if(move.type == 0){
									currentCell.getPlayer().setEnergyLeft(-1); //energy for not moving
								}
							currentCell.getPlayer().setEnergyLeft(-MGC.v()); //energy taken even if you make a mistake	
							movePlayer(board, x, y, move.type()); } //maybe overload method
							//There's a baby coming! where to put him?
							else{
								currentCell.getPlayer().setEnergyLeft(-MGC.v());
								try{
								Player child = currentCell.getPlayer().getClass().newInstance();
								}
								catch(Exception e){
									System.out.println("problem creating new instance"); System.exit(0);
								}
//								moveChild(board, x, y, move.childpos(), true); //how to keep player in same place gracefully?
							}
							resetBubbles(); //setting all bubbles to default 
						}						
							
					}
					
				}
			}
			//A final iteration to update PlayerRoundData
			for (x = 0; x < 10; x++){
				for(y = 0; y < 10; y++){
					if(currentCell.getPlayer() != null){
						currentCell.getPlayer().setMoveFlag(false); //resetting the movement flag
						updatePlayerRoundData(board, x, y);
					}
				}
				
			}	
			
			printBoard();
			rounds--;
		}
		
		printResults();
		
		return false;
	}

	/**
	 * This method will move players to different cells, and keep them in the same place if move is invalid
	 * @param board the board object
	 * @param x coordinate
	 * @param y coordinate
	 * @param direction where the player wants to go
	 */
	public void movePlayer(Board board, int x, int y, int direction){
		Cell[][] ph = this.board.getBoard(); //to reduce method chaining
		Player p = ph[x][y].getPlayer(); //careful here with inheritance
		if (p instanceof HumanPlayer){// || ComputerPlayer){ //when you implement computer player
			HumanPlayer player = (HumanPlayer) p;
		//stay put
		if(direction == 0){
			ph[x][y].getPlayer().setMoveFlag(true);
		}
		//move west
		if(direction == 1){
			if (playerBubble[1] == 1){
				ph[x][y].getPlayer().setMoveFlag(true); //you ain't goin nowhere
			}
			else{
				//checking wall
				if(x == 0){
					ph[x][y].setPlayer(null);
					ph[9][y].setPlayer(player); //check how to inheritance this shit
					ph[9][y].getPlayer().setMoveFlag(true);
					
				}
				else{
					ph[x][y].setPlayer(null);
					ph[x-1][y].setPlayer(player); //same thing
				}
			}
		}
		//move east
		if (direction == 2){
			if (playerBubble[2] == 2){
				ph[x][y].getPlayer().setMoveFlag(true); //you ain't goin nowhere
			}
			else{
				//checking wall
				if(x == 9){
					ph[x][y].setPlayer(null);
					ph[0][y].setPlayer(player); //check how to inheritance this shit
					ph[0][y].getPlayer().setMoveFlag(true);
				}
				else{
					ph[x][y].setPlayer(null);
					ph[x+1][y].setPlayer(player); //same thing
					ph[x+1][y].getPlayer().setMoveFlag(true);
				}
			}
		}
		//move north
		if(direction == 3){
			if (playerBubble[3] == 3){
				ph[x][y].getPlayer().setMoveFlag(true); //you ain't goin nowhere
			}
			else{
				//checking wall
				if(y == 0){
					ph[x][y].setPlayer(null);
					ph[x][9].setPlayer(player); //check how to inheritance this shit
					ph[x][9].getPlayer().setMoveFlag(true);
				}
				else{
					ph[x][y].setPlayer(null);
					ph[x][y+1].setPlayer(player); //same thing
					ph[x][y+1].getPlayer().setMoveFlag(true);
				}
			}
		}
		//move south
		if(direction == 4){
			if (playerBubble[4] == 4){
				ph[x][y].getPlayer().setMoveFlag(true); //you ain't goin nowhere
			}
			else{
				//checking wall
				if(y == 9){
					ph[x][y].setPlayer(null);
					ph[x][0].setPlayer(player); //check how to inheritance this shit
					ph[x][0].getPlayer().setMoveFlag(true);
				}
				else{
					ph[x][y].setPlayer(null);
					ph[x][y-1].setPlayer(player); //same thing
					ph[x][y-1].getPlayer().setMoveFlag(true);
				}
			}
		}
		}
	}
	
	public void moveChild(Board board, int x, int y, int direction){
		Cell[][] ph = this.board.getBoard(); //to reduce method chaining
		Player c = ph[x][y].getPlayer(); //careful here with inheritance
		if (c instanceof HumanPlayer){ // || or computer player
			HumanPlayer child = (HumanPlayer) c;
		//move west
		if(direction == 1){
			if (playerBubble[1] == 1){
				ph[x][y].getPlayer().setMoveFlag(true); //no babymaking if you can't choose a valid direction
			}
			else{
				//checking wall
				if(x == 0){
					ph[9][y].setPlayer(child); 
					ph[9][y].getPlayer().setMoveFlag(true);
					
				}
				else{
					ph[x-1][y].setPlayer(child); 
					ph[x-1][y].getPlayer().setMoveFlag(true);
				}
			}
		}
		//move east
		if (direction == 2){
			if (playerBubble[2] == 2){
				ph[x][y].getPlayer().setMoveFlag(true); //no babymaking if you can't choose a valid direction
			}
			else{
				//checking wall
				if(x == 9){
					ph[0][y].setPlayer(child); 
					ph[0][y].getPlayer().setMoveFlag(true);
				}
				else{
					ph[x+1][y].setPlayer(child);
					ph[x+1][y].getPlayer().setMoveFlag(true);
				}
			}
		}
		//move north
		if(direction == 3){
			if (playerBubble[3] == 3){
				ph[x][y].getPlayer().setMoveFlag(true); //no babymaking if you can't choose a valid direction
			}
			else{
				//checking wall
				if(y == 0){
					ph[x][9].setPlayer(child); 
					ph[x][9].getPlayer().setMoveFlag(true);
				}
				else{
					ph[x][y+1].setPlayer(child); 
					ph[x][y+1].getPlayer().setMoveFlag(true);
				}
			}
		}
		//move south
		if(direction == 4){
			if (playerBubble[4] == 4){
				ph[x][y].getPlayer().setMoveFlag(true); //no babymaking if you can't choose a valid direction
			}
			else{
				//checking wall
				if(y == 9){
					ph[x][0].setPlayer(child);
					ph[x][0].getPlayer().setMoveFlag(true);
				}
				else{
					ph[x][y-1].setPlayer(child);
					ph[x][y-1].getPlayer().setMoveFlag(true);
				}
			}
		}
	  }
		
	}
	
	
	/**
	 * @return the playerBubble
	 */
	public int[] getPlayerBubble() {
		return playerBubble;
	}

	/**
	 * @param playerBubble the playerBubble to set
	 */
	public void setPlayerBubble(int index, int status) {
		playerBubble[index] = status;
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
	
	public void printBoard(){
		int i = 0;
		int j = 0;
		Cell[][] ph = board.getBoard(); //@TODO ask about this
		for (i = 0; i < 10; j++){
			for (j = 0; j < 10; j++){
				System.out.printf("%d", ph[i][j].getStatus()); //".status"
			}
			System.out.println();
		}
	}
	
	/**
	 * This method adds energy when a player eats
	 * Really should be called 'feed' 
	 * 
	 */
	public void eat(HumanPlayer player){
		player.setEnergyLeft(MGC.getU());
	}
	
	/**
	 * This method will check the cells around the player and set the movement arrays as it goes
	 * @return an int array illustrating where other organisms are located
	 * @param the current cell
	 * 
	 */
	public void spaceCheck(Board board, int x, int y){ //-1 if no organism is present, 1 is they are present
		//is this wrapping around corners properly? probably not
		Cell[][] ph = this.board.getBoard(); //just to shorten the method chaining
		if(ph[x][y].isFoodP()){ //checking current space
			setFoodBubble(0, true);} //really there should be a third option
		setPlayerBubble(0, 1);
		//check west
		if(x == 0 && y == 0){ //check if it's on the upperwest side
			if (ph[9][9].getPlayer() != null){
				setFoodBubble(1, false);
				setPlayerBubble(1, 1);
			}
		}
		if(x == 0){ //
			if(ph[9][y].getPlayer() != null) 
				setPlayerBubble(1, 1);
			else{
				if(ph[9][y].isFoodP()){
					setFoodBubble(1, true);
				}
			}
		}
		if(ph[x-1][y].getPlayer() != null){
			setPlayerBubble(1, 1);
		}
		
		else{
			setPlayerBubble(1, -1);
		   if(ph[x-1][y].isFoodP()){
			  setFoodBubble(1, true);
		    }
		}
		//check east
		//check lower east side
		if(x == 9 && y == 9){
			if(ph[0][0].getPlayer() != null){
				setPlayerBubble(2, 1);
			}
			else{
				if(ph[0][0].isFoodP()){
					setFoodBubble(2, true);
				}
			}
		if(x == 9){ //checking east side wall
			if(ph[0][y].getPlayer() != null){
				setPlayerBubble(2, 1);
			}
			else{
				if(ph[0][y].isFoodP()){
					setFoodBubble(2, true);
				}
			}
		}
		
		if(ph[x+1][y].getPlayer() != null){
			setPlayerBubble(2, 1);
			
		}
		else{
			if (ph[x+1][y].isFoodP()){
				setFoodBubble(2, true);
			}
		}
		//check north
		if(y == 0){
			if(ph[x][9].getPlayer() != null){
				setPlayerBubble(3, 1);
			}
			else{
				if (ph[x][9].isFoodP()){
					setFoodBubble(3, true);
				}
			}
		}
		if(ph[x][y+1].getPlayer() != null){
			setPlayerBubble(3, 1);
			
		}
		else{
			if (ph[x][y+1].isFoodP()){
				setFoodBubble(3, true);
			}
		}
		//check south
		if(y == 9){
			if(ph[x][0].getPlayer() != null){
				setPlayerBubble(4, 1);
			}
			else{
				if(ph[x][0].isFoodP()){
					setFoodBubble(4, true);
				}
			}
		}
		if(ph[x][y-1].getPlayer() != null){
			setPlayerBubble(4, 1);
			
		}
		else{
			if (ph[x][y-1].isFoodP()){
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
		for(int j = 0; j < playerBubble.length; j++){
			playerBubble[j] = -1;
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
	
	public void updatePlayerRoundData(Board board, int x, int y){
		Cell[][] ph = this.board.getBoard();
		HumanPlayer hp = ph[x][y].getPlayer();
		//get the player round data corresponding to the player
		int id = hp.getKey();
		for(PlayerRoundData my : PRD){
			if(id == my.getPlayerId()){
				MyPlayerRoundData myPRD = (MyPlayerRoundData) my;  //inheritance!!!
				myPRD.setCount(1); //Inheritance!
				myPRD.setEnergy(hp.getEnergyLeft()); //Inheritance!
			}
			//else, some kind of exception
		}
			
	}
	
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
	
	
	
	
}

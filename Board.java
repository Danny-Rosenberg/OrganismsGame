
public class Board {

	private Cell[][] board;
	 
	//borrowed from Ryan's tic-tac-toe board creator
	/**
	 * The constructor, creating a playing board for organisms with Cell objects
	 * @param row
	 * @param column
	 * @param MGC a game configuration object
	 */
	public Board(int row, int column, MyGameConfig MGC, double p, double q){
		board = new Cell[row][column];
		
		for(int i = 0; i < row; i++){
			for(int j = 0; j < column; j++){
				board[i][j] = new Cell(MGC, p, q);
			}
		}
		
	}
	
	public Cell[][] getBoard(){
		return board;
	}
	
}

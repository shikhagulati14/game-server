package com.server.game.model;


import com.server.game.constant.GameConstants;
import com.server.game.exception.GameForTwoServiceException;

import java.util.ArrayList;

public class GameBoard {

    //game board define
    ArrayList<StringBuilder> board = new ArrayList<>();

    public void nextMove(Player player, int column) throws GameForTwoServiceException {
        //check for invalid move ie. column is full
    }

    public GameBoard() {
        initializeGameBoard();
    }

    private void initializeGameBoard() {

        for(int row = 0; row<6 ; row++){
            StringBuilder column = new StringBuilder("_________");
            board.add(column);
        }
    }

    public ArrayList<StringBuilder> getBoard() {
        return board;
    }

    public void setBoard(ArrayList<StringBuilder> board) {
        this.board = board;
    }

    //check winning condition
    public boolean checkWinningCondition(){
        //check winning conditions
        return false;
    }

    /**
     * @param column
     * @param charToFill
     * @return
     */
    public boolean updateGameBoard(int column, char charToFill){
        boolean updated = false;
        for(int rowNum = 0; rowNum < 6; rowNum++){
            if(board.get(rowNum).charAt(column)== GameConstants.DEFAULT_TOKEN){
            	if(rowNum ==5)
            	{
            		updateColumnInGameBoard(column, charToFill, rowNum); 
            		updated = true;
            	}
                continue;
            }else{
                updateColumnInGameBoard(column, charToFill, rowNum-1);
                updated = true;
                break;
            }
        }
        return updated;
    }

	private void updateColumnInGameBoard(int column, char charToFill, int rowNum) {
        StringBuilder row = board.get(rowNum);
        row.replace(column, column+1,charToFill+"");
		/*row = row.substring(0, column) + charToFill
		        + row.substring(column + 1);*/
		//board.add(rowNum, row);
	}

}

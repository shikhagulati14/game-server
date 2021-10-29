package com.server.game.engine;

import com.server.game.constant.GameConstants;
import com.server.game.exception.GameForTwoServiceException;
import com.server.game.model.*;
import com.server.game.state.GameState;
import com.server.game.utils.GameUtils;
import org.springframework.context.annotation.Scope;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@Scope("singleton")
public final class GameEngine {

    //keeps track of all the running games at any given time
    private final Map<String, GameState> runningGames = new ConcurrentHashMap<>();

    //Keeps Track of finished Games.
    private final Map<String, GameState> finishedGames = new ConcurrentHashMap<>();

    //This map will hold all the newly created games where players were in waiting state but don't know yet.
    private final Map<String, String> gamesCreatedForWaitingPlayers = new ConcurrentHashMap<>();

    //Players Waiting lounge
    private final Queue<Player> playersWaitingLounge = new ArrayBlockingQueue<>(2);

    //List of Players currently playing the Game, this will eliminate them from creating new game request.
    private final Set<Player> onlinePlayers = new HashSet<>();

    private final ReentrantLock lock = new ReentrantLock();

    public GameState getRunningGame(String gameId) {
        return runningGames.get(gameId);
    }

    public synchronized void addRunningGame(String gameId, GameState gameState) {
        runningGames.put(gameId, gameState);
    }

    /**
     * @param player
     * @return
     * @throws GameForTwoServiceException
     * @throws InterruptedException
     */
    public GameState getGameFromPlayersLounge(Player player) throws GameForTwoServiceException, InterruptedException {

        boolean isLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
        if (isLockAcquired) {
            try {

                if (gamesCreatedForWaitingPlayers.containsKey(player.getPlayerId()) || isPlayerEligibleForNewGame(player)) {
                    //check to see if a game is created for a player when he was in a waiting state
                    String existingGame = getGameCreatedForPlayer(player);
                    if (existingGame != null) {
                        //remove entry from the waiting list of created games

                        return newGameState(runningGames.get(gamesCreatedForWaitingPlayers.remove(player.getPlayerId())));
                    }
                    //check if any players waiting in waiting lounge if not add the current player
                    if (playersWaitingLounge.isEmpty()) {
                        playersWaitingLounge.add(player);
                    } else if (!(playersWaitingLounge.peek().equals(player))) {
                        //we got the opponent now we should create the game
                        Player opponent = playersWaitingLounge.poll();
                        String newGameId = GameUtils.generateNewGameId();
                        GameState gameState = new GameState(opponent, player, opponent, newGameId);
                        runningGames.put(newGameId, gameState);
                        onlinePlayers.add(player);
                        onlinePlayers.add(opponent);
                        gamesCreatedForWaitingPlayers.put(opponent.getPlayerId(), newGameId);
                        return newGameState(gameState);
                    }
                    return newGameState(new GameState(player, null, player, null));
                } else {
                    throw new GameForTwoServiceException("Player already Participating in a Game. Cannot start a new one.");
                }
            } finally {
                lock.unlock();
            }
        } else {
            throw new GameForTwoServiceException("Unable to create a new game. Please try again.");
        }
    }

    private GameState newGameState(GameState gameState) {
        GameState newGameStateToReturnToClient = new GameState(gameState.getPlayer1(), gameState.getPlayer2(), gameState.getPlayerToMove(), gameState.getGameId());

        if (null == gameState.getPlayer2()) {
            newGameStateToReturnToClient.setResult(Result.PLAYER_WAITING_FOR_NEW_GAME);
        }
        return newGameStateToReturnToClient;
    }

    /**
     * @param player
     */
    private boolean isPlayerEligibleForNewGame(Player player) {
        if (onlinePlayers.contains(player)) {
            return false;
        }
        return true;

    }

    //return the gameID for waiting player
    private String getGameCreatedForPlayer(Player player) {
        return gamesCreatedForWaitingPlayers.get(player.getPlayerId());
    }

    //return the copies of online player
    public List<Player> getAllOnlinePlayers() {
        List<Player> copy = new ArrayList<>();
        for (Player player : onlinePlayers) {
            copy.add(new Player(player.getPlayerId(), player.getPlayerName()));
        }
        return copy;
    }

    //return copies of all running games
    public List<RunningGame> getAllRunningGames() {
        List<RunningGame> runningGamesCopy = new ArrayList<>();
        for (Map.Entry entry : runningGames.entrySet()) {
            List<Player> participants = new ArrayList<>();
            GameState gameState = (GameState) entry.getValue();
            participants.add(new Player(gameState.getPlayer1().getPlayerId(), gameState.getPlayer1().getPlayerName()));
            participants.add(new Player(gameState.getPlayer2().getPlayerId(), gameState.getPlayer2().getPlayerName()));
            RunningGame game = new RunningGame((String) entry.getKey(), participants);
            runningGamesCopy.add(game);
        }
        return runningGamesCopy;
    }

    /**
     * Remove all entries for both the players and game from game engine:
     * 1. runningGames
     * 2.onlinePlayers
     */
    public GameState finishGameAndCleanup(GameState gameState, Player player) throws InterruptedException, GameForTwoServiceException {
        boolean isLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
        if (isLockAcquired) {
            try {

                //remove or add to finished games :
                    // for first caller add to finished games
                    // for second caller remove from finished games
                GameState finishedGame = finishedGames.get(gameState.getGameId());
                if(null != finishedGame){
                    finishedGames.remove(gameState.getGameId());
                    onlinePlayers.remove(player);
                }else{
                    finishedGames.put(gameState.getGameId(), gameState);
                    //remove from running games
                    GameState removedGameState = runningGames.remove(gameState.getGameId());

                    if (removedGameState == null) {
                        //Removes the Player when his retry count to find a new game on client side is more than defined
                        onlinePlayers.remove(player);
                        return gameState;
                    } else {
                        onlinePlayers.remove(removedGameState.getPlayer1());
                        onlinePlayers.remove(removedGameState.getPlayer2());
                    }
                    System.out.println("Game is finished successfully. Result: " + removedGameState.getResult() + ". Player 1: " + removedGameState.getPlayer1().getPlayerName() + ". Player 2: " + removedGameState.getPlayer2().getPlayerName() + ". GameID: " + gameState.getGameId());
                }

                return gameState;
            } finally {
                lock.unlock();
            }
        } else {
            throw new GameForTwoServiceException("Unable to Finish the game" + gameState.getGameId() + ". Please try again.");
        }
    }



    /**
     * @param nextMove
     * @return
     */
    public GameState playNextMove(NextMove nextMove) throws GameForTwoServiceException {
        /**
         * fetch gameState from running games
         * update gameboard with new columnvalue
         * Determine winning/Losing/tied condition
         * if yes--update gameState resultobject with proper value
         * return
         * if not -- update gameState playertomove to opponent
         * return gameState
         */
        GameState gameState = runningGames.get(nextMove.getGameId());


        if (null != gameState && !GameUtils.isGameFinished(gameState.getResult())) {
            System.out.println("Game found. " +gameState.getGameId());
            System.out.println("Player making move : "+ nextMove.getPlayerId() + " as " + nextMove.getPlayerToken());
            //get game Board
            GameBoard gameBoard = gameState.getGameBoard();
            //validate whether the move is valid
            validatePlayersMove(gameBoard, nextMove);
            //update the game board with the new move
            if(!gameBoard.updateGameBoard(nextMove.getColumnId(), nextMove.getPlayerToken())) {
            	throw new GameForTwoServiceException("Unable to update game board.");
            }
            //check if move resulted in an any of game ending condition i.e: Win/loss/Tie
            Result result = verifyWinningConditionMet(gameBoard, nextMove);
            if(GameUtils.isGameFinished(result)){
                //update the result in game state according
                gameState.setResult(result);
                System.out.println(result);
            }
            //Toggle Player to move
            gameState.setPlayerToMove(determinePlayerToMove(gameState));
            return gameState;

        }else if( null != finishedGames.get(nextMove.getGameId())) {
            return finishedGames.get(nextMove.getGameId());
        }
        else{
            throw new GameForTwoServiceException("No Game running with the provided gameID. Please verify.");
        }
    }

    private Player determinePlayerToMove(GameState gameState) {
        Player oldPlayer = gameState.getPlayerToMove();

        if(oldPlayer.getPlayerId().equalsIgnoreCase(gameState.getPlayer1().getPlayerId())){
            return gameState.getPlayer2();
        }else{
            return gameState.getPlayer1();
        }
    }

    private Result verifyWinningConditionMet(GameBoard gameBoard, NextMove nextMove) {

        if(verifyColumns(gameBoard, nextMove) || verifyRow(gameBoard,nextMove) || verifyDiagonals(gameBoard,nextMove)){
            if(nextMove.getPlayerToken()=='X'){
                return Result.PLAYER_1_WIN;
            }
            return Result.PLAYER_2_WIN;
        }
        return Result.PLAY_NEXT_MOVE;
    }

    private boolean verifyDiagonals(GameBoard gameBoard, NextMove nextMove) {
        return (verifyTopDownLeftToRight(gameBoard, nextMove) || verifyBottomUpLeftToRight(gameBoard,nextMove));
    }

    private boolean verifyTopDownLeftToRight(GameBoard gameBoard, NextMove nextMove) {
        ArrayList<StringBuilder> board = gameBoard.getBoard();
        boolean firstCharFound=false;
        int winningCounter=0;
        for(int i=0; i<2 && winningCounter <5; i++) {
            firstCharFound=false;
            winningCounter=0;

            int counterConditon = (i==0?4:8);
            for(int j=0;j<counterConditon && winningCounter <5 ;j++) {
                winningCounter=0;
                if(!firstCharFound && board.get(i).charAt(j) == nextMove.getPlayerToken()) {
                    firstCharFound=true;
                    winningCounter++;
                    if( j+counterConditon >9) {
                        break;
                    }
                    for(int k = j+1, row=i+1  ; k<9 && winningCounter<5;k++, row++) {
                        if(board.get(row).charAt(k) == nextMove.getPlayerToken()) {
                            winningCounter++;
                        }
                        else {
                            break;
                        }
                    }
                }
            }

        }
        return winningCounter==5;
    }

    private boolean verifyBottomUpLeftToRight(GameBoard gameBoard, NextMove nextMove) {
        ArrayList<StringBuilder> board = gameBoard.getBoard();
        boolean firstCharFound=false;
        int winningCounter=0;
        for(int i=5; i>3 && winningCounter <5; i--) {
            firstCharFound=false;
            winningCounter=0;

            int counterConditon = (i==5?5:1);
            for(int j=0;j<counterConditon && winningCounter <5 ;j++) {
                winningCounter=0;
                firstCharFound=false;
                if(board.get(i).charAt(j) == nextMove.getPlayerToken()) {
                    firstCharFound=true;
                    winningCounter++;
                    for(int k = j+1, row=i-1  ; k<9 && winningCounter<5;k++, row--) {
                        if(board.get(row).charAt(k) == nextMove.getPlayerToken()) {
                            winningCounter++;
                        }
                        else {
                            break;
                        }
                    }
                }
            }
        }
        return winningCounter==5;

    }

    private boolean verifyRow(GameBoard gameBoard, NextMove nextMove) {
        ArrayList<StringBuilder> board = gameBoard.getBoard();
        boolean firstCharFound=false;
        int winningCounter=0;
        for(int i = 0 ; (i<6 &&  winningCounter <5 );i++) {
            firstCharFound=false;
            winningCounter=0;
            StringBuilder row = board.get(i);

            for(int j = 0 ; (j < 9 &&  winningCounter <5); j++) {
                if(!firstCharFound && row.charAt(j) == nextMove.getPlayerToken()) {
                    firstCharFound=true;
                    winningCounter++;
                }
                if(j>5) {
                    winningCounter=0;
                    break;
                }
                for(int k=j+1; k<9 && winningCounter<5;k++) {
                    if(row.charAt(k) == nextMove.getPlayerToken()) {
                        winningCounter++;
                    }else {
                        winningCounter=0;
                        break;
                    }
                }
            }
        }
        return winningCounter==5;
    }

    /**
     * @param gameBoard
     * @param nextMove
     * @return
     */
    private boolean verifyColumns(GameBoard gameBoard, NextMove nextMove) {

        ArrayList<StringBuilder> board = gameBoard.getBoard();

        boolean firstCharFound=false;
        int winningCounter=0;
        for(int i=0 ; i<=5 ; i++) {
            if(!firstCharFound && board.get(i).charAt(nextMove.getColumnId())==nextMove.getPlayerToken()) {
                firstCharFound = true;
                winningCounter++;
                if( i > 2) {
                    break;
                }
                for(int j= i ; j<5 &&winningCounter<5 ;j++ ) {
                    if(board.get(j).charAt(3) == nextMove.getPlayerToken()) {
                        winningCounter++;
                    }else {
                        break;
                    }
                }
            }

        }
        return winningCounter == 5;
    }


    /**
     * @param gameBoard
     * @param nextMove
     */
    private void validatePlayersMove(GameBoard gameBoard, NextMove nextMove) throws GameForTwoServiceException {
        int columnId = nextMove.getColumnId();
        ArrayList<StringBuilder> rows = gameBoard.getBoard();
        if (rows.get(0).charAt(columnId) != GameConstants.DEFAULT_TOKEN) {
            throw new GameForTwoServiceException("Invalid Move. This column is already full. Please select a different Column.");
        }
    }
}

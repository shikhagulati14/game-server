package com.server.game.service.impl;

import com.server.game.engine.GameEngine;
import com.server.game.exception.GameForTwoServiceException;
import com.server.game.model.NextMove;
import com.server.game.model.Player;
import com.server.game.service.GameForTwoService;
import com.server.game.state.GameState;
import com.server.game.utils.GameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameForTwoServiceImpl implements GameForTwoService {

    @Autowired
    GameEngine gameEngine;

    @Override
    public GameState getGame(Player player) throws GameForTwoServiceException {

        //Get a game from the wait list where at-least one player is in waiting state
        try {
            GameUtils.validatePlayerParameters(player.getPlayerId(), player.getPlayerName());
            return gameEngine.getGameFromPlayersLounge(player);
        } catch (GameForTwoServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new GameForTwoServiceException(e);
        }
    }

    @Override
    public GameState makeNextMove(NextMove nextMove) throws GameForTwoServiceException {

        System.out.println("next move api called for from player : "+ nextMove.getPlayerToken());
        return gameEngine.playNextMove(nextMove);

    }

    @Override
    public GameState finishGame(GameState gameState, Player player) throws GameForTwoServiceException {
        /**
         * Remove all entries for both the players and game from game engine:
         * 1. runningGames
         * 2. onlinePlayer
         */
        try {
            return gameEngine.finishGameAndCleanup(gameState, player);
        } catch (InterruptedException e) {
            throw new GameForTwoServiceException("Something went wrong. Please try again.");
        }

    }

    @Override
    public GameState getPlayersTurn(String gameId) {
        GameState gameState = gameEngine.getRunningGame(gameId);
        return gameState;
    }
}

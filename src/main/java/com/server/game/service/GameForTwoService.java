package com.server.game.service;

import com.server.game.exception.GameForTwoServiceException;
import com.server.game.model.NextMove;
import com.server.game.model.Player;
import com.server.game.state.GameState;
import org.springframework.stereotype.Service;

@Service
public interface GameForTwoService {

    GameState getGame(Player player) throws GameForTwoServiceException;

    GameState makeNextMove(NextMove nextMove) throws GameForTwoServiceException;

    GameState finishGame(GameState gameState, Player player) throws GameForTwoServiceException;

    GameState getPlayersTurn(String gameId);
}

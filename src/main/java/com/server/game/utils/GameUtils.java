package com.server.game.utils;

import com.server.game.exception.GameForTwoServiceException;
import com.server.game.model.Result;

import java.util.UUID;

public class GameUtils {


    public static String generateNewGameId() {
        //generate new UUID
        UUID gameId = UUID.randomUUID();
        return gameId.toString();
    }

    public static void validatePlayerParameters(String playerId, String playerName) throws GameForTwoServiceException {
        if(null == playerId || playerId.isEmpty() || null == playerName || playerName.isEmpty()){
            throw new GameForTwoServiceException("Players details cannot be null or empty.");
        }
    }

    public static boolean isGameFinished(Result result) {
        return(result== Result.PLAYER_1_WIN
                || result == Result.PLAYER_2_WIN
                || result == Result.TIED);
    }
}

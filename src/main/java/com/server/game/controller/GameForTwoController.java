package com.server.game.controller;

import com.server.game.exception.GameForTwoServiceException;
import com.server.game.model.NextMove;

import com.server.game.model.Player;
import com.server.game.service.GameForTwoService;
import com.server.game.state.GameState;
import com.server.game.utils.GameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/gamefortwo")
public class GameForTwoController {

    @Autowired
    GameForTwoService gameForTwoService;
    @GetMapping("/findGame")
    public GameState getGame(@RequestParam("playerName") String playerName,
                             @RequestParam("playerId") String playerId) throws GameForTwoServiceException {

        //find a game with one player waiting or add player to waiting lounge
        Player player = new Player(playerId,playerName);
        return gameForTwoService.getGame(player);
    }

    @GetMapping("/playersTurn")
    public GameState getPlayersTurn(@RequestParam("gameId") String gameId) throws GameForTwoServiceException {
        //Get the player who is to play next
        System.out.println("Players turn called for Game: " + gameId);
        return gameForTwoService.getPlayersTurn(gameId);
    }

    @PostMapping("/makeMove")
    public GameState makeMove(@RequestBody NextMove nextMove) throws GameForTwoServiceException {
        System.out.println("Make move called for game " + nextMove.getGameId()+ " player : "+ nextMove.getPlayerToken());
        return gameForTwoService.makeNextMove(nextMove);

    }

    @PostMapping("/finishgame")
    public GameState finishGame(@RequestBody GameState gameState, @RequestBody Player player) throws GameForTwoServiceException {
        return gameForTwoService.finishGame(gameState, player);

    }
}

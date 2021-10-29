package com.server.game.controller;

import com.server.game.exception.BackendServiceException;
import com.server.game.model.Player;
import com.server.game.model.RunningGame;
import com.server.game.service.BackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/games/gamefortwo/backend")
public class BackendController {

    @Autowired
    private BackendService backendService;

    @GetMapping("/onlinePlayers")
    public ResponseEntity<?> onlinePlayers() throws BackendServiceException {
        List<Player> allOnlinePlayers = backendService.getAllOnlinePlayers();
        if(!allOnlinePlayers.isEmpty()) {
            return new ResponseEntity<>(allOnlinePlayers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("No Online Players at the Moment.", HttpStatus.OK);
        }

    }

    @GetMapping("/runningGames")
    public ResponseEntity<?> getRunningGames() throws BackendServiceException {
        List<RunningGame> allRunningGames = backendService.getAllRunningGames();
        if(!allRunningGames.isEmpty()) {
            return new ResponseEntity<>(allRunningGames, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("No Running Games at the Moment.", HttpStatus.OK);
        }
    }
}

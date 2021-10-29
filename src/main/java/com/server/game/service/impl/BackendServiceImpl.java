package com.server.game.service.impl;

import com.server.game.engine.GameEngine;
import com.server.game.exception.BackendServiceException;
import com.server.game.model.Player;
import com.server.game.model.RunningGame;
import com.server.game.service.BackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackendServiceImpl implements BackendService {

    @Autowired
    GameEngine gameEngine;

    /**
     * Returns List of all the Participants currently playing game
     * @return
     * @throws BackendServiceException
     */
    @Override
    public List<Player> getAllOnlinePlayers() throws BackendServiceException {
        //this does not return the games that those players are playing, for that call /allRunningGames api
       return gameEngine.getAllOnlinePlayers();
    }

    /**
     * @return returns list of all the running games along with it's players list
     * @throws BackendServiceException
     */
    @Override
    public List<RunningGame> getAllRunningGames() throws BackendServiceException {
        return gameEngine.getAllRunningGames();
    }

}

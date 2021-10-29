package com.server.game.service;

import com.server.game.exception.BackendServiceException;
import com.server.game.model.Player;
import com.server.game.model.RunningGame;

import java.util.List;

public interface BackendService {

    List<Player> getAllOnlinePlayers() throws BackendServiceException;

    List<RunningGame> getAllRunningGames() throws BackendServiceException;


}

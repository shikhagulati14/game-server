package com.server.game.model;

import java.util.List;

public class RunningGame {
    private String GameId;
    private List<Player> participants;

    public RunningGame(String gameId, List<Player> participants) {
        GameId = gameId;
        this.participants = participants;
    }

    public String getGameId() {
        return GameId;
    }

    public void setGameId(String gameId) {
        GameId = gameId;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Player> participants) {
        this.participants = participants;
    }
}

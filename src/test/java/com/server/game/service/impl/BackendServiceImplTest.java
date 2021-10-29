package com.server.game.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.server.game.engine.GameEngine;
import com.server.game.exception.BackendServiceException;
import com.server.game.exception.GameForTwoServiceException;
import com.server.game.model.Player;
import com.server.game.model.RunningGame;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BackendServiceImplTest {

	@Autowired
	BackendServiceImpl service;

	@Order(1)
	@Test
	public void testGetAllOnlinePlayers_Zero_WithNoGameInProgress()
			throws BackendServiceException, GameForTwoServiceException, InterruptedException {

		List<Player> players = service.getAllOnlinePlayers();
		assertNotEquals(players, null);
		assertEquals(players.size(), 0);
		System.out.println(players);

	}

	@Order(2)
	@Test
	public void testGetAllOnlinePlayers_Success_WithValidInput()
			throws BackendServiceException, GameForTwoServiceException, InterruptedException {

		GameEngine ge = service.getGameEngine();
		Player p1 = new Player("1", "Arjun");
		Player p2 = new Player("2", "Shankar");

		ge.getGameFromPlayersLounge(p1);
		ge.getGameFromPlayersLounge(p2);

		List<Player> players = service.getAllOnlinePlayers();
		assertNotEquals(players, null);
		assertEquals(players.size(), 2);
		assertEquals(players.get(0).getPlayerName(), "Arjun");
		assertEquals(players.get(1).getPlayerName(), "Shankar");
		System.out.println(players);

	}

	@Order(3)
	@Test
	public void testGetAllRunningGames_Success_WithValidInput()
			throws BackendServiceException, GameForTwoServiceException, InterruptedException {

		GameEngine ge = service.getGameEngine();
		Player p1 = new Player("3", "Sam");
		Player p2 = new Player("4", "Joe");

		ge.getGameFromPlayersLounge(p1);
		ge.getGameFromPlayersLounge(p2);

		List<RunningGame> runningGames = service.getAllRunningGames();

		System.out.println("Games List : " + runningGames);

		assertNotEquals(runningGames, null);
		assertTrue(runningGames.size() >= 1);
		assertNotEquals(runningGames.get(0).getGameId(), null);
		assertTrue(runningGames.get(0).getParticipants().size() > 1);
		assertTrue(runningGames.get(0).getParticipants().get(0).getPlayerName() != null);

	}

	@Order(4)
	@Test
	public void testGetAllRunningGames_ThrowsException_WhenGameEngineIsNull() throws BackendServiceException {

		service.setGameEngine(null);

		Exception exception = assertThrows(RuntimeException.class, () -> service.getAllRunningGames());
		assertEquals(new NullPointerException().getClass(), exception.getClass());

	}

	@Order(5)
	@Test
	public void testGetAllOnlinePlayers_ThrowsException_WhenGameEngineIsNull() throws BackendServiceException {

		service.setGameEngine(null);

		Exception exception = assertThrows(RuntimeException.class, () -> service.getAllOnlinePlayers());
		assertEquals(new NullPointerException().getClass(), exception.getClass());

	}

}

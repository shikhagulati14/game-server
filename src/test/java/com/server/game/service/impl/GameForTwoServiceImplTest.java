package com.server.game.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.server.game.exception.BackendServiceException;
import com.server.game.exception.GameForTwoServiceException;
import com.server.game.model.NextMove;
import com.server.game.model.Player;
import com.server.game.state.GameState;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameForTwoServiceImplTest {

	@Autowired
	GameForTwoServiceImpl service;

	public void cleanUp(GameState gameState) throws GameForTwoServiceException {
		gameState = null;
	}

	@Test
	@Order(1)
	public void testGetGame_Success_WithTwoPlayers()
			throws BackendServiceException, GameForTwoServiceException, InterruptedException {

		System.out.println("Executing first");

		Player p1 = new Player("21", "Sachin");
		Player p2 = new Player("22", "Wasim");

		GameState gameState = null;

		gameState = service.getGame(p1);
		// if game has not been created, create one
		if (gameState.getGameId() == null) {
			gameState = service.getGame(p2);
		}

		assertNotEquals(gameState, null);
		assertNotEquals(gameState.getGameId(), null);
		// player1 in the game
		assertNotEquals(gameState.getPlayer1(), null);
		// player2 in the game as well
		assertNotEquals(gameState.getPlayer2(), null);
		// Since game is on, player to move will also not be null
		assertNotEquals(gameState.getPlayerToMove(), null);
		System.out.println(gameState);

		cleanUp(gameState);
	}

	@Test
	@Order(2)
	public void testGetGame_NoGame_WithJustOnePlayer()
			throws BackendServiceException, GameForTwoServiceException, InterruptedException {

		Player p1 = new Player("12", "Allan");

		GameState gameState = service.getGame(p1);

		assertNotEquals(gameState, null);
		// with just one player, there is no game id created
		assertEquals(gameState.getGameId(), null);

		// With just one player, he will be waiting for another player to join for a
		// game
		assertEquals(gameState.getResult().description, "Player is waiting for new game.");
		System.out.println(gameState);
		cleanUp(gameState);

	}

	@Test
	@Order(3)
	public void testGetGame_ThrowsException_WhenPlayerIsInvalid() throws BackendServiceException {

		Player p1 = new Player(null, "Sam");

		Exception exception = assertThrows(GameForTwoServiceException.class, () -> service.getGame(p1));

		assertEquals(new GameForTwoServiceException().getClass(), exception.getClass());

	}

	@Test
	@Order(4)
	public void testFinishGame_Success_WhenFinishedProperly() throws GameForTwoServiceException {

		Player p1 = new Player("100", "Joe");
		Player p2 = new Player("200", "Hardik");

		GameState gameState = service.getGame(p1);
		// if game is not on, start a new one
		if (gameState.getGameId() == null) {
			gameState = service.getGame(p2);
		}

		String actualResult = service.finishGame(gameState);
		String expectedResult = "Game is finished successfully. Result: " + gameState.getResult() + ". Player 1: "
				+ gameState.getPlayer1().getPlayerName() + ". Player 2: " + gameState.getPlayer2().getPlayerName()
				+ ". GameID: " + gameState.getGameId();

		assertEquals(expectedResult, actualResult);

		cleanUp(gameState);

	}

	@Test
	@Order(5)
	public void testFinishGame_Fail_WhenFinishedWithIncorrectGameState() throws GameForTwoServiceException {

		Player p1 = new Player("10", "Virat");
		Player p2 = new Player("20", "Kane");

		GameState gameState = service.getGame(p1);
		gameState = service.getGame(p2);

		// calling finishGame method with null game state to cause failure message
		gameState.setGameId("14124");
		;
		String actualResult = service.finishGame(gameState);
		String expectedResult = "Game is finished successfully. Result: " + gameState.getResult() + ". Player 1: "
				+ gameState.getPlayer1().getPlayerName() + ". Player 2: " + gameState.getPlayer2().getPlayerName()
				+ ". GameID: " + gameState.getGameId();

		String failureResult = "Game is not in running state. Already closed Or incorrect gameId.";

		assertNotEquals(expectedResult, actualResult);
		assertEquals(failureResult, actualResult);
		System.out.println("Game state : " + gameState);

		cleanUp(gameState);

	}

	@Test
	@Order(6)
	public void testFinishGame_ThrowsException_WhenGameStateIsNull() throws BackendServiceException {

		Exception exception = assertThrows(GameForTwoServiceException.class, () -> service.getGame(null));

		assertEquals(new GameForTwoServiceException().getClass(), exception.getClass());

	}

	@Test
	@Order(7)
	public void testPlayNextMove_Throws_Exception_WhenNoGame() throws GameForTwoServiceException {
		String gameId = "14312";
		String playerId = "41";
		int columnId = 2;
		char playerToken = 'a';
		NextMove nextMove = new NextMove(gameId, playerId, columnId, playerToken);
		Exception exception = assertThrows(NullPointerException.class, () -> service.makeNextMove(nextMove));

		assertEquals(new NullPointerException().getClass(), exception.getClass());

	}

}

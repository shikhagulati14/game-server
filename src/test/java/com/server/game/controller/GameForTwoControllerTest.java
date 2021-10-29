package com.server.game.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.server.game.exception.BackendServiceException;
import com.server.game.exception.GameForTwoServiceException;
import com.server.game.model.Player;
import com.server.game.service.GameForTwoService;
import com.server.game.state.GameState;

@SpringBootTest
public class GameForTwoControllerTest {
	
	
	@Autowired
	GameForTwoController controller;
	
	@MockBean
	GameForTwoService service;
	
	@Test
	public void testGetGame_Success() throws BackendServiceException, GameForTwoServiceException {
		
		Player player1 = new Player("41", "Vishwanathan");
		Player player2 = new Player("42", "Garry");
		
		GameState expectedGameState = new GameState(player1, player2, player2, "123131123");
		
		Mockito.when(service.getGame(player2)).thenReturn(expectedGameState);
		GameState actualGameState = controller.getGame("Garry", "42");
		assertNotEquals(actualGameState, null);
		assertEquals(actualGameState.getGameId(), "123131123");
		assertNotEquals(actualGameState.getPlayer1(), null);
		assertNotEquals(actualGameState.getPlayer2(), null);
		
	}
	
	@Test
	public void testGetGame_ThrowsException_WhenExceptionOccursInService() throws BackendServiceException, GameForTwoServiceException {
		
		Player player = new Player("42", "Garry");
		
		Mockito.when(service.getGame(player)).thenThrow(GameForTwoServiceException.class);
		
		Exception ex = null;
		
		try {
			controller.getGame("Garry", "42");
		}catch(Exception e) {
			ex = e;
		}
		
		assertNotEquals(ex, null);
		
	}
	
	@Test
	public void testFinishGame_Success() throws BackendServiceException, GameForTwoServiceException {
		
		Player player1 = new Player("51", "Anand");
		Player player2 = new Player("52", "Casprov");
		
		GameState gameState = new GameState(player1, player2, player2, "100900");
		
		String expectedFinishedMessage = "Finished Successfully";
		
		Mockito.when(service.finishGame(gameState)).thenReturn("Finished Successfully");
		String finishMessage = controller.finishGame(gameState);
		assertNotEquals(finishMessage, null);
		assertEquals(finishMessage, expectedFinishedMessage);
		
	}
	
	@Test
	public void testFinishGame_ThrowsException_WhenExceptionOccursInService() throws BackendServiceException, GameForTwoServiceException {
		
		Player player1 = new Player("61", "Kapil");
		Player player2 = new Player("62", "David");
		
		GameState gameState = new GameState(player1, player2, player2, "100600");
		
		Mockito.when(service.finishGame(gameState)).thenThrow(GameForTwoServiceException.class);
		
		Exception ex = null;
		
		try {
			controller.finishGame(gameState);
		}catch(Exception e) {
			ex = e;
		}
		
		assertNotEquals(ex, null);
		
	}

}

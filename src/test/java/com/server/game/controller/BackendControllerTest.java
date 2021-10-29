package com.server.game.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import com.server.game.exception.BackendServiceException;
import com.server.game.model.Player;
import com.server.game.model.RunningGame;
import com.server.game.service.BackendService;

@SpringBootTest
public class BackendControllerTest {
	
	
	@Autowired
	BackendController controller;
	
	@MockBean
	BackendService service;
	
	@Test
	public void testOnlinePlayer() throws BackendServiceException {
		
		List<Player> expectedPlayersList = new ArrayList<>();
		expectedPlayersList.add(new Player("31","Ross"));
		Mockito.when(service.getAllOnlinePlayers()).thenReturn(expectedPlayersList);
		ResponseEntity<?> respEntity = controller.onlinePlayers();
		assertNotEquals(respEntity, null);
		assertNotEquals(respEntity.getStatusCode(), 200);
		
	}
	
	@Test
	public void testGetRunningGames() throws BackendServiceException {
		
		List<RunningGame> expectedRunningGamesList = new ArrayList<>();
		expectedRunningGamesList.add(new RunningGame("1", null));
		Mockito.when(service.getAllRunningGames()).thenReturn(expectedRunningGamesList);
		ResponseEntity<?> respEntity = controller.getRunningGames();
		assertNotEquals(respEntity, null);
		assertNotEquals(respEntity.getStatusCode(), 200);
		
	}

}

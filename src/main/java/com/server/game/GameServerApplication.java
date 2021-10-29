package com.server.game;

import com.server.game.engine.GameEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan
@Configuration
public class GameServerApplication {

	@Bean(name="gameEngine")
	public GameEngine gameEngine()
	{
		return new GameEngine();
	}

	public static void main(String[] args) {
		SpringApplication.run(GameServerApplication.class, args);
	}

}

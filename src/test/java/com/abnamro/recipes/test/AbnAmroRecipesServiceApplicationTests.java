package com.abnamro.recipes.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.abnamro.recipes.controller.RecipesController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AbnAmroRecipesServiceApplicationTests {

	@Autowired
	private RecipesController recipesController;
	
	@Test
	void contextLoads() {
		assertThat(recipesController).as("Application Context failed to load").isNotNull();
	}

}

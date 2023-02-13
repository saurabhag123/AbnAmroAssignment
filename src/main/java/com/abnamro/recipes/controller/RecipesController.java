package com.abnamro.recipes.controller;

import java.util.List;

import com.abnamro.recipes.exceptions.NoSuchRecipeFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abnamro.recipes.exceptions.ErrorMessages;
import com.abnamro.recipes.pojos.Recipe;
import com.abnamro.recipes.service.RecipesService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class RecipesController {

    @Autowired
    private RecipesService service;

    @PostMapping("/recipe")
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        log.debug("Calling service.saveRecipeToRepository to save recipe into DB");
        Recipe savedRecipe = service.saveRecipeToRepository(recipe);
        log.info("Service successfully saved new recipe into DB with recipeId: " + savedRecipe.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
    }

    @GetMapping("/recipe/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable Integer id) {
        log.info("Calling service.getRecipe to retrieve recipes from DB on basis of ID");
        Recipe recipe = service.getRecipeFromRepository(id);
        if (recipe != null) {
            log.info("Requested recipe with id: " + id + " retrieved from DB");
            return ResponseEntity.status(HttpStatus.OK).body(recipe);
        } else {
            log.error("Requested recipe with id: " + id + " not found in DB");
            throw new NoSuchRecipeFoundException(ErrorMessages.RECIPE_NOT_FOUND_MSG);
        }
    }

    @GetMapping("/recipes")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        log.debug("Calling service.getAllRecipes to retrieve all recipes from DB");
        List<Recipe> recipeList = service.getAllRecipesFromRepository();
        log.info("Number of recipes retrieved from DB: " + recipeList.size());
        return ResponseEntity.status(HttpStatus.OK).body(recipeList);
    }

    @PutMapping("/recipe")
    public ResponseEntity<Recipe> modifyRecipe(@RequestBody Recipe recipe) {
        log.debug("Calling service.modifyRecipeInRepository to update recipe");
        Recipe modifiedRecipe = service.modifyExistingRecipeInRepository(recipe);
        log.info("Service successfully modifed existing recipe in DB");
        return ResponseEntity.status(HttpStatus.OK).body(modifiedRecipe);

    }

    @DeleteMapping("/recipe/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Integer id) {
        log.debug("Calling service.deleteRecipeFromRepository to remove recipe");
        service.deleteRecipeFromRepository(id);
        return ResponseEntity.status(HttpStatus.OK).body("Requested recipe deleted from DB");
    }
}
}

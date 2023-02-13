package com.abnamro.recipes.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.abnamro.recipes.dao.RecipeEntity;
import com.abnamro.recipes.exceptions.ErrorMessages;
import com.abnamro.recipes.exceptions.ErrorResponse;
import com.abnamro.recipes.pojos.Recipe;
import com.abnamro.recipes.test.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RecipesControllerTests {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	//Common method to build apiPath and return
	private String buildApiPath(String endPoint) {
		String baseURL = "http://localhost:"+port;
		String apiPath = baseURL+endPoint;
		return apiPath;
	}


	@Test
	void WhenTriedToRequestResourceWithInvalidRecipeId_ThenResponseIsNotFound_Test() {

		//Prepare URL Path and parameters value
		String apiPath = buildApiPath("/api/recipe/10");
	    
	    //Make client request to /api/recipes/{id} path with correct auth header
		ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(apiPath, HttpMethod.GET, ErrorResponse.class);

		//ValidateHttp Status in getResponse is NOTFOUND
		assertThat(getResponse.getStatusCode()).as("Http Status is not as expected").isEqualTo(HttpStatus.NOT_FOUND);
		
		//Validate error message present in response entity body
		assertThat(getResponse.getBody().getMessage()).as("Error message returned in response entity is not as expected")
		.contains(ErrorMessages.RECIPE_NOT_FOUND_MSG);
	}

	@Test
	void GivenRecipeWithNulls_WhenPosted_ThenResponseIsBadRequest_Test() {
		
		//Prepare POST request details with recipe instance as null
		String apiPath = buildApiPath("/api/recipe/");
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<RecipeEntity> request = new HttpEntity<>(new RecipeEntity(), headers);
	    
	    //Make POST Request with recipe instance as null
	    ResponseEntity<ErrorResponse> errorResponse = restTemplate.postForEntity(apiPath, request, ErrorResponse.class);
	    
	    //Validate Http Status in response entity is BAD_REQUEST
	    assertThat(errorResponse.getStatusCode()).as("Http Status is not as expected").isEqualTo(HttpStatus.BAD_REQUEST);

	}
	
	@Test
	void GivenValidRecipe_WhenPosted_ThenResponseIsCreated_Test() {
		
		//Prepare POST request details with recipe instance as null
		String apiPath = buildApiPath("/api/recipe");
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    Recipe newRecipe = TestUtil.buildSampleRecipe(101, "Butter-Sponge-Cake", "eg", 5);
	    HttpEntity<Recipe> request = new HttpEntity<>(newRecipe, headers);
	    
	    //Make POST Request with valid recipe instance
	    ResponseEntity<Recipe> postResponse = restTemplate.postForEntity(apiPath, request, Recipe.class);
	    
	    //Validate Http Status in response entity is CREATED
	    assertThat(postResponse.getStatusCode()).as("Http Status is not as expected").isEqualTo(HttpStatus.CREATED);
		
	    //Validate response entity body has recipe fields same as earlier recipe fields
	    Recipe savedRecipe = postResponse.getBody(); 
	    assertThat(savedRecipe.getId()).as("Saved Recipe Id not matched with given recipe Id")
		  						.isEqualTo(newRecipe.getId());
	    assertThat(savedRecipe.getName()).as("Saved Recipe Name not matched with given recipe Name")
		  						.isEqualTo(savedRecipe.getName());
	    assertThat(savedRecipe.getCreationDateTime()).as("Saved Recipe DateTime not matched with given recipe DateTime")
		  						.isEqualTo(savedRecipe.getCreationDateTime());
	    assertThat(savedRecipe.getIngredientsList()).as("Number of ingredeints in saved recipe not as expected")
	    						.hasSize(newRecipe.getIngredientsList().size());

	  	//Make Get request with valid recipe Id and retrieve recipe
	  	Recipe retRecipe = GivenValidRecipeID_WhenRequested_ThenResponseIsOK(savedRecipe.getId());
	  	
	  	//Validate the retrieved recipe instance fields are same as saved recipe fields
	  	assertThat(retRecipe.getId()).as("Retrieved Recipe Id not matched with saved recipe Id")
		     				  .isEqualTo(savedRecipe.getId());
	  	assertThat(retRecipe.getName()).as("Retrieved Recipe Name not matched with saved recipe Name")
		  					  .isEqualTo(savedRecipe.getName());
	  	assertThat(retRecipe.getCreationDateTime()).as("Retrieved Recipe DateTime not matched with saved recipe DateTime")
		  					  .isEqualTo(savedRecipe.getCreationDateTime());
	  	assertThat(retRecipe.getIngredientsList()).as("Number of ingredeints in returned recipe not as expected")
		.hasSize(newRecipe.getIngredientsList().size());
	}
	
	Recipe GivenValidRecipeID_WhenRequested_ThenResponseIsOK(Integer recipeId) {
		//Prepare URL parameters for GET Request with valid recipe ID
		String apiPath = buildApiPath("/api/recipe/"+recipeId);
		HttpEntity<Recipe> request = new HttpEntity<>(new Recipe(),new HttpHeaders());
	    
	    //Make client request to /api/recipe/{id} path with correct auth header
		ResponseEntity<Recipe> getResponse = restTemplate.exchange(apiPath, HttpMethod.GET, request, Recipe.class);

		//Validate Http Status in Get Response is OK
		assertThat(getResponse.getStatusCode()).as("Http Status is not as expected").isEqualTo(HttpStatus.OK);
		
		//Extract Recipe Instance and return
		return getResponse.getBody();
	}
	
	@Test
	void GivenNoRecipesInDB_WhenTriedToRequestAllRecipes_ThenResponseIsNotFound_Test() {
		
		//Prepare URL Path and parameters value
		String apiPath = buildApiPath("/api/recipes");

		HttpEntity request = new HttpEntity(new HttpHeaders());
	    
	    //Make client request to /api/recipes path with correct auth header
		ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(apiPath, HttpMethod.GET, request, ErrorResponse.class);

		//ValidateHttp Status in getResponse is NOTFOUND
		assertThat(getResponse.getStatusCode()).as("Http Status is not as expected").isEqualTo(HttpStatus.NOT_FOUND);

	}

	@Test
	void GivenRecipeNotPresentInDB_WhenTriedToModifyRecipe_ThenResponseIsNotFound_Test() {
		
		//Prepare URL Path and parameters value
		String apiPath = buildApiPath("/api/recipe");
		HttpHeaders headers = new HttpHeaders();
		Recipe unknownRecipe = TestUtil.buildSampleRecipe(999, "Unknown Recipe", "xx", 0);
		HttpEntity<Recipe> request = new HttpEntity<>(unknownRecipe,headers);
	    
	    //Make put request to /api/recipe path with correct auth header
		ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(apiPath, HttpMethod.PUT, request, ErrorResponse.class);

		//ValidateHttp Status in getResponse is NOTFOUND
		assertThat(getResponse.getStatusCode()).as("Http Status is not as expected").isEqualTo(HttpStatus.NOT_FOUND);
		
		//Validate error message present in response entity body
		assertThat(getResponse.getBody().getMessage()).as("Error message returned in response entity is not as expected")
		.contains(ErrorMessages.RECIPE_NOT_FOUND_MSG);
	}

	@Test
	void GivenRecipeNotPresentInDB_WhenTriedToDeleteRecipe_ThenResponseIsNotFound_Test() {
		
		//Prepare URL Path and parameters value
		String apiPath = buildApiPath("/api/recipe/10");
		HttpHeaders headers = new HttpHeaders();
		HttpEntity request = new HttpEntity(headers);
	    
	    //Make delete request to /api/recipe/id path with correct auth header
		ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(apiPath, HttpMethod.DELETE, request, ErrorResponse.class);

		//ValidateHttp Status in getResponse is NOTFOUND
		assertThat(getResponse.getStatusCode()).as("Http Status is not as expected").isEqualTo(HttpStatus.NOT_FOUND);
		
		//Validate error message present in response entity body
		assertThat(getResponse.getBody().getMessage()).as("Error message returned in response entity is not as expected")
		.contains(ErrorMessages.RECIPE_NOT_FOUND_MSG);
	}

}

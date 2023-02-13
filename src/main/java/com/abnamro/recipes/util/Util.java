package com.abnamro.recipes.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.abnamro.recipes.pojos.Ingredient;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

//Util Class to contain common utility methods
@Slf4j
public class Util {
	private static String pattern = "dd-MM-yyyy HH:mm:ss";

	//Method to Get and return current date along with time in required format
	public static Optional<Date> getCurrentDateTime() {
		try {
			Date currentDateTime = Calendar.getInstance().getTime();
			log.debug("Current Date Time Value: "+currentDateTime.toString());
			return Optional.of(currentDateTime);
		}catch(Exception e) {
			log.error("Exception caught while getting current datetime");
			ExceptionUtils.getStackTrace(e);
			return Optional.empty();
		}
	}
	//Convert given ingredients list to JsonString and return
	public static String convertToJSONString(List<Ingredient> ingList) {
		ObjectMapper mapper = new ObjectMapper(); 
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(ingList); 
		} catch(Exception e) {
			log.error("Exception caught while converting List to JSON String");
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return jsonString;
	}
	
	//Convert given JSON String to List of Ingredients
	public static List<Ingredient> convertJSONStringToIngredientsList(String jsonString){
		ObjectMapper mapper = new ObjectMapper();
		List<Ingredient> ingredientsList = null;
		//Convert JSON array to Array objects
		//Ingredient[] ingredients = mapper.readValue(jsonString, Ingredient[].class);
		try {
			//Convert JSON array to List of objects
			ingredientsList = Arrays.asList(mapper.readValue(jsonString, Ingredient[].class));
		} catch(Exception e) {
			log.error("Exception caught while converting JSON String to Ingredients List");
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return ingredientsList;
	}
	
	//Format given Date contents to 
	public static String formatDateTime(Date date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			String formattedDateTimeString = formatter.format(date);
			log.debug("Formatted DateTime: "+formattedDateTimeString);
			return formattedDateTimeString;
		}catch(Exception e) {
			log.error("Exception caught while formatting and parsing date time in "+pattern);
			ExceptionUtils.getStackTrace(e);
			return null;
		}
	}
}

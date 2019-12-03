package com.rationaleemotions.pojos;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ByIdOrName;
import org.testng.Assert;

import com.google.gson.JsonObject;

public class Test {

	public static void main(String[] args) {
		// java.lang.IllegalArgumentException: name attribute is missing.
		// JsonObject object = new JsonObject();
		// JsonWebElement.newElement(object);
		//---------------------------------
		
		
		//java.lang.IllegalArgumentException: locator attribute is missing.
		/*
		 * JsonObject object = new JsonObject();
		 * object.addProperty(JsonWebElement.MandatoryKeys.NAME, "foo");
		 * //object.addProperty(JsonWebElement.MandatoryKeys.LOCATOR, "//h1");
		 * JsonWebElement.newElement(object);
		 */
		
		/*
		 * JsonObject object = newJson();
		 * object.addProperty(JsonWebElement.MandatoryKeys.NAME, "foo");
		 * object.addProperty(JsonWebElement.MandatoryKeys.LOCATOR, "h2");
		 * JsonWebElement element = JsonWebElement.newElement(object); By actual =
		 * element.getLocationStrategy();
		 * 
		 * System.out.println(actual.toString()); Assert.assertEquals(actual.getClass(),
		 * ByIdOrName.class);
		 */
		
		/*
		 * JsonObject object = newJson(); JsonWebElement element =
		 * JsonWebElement.newElement(object); By actual = element.getLocationStrategy();
		 * System.out.println(actual.toString()); Assert.assertEquals(actual.getClass(),
		 * By.ByXPath.class);
		 */

	}
	
	private static JsonObject newJson(String name,String locator) {  
        JsonObject element = new JsonObject();
        element.addProperty(JsonWebElement.MandatoryKeys.NAME, name);
        element.addProperty(JsonWebElement.MandatoryKeys.LOCATOR, locator);
        return element;
    }
    private static JsonObject newJson() {
        return newJson("foo", "xpath=//h1");
    }

}

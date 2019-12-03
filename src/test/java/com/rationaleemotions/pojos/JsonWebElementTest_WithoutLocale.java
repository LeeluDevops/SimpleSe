package com.rationaleemotions.pojos;

import com.rationaleemotions.internal.locators.Until;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ByIdOrName;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class JsonWebElementTest_WithoutLocale {

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = JsonWebElement.MandatoryKeys.NAME
			+ JsonWebElement.ATTRIBUTE_IS_MISSING)
	public void testMissingName() {
		JsonObject object = new JsonObject();
		JsonWebElement.newElement(object);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = JsonWebElement.MandatoryKeys.LOCATOR
			+ JsonWebElement.ATTRIBUTE_IS_MISSING)
	public void testMissingLocator() {
		JsonObject object = new JsonObject();
		object.addProperty(JsonWebElement.MandatoryKeys.NAME, "foo");
		// object.addProperty(JsonWebElement.MandatoryKeys.LOCATOR, "//h1");
		JsonWebElement.newElement(object);
	}

	@Test
	public void testElementRetrievalForRequestedLocaleWhenMultipleLocalesPresent() {
		JsonObject object = newJson();
		object.addProperty(JsonWebElement.MandatoryKeys.NAME, "foo");
		object.addProperty(JsonWebElement.MandatoryKeys.LOCATOR, "h2");
		JsonWebElement element = JsonWebElement.newElement(object);
		By actual = element.getLocationStrategy();
		Assert.assertEquals(actual.getClass(), ByIdOrName.class);
	}

	@Test(dataProvider = "getTestData")
	public void testNewElementCreation(JsonObject object, Until expUntil, int expWait, Class<By> expClass) {
		JsonWebElement element = JsonWebElement.newElement(object);
		Assert.assertEquals(element.getName(), object.get(JsonWebElement.MandatoryKeys.NAME).getAsString());
		Assert.assertEquals(element.getUntil(), expUntil);
		Assert.assertEquals(element.getWaitForSeconds(), expWait);
		By actual = element.getLocationStrategy();
		Assert.assertEquals(actual.getClass(), expClass);
		String locator = object.get(JsonWebElement.MandatoryKeys.LOCATOR).getAsString();
		// For CSS
		if (expClass.equals(By.ByCssSelector.class)) {
			locator = locator.substring(4);
		} else if (expClass.equals(By.ByClassName.class)) {
			locator = locator.substring(6);
		} else if (expClass.equals(By.ByLinkText.class)) {
			locator = locator.substring(9);
		} else if (expClass.equals(By.ByPartialLinkText.class)) {
			locator = locator.substring(16);
		} else if (expClass.equals(By.ByTagName.class)) {
			locator = locator.substring(8);
		} else if (expClass.equals(By.ByXPath.class)) {
			locator = locator.substring(6);
		}

		Assert.assertTrue(actual.toString().contains(locator));
	}

	@DataProvider
	public Object[][] getTestData() {
		return new Object[][] {
				// basic object creation test data
				{ newJson(), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME, By.ByXPath.class },
				// checking if different xpath combinations yield proper results.
				{ newJson("foo", "xpath=//h1"), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME, By.ByXPath.class },
				{ newJson("foo", "xpath=/h1"), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME, By.ByXPath.class },
				{ newJson("foo", "xpath=./h1"), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME, By.ByXPath.class },
				// checking if css is parsed properly.
				{ newJson("foo", "css=foo"), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME,
						By.ByCssSelector.class },
				// checking if class is parsed properly.
				{ newJson("foo", "class=foo"), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME,
						By.ByClassName.class },
				// checking if tagName is parsed properly.
				{ newJson("foo", "tagName=foo"), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME,
						By.ByTagName.class },
				// checking if linkText is parsed properly.
				{ newJson("foo", "linkText=foo"), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME,
						By.ByLinkText.class },
				// checking if partialLinkText is parsed properly.
				{ newJson("foo", "partialLinkText=foo"), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME,
						By.ByPartialLinkText.class },
				// checking if byId/byName is parsed properly.
				{ newJson("foo", "foo"), Until.Available, JsonWebElement.DEFAULT_WAIT_TIME, ByIdOrName.class },
				// checking if Until defaults to Available when its empty (or) missing
				{ newJson("", 10), Until.Available, 10, By.ByXPath.class },
				{ newJsonWithout(JsonWebElement.WaitAttributes.UNTIL, "25"), Until.Available, 25, By.ByXPath.class },
				// checking if other custom values for until are parsed properly.
				{ newJson(Until.Clickable.name(), 10), Until.Clickable, 10, By.ByXPath.class },
				// checking if time defaults to default wait time if its less than zero (or)
				// when its missing
				{ newJson(Until.Clickable.name(), 0), Until.Clickable, JsonWebElement.DEFAULT_WAIT_TIME,
						By.ByXPath.class },
				{ newJsonWithout(JsonWebElement.WaitAttributes.FOR, Until.Visible.name()), Until.Visible,
						JsonWebElement.DEFAULT_WAIT_TIME, By.ByXPath.class } };
	}

	private static JsonObject newJsonWithout(String attrib, String val) {
		JsonObject json = newJson();
		JsonObject wait = new JsonObject();
		switch (attrib) {
		case JsonWebElement.WaitAttributes.UNTIL:
			wait.addProperty(JsonWebElement.WaitAttributes.FOR, Integer.parseInt(val));
			break;
		case JsonWebElement.WaitAttributes.FOR:
			wait.addProperty(JsonWebElement.WaitAttributes.UNTIL, Until.parse(val).name());
		}
		json.add(JsonWebElement.OptionalKeys.WAIT, wait);
		return json;
	}

	private static JsonObject newJson(String until, int time) {
		JsonObject json = newJson();
		json.add(JsonWebElement.OptionalKeys.WAIT, newWait(until, time));
		return json;
	}

	private static JsonObject newWait(String until, int time) {
		JsonObject object = new JsonObject();
		object.addProperty(JsonWebElement.WaitAttributes.UNTIL, until);
		object.addProperty(JsonWebElement.WaitAttributes.FOR, time);
		return object;
	}

	private static JsonObject newJson(String name, String locator) {
		JsonObject element = new JsonObject();
		element.addProperty(JsonWebElement.MandatoryKeys.NAME, name);
		element.addProperty(JsonWebElement.MandatoryKeys.LOCATOR, locator);
		return element;
	}

	private static JsonObject newJson() {
		return newJson("foo", "xpath=//h1");
	}
}
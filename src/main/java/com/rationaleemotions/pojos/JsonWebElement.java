package com.rationaleemotions.pojos;

import com.rationaleemotions.internal.locators.Strategy;
import com.rationaleemotions.internal.locators.StrategyTraits;
import com.rationaleemotions.internal.locators.Until;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rationaleemotions.utils.StringUtils;
import org.openqa.selenium.By;

import java.util.List;
import java.util.Map;
import static com.rationaleemotions.pojos.JsonWebElement.MandatoryKeys.LOCATOR;
import static com.rationaleemotions.pojos.JsonWebElement.MandatoryKeys.LOCALE;
import static com.rationaleemotions.pojos.JsonWebElement.MandatoryKeys.NAME;
import static com.rationaleemotions.pojos.JsonWebElement.OptionalKeys.WAIT;
import static com.rationaleemotions.pojos.JsonWebElement.WaitAttributes.FOR;
import static com.rationaleemotions.pojos.JsonWebElement.WaitAttributes.UNTIL;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class JsonWebElement {
	private static final String defaultValue = System.getProperty("default.wait.time", "45");
	static final int DEFAULT_WAIT_TIME = Integer.parseInt(defaultValue);
	static final String ATTRIBUTE_IS_MISSING = " attribute is missing.";
	private String name;
	/* locator in case locale is absent */
	private String locator;
	private Map<String, By> locationStrategyMap = Maps.newHashMap();
	/* location strategy in case locale is absent */
	private By locationStrategy;
	private Until until;
	private int forSeconds;
	private String defaultLocale;

	String getName() {
		return name;
	}

	/**
	 * denotes locator field when Locale is Absent
	 * 
	 * @return
	 */
	public String getLocator() {
		return locator;
	}

	public Until getUntil() {
		return until;
	}

	public int getWaitForSeconds() {
		return forSeconds;
	}

	/**
	 * This method is to get the locationStrategy when Locale is absent
	 * 
	 * @return
	 */
	public By getLocationStrategy() {
		return locationStrategy;
	}

	public By getLocationStrategy(String whichLocale) {
		checkArgument(StringUtils.isNotBlank(whichLocale), "Querying locale cannot be empty (or) null.");
		checkState(locationStrategyMap.containsKey(defaultLocale),
				"Un-recognized default locale [" + defaultLocale + "]" + " provided.");
		if (locationStrategyMap.containsKey(whichLocale)) {
			return locationStrategyMap.get(whichLocale);
		}
		return locationStrategyMap.get(defaultLocale);
	}

	static JsonWebElement newElement(JsonObject json, String defaultLocale) {
		ensureMandatoryKeysArePresent(json);
		JsonWebElement element = new JsonWebElement();
		element.name = json.get(NAME).getAsString();
		List<LocaleDefinition> localeDefinitions = LocaleDefinition.newDefinition(json.get(LOCALE).getAsJsonArray());
		for (LocaleDefinition localeDefinition : localeDefinitions) {
			element.locationStrategyMap.put(localeDefinition.getLocale(), localeDefinition.getLocationStrategy());
		}
		JsonElement waitNode = json.get(WAIT);
		if (waitNode == null) {
			element.until = Until.Available;
			element.forSeconds = DEFAULT_WAIT_TIME;
		} else {
			JsonObject waitObject = waitNode.getAsJsonObject();
			Until until = Until.Available;
			if (waitObject.has(UNTIL)) {
				until = Until.parse(waitNode.getAsJsonObject().get(UNTIL).getAsString());
			}
			element.until = until;
			int wait = DEFAULT_WAIT_TIME;
			if (waitObject.has(FOR) && waitObject.get(FOR).getAsInt() > 0) {
				wait = waitObject.get(FOR).getAsInt();
			}
			element.forSeconds = wait;
		}
		element.defaultLocale = defaultLocale;
		return element;
	}

	/**
	 * This method is to create a Json element while locale is absent
	 * 
	 * @param json
	 * @return
	 */
	static JsonWebElement newElement(JsonObject json) {
		ensureMandatoryKeysArePresent(json, NAME);
		ensureMandatoryKeysArePresent(json, LOCATOR);
		JsonWebElement element = new JsonWebElement();
		element.name = json.get(NAME).getAsString();
		element.locator = json.get(LOCATOR).getAsString();
		StrategyTraits traits = Strategy.identifyStrategy(element.locator);
		element.locationStrategy = traits.getStrategy(element.locator);
		JsonElement waitNode = json.get(WAIT);
		if (waitNode == null) {
			element.until = Until.Available;
			element.forSeconds = DEFAULT_WAIT_TIME;
		} else {
			JsonObject waitObject = waitNode.getAsJsonObject();
			Until until = Until.Available;
			if (waitObject.has(UNTIL)) {
				until = Until.parse(waitNode.getAsJsonObject().get(UNTIL).getAsString());
			}
			element.until = until;
			int wait = DEFAULT_WAIT_TIME;
			if (waitObject.has(FOR) && waitObject.get(FOR).getAsInt() > 0) {
				wait = waitObject.get(FOR).getAsInt();
			}
			element.forSeconds = wait;
		}
		return element;
	}

	private static void ensureMandatoryKeysArePresent(JsonObject json) {
		checkArgument(json.has(NAME), NAME + ATTRIBUTE_IS_MISSING);
		checkArgument(json.has(LOCALE), LOCALE + ATTRIBUTE_IS_MISSING);
		// checkArgument(json.has(LOCATOR), LOCATOR + ATTRIBUTE_IS_MISSING);
	}

	/**
	 * To ensure the given key is present in the given json
	 * 
	 * @param json
	 * @param argument
	 */
	private static void ensureMandatoryKeysArePresent(JsonObject json, String key) {
		checkArgument(json.has(key), key + ATTRIBUTE_IS_MISSING);
	}

	interface MandatoryKeys {
		String NAME = "name";
		String LOCALE = "locale";
		String LOCATOR = "locator";
	}

	interface OptionalKeys {
		String WAIT = "wait";
	}

	interface WaitAttributes {
		String UNTIL = "until";
		String FOR = "for";
	}
}

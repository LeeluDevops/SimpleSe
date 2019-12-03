package com.rationaleemotions.pojos;

import static com.google.common.base.Preconditions.checkArgument;
import static com.rationaleemotions.pojos.JsonWebElement.MandatoryKeys.LOCATOR;
import static com.rationaleemotions.pojos.JsonWebElement.MandatoryKeys.NAME;
import static com.rationaleemotions.pojos.JsonWebElement.OptionalKeys.WAIT;
import static com.rationaleemotions.pojos.JsonWebElement.WaitAttributes.FOR;
import static com.rationaleemotions.pojos.JsonWebElement.WaitAttributes.UNTIL;

import org.openqa.selenium.By;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rationaleemotions.internal.locators.Strategy;
import com.rationaleemotions.internal.locators.StrategyTraits;
import com.rationaleemotions.internal.locators.Until;

public class JsonWebElement {
    private static final String defaultValue = System.getProperty("default.wait.time", "45");
    static final int DEFAULT_WAIT_TIME = Integer.parseInt(defaultValue);
    static final String ATTRIBUTE_IS_MISSING = " attribute is missing.";
    private String name;
    private String locator;
   // private Map<String, By> locationStrategy = Maps.newHashMap();
    private By locationStrategy;
    private Until until;
    private int forSeconds;
   
    String getName() {
        return name;
    }
    
    String getLocator() {
		return locator;
	}

	public Until getUntil() {
        return until;
    }

    public int getWaitForSeconds() {
        return forSeconds;
    }

    public By getLocationStrategy() {
        return locationStrategy;
    }

    static JsonWebElement newElement(JsonObject json) {
        ensureMandatoryKeysArePresent(json);
        JsonWebElement element = new JsonWebElement();
        element.name = json.get(NAME).getAsString();
        element.locator= json.get(LOCATOR).getAsString();
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
        checkArgument(json.has(LOCATOR), LOCATOR + ATTRIBUTE_IS_MISSING); 
    }

    interface MandatoryKeys {
        String NAME = "name";        
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

package com.rationaleemotions.page;

import com.rationaleemotions.pojos.JsonWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

final class RawElement {
	private JsonWebElement jsonWebElement;
	private String locale;
	private SearchContext context;

	RawElement(SearchContext context, JsonWebElement jsonWebElement, String locale) {
		this.context = context;
		this.jsonWebElement = jsonWebElement;
		this.locale = locale;
	}

	/**
	 * Constructor to initialize context & JsonWebElement
	 * 
	 * @param context
	 * @param jsonWebElement
	 */
	RawElement(SearchContext context, JsonWebElement jsonWebElement) {
		this.context = context;
		this.jsonWebElement = jsonWebElement;
	}

	final WebElement getWebElement() {
		By by = jsonWebElement.getLocationStrategy(locale);
		return context.findElement(by);
	}

	final List<WebElement> getWebElements() {
		By by = jsonWebElement.getLocationStrategy(locale);
		return context.findElements(by);
	}

	/**
	 * This method finds the element in context using the location strategy
	 * determined by isLocaleEnabled
	 * 
	 * @param isLocaleEnabled
	 * @return
	 */
	final WebElement getWebElement(boolean isLocaleEnabled) {
		By by;
		if (isLocaleEnabled) {
			by = jsonWebElement.getLocationStrategy(locale);
		} else {
			by = jsonWebElement.getLocationStrategy();
		}
		return context.findElement(by);
	}

	/**
	 * This method finds the elements in context using the location strategy
	 * determined by isLocaleEnabled
	 * 
	 * @param isLocaleEnabled
	 * @return
	 */
	final List<WebElement> getWebElements(boolean isLocaleEnabled) {
		By by;
		if (isLocaleEnabled) {
			by = jsonWebElement.getLocationStrategy(locale);
		} else {
			by = jsonWebElement.getLocationStrategy();
		}
		return context.findElements(by);
	}
}

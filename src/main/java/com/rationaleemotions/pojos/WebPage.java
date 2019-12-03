package com.rationaleemotions.pojos;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rationaleemotions.internal.PageStore;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class WebPage {
	private String name;
	private String defaultLocale;
	private Map<String, JsonWebElement> elements = Maps.newConcurrentMap();
	/* Locale is Absent */
	private boolean isLocalePresent;
	private static final String LOCALE = "locale";

	public static WebPage getPage(String fileName) {
		File file = new File(fileName);
		Preconditions.checkArgument(file.exists(), "Cannot find file : " + file.getAbsolutePath());
		WebPage page = PageStore.getPage(FilenameUtils.getBaseName(fileName));
		if (page != null) {
			return page;
		}
		try {
			JsonParser parser = new JsonParser();
			JsonObject contents = parser.parse(new FileReader(fileName)).getAsJsonObject();
			page = new WebPage();
			page.name = contents.get("name").getAsString();

			JsonArray elements = contents.get("elements").getAsJsonArray();
			if (containsLocale(elements)) {
				page.defaultLocale = contents.get("defaultLocale").getAsString();
				for (int i = 0; i < elements.size(); i++) {
					JsonObject object = elements.get(i).getAsJsonObject();
					JsonWebElement element = JsonWebElement.newElement(object, page.defaultLocale);
					page.elements.put(element.getName(), element);
				}
			} else {
				for (int i = 0; i < elements.size(); i++) {
					JsonObject object = elements.get(i).getAsJsonObject();
					JsonWebElement element = JsonWebElement.newElement(object);
					page.elements.put(element.getName(), element);
				}
			}

			PageStore.addPage(page);
		} catch (IOException e) {
			throw new WebPageParsingException(e);
		}
		return page;
	}

	public String getName() {
		return name;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public JsonWebElement getWebElement(String name) {
		return elements.get(name);
	}

	public static class WebPageParsingException extends RuntimeException {
		public WebPageParsingException(Throwable t) {
			super(t);
		}
	}

	/**
	 * Return true if locale is present in the json. This is set when initializing
	 * page from Json
	 * 
	 * @return
	 */
	public boolean isLocalePresent() {
		return isLocalePresent;
	}

	/**
	 * This method checks if locale key is present in any of the elements in the
	 * given json array
	 * 
	 * @param elements
	 * @return
	 */
	public static boolean containsLocale(JsonArray elements) {
		boolean present = false;
		for (JsonElement element : elements) {
			if (element.getAsJsonObject().has(LOCALE)) {
				present = true;
				break;
			}
		}
		return present;
	}
}

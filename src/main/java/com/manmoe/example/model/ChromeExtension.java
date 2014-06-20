package com.manmoe.example.model;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;

/**
 * This is a file for accessing the extension for testing. Here are the details, how we access everything we need for
 * testing. The good news is, that you don't have to worry about it in the tests.
 *
 * @author Manuel Möhlmann <mail@manmoe.com>
 */
public class ChromeExtension {
	public static final String EXTENSION_URL_PROTOCOL = "chrome-extension://";
	public static final String EXTENSION_SITE_BACKGROUND_URL = "/background.html";
	public static final String EXTENSION_INSPECT_PAGE = "chrome://inspect/#extensions";

	/**
	 * The name of the extension. Have a look in your extension manifest.json, if you are not sure.
	 */
	private String name;

	/**
	 * The chrome webdriver we want to use.
	 */
	private RemoteWebDriver driver;

	/**
	 * This is the id our extension has on the remote browser.
	 */
	private String extensionId;

	/**
	 * We need the webdriver and the name of the extension you want to test. Have a look in your manifest, if you are
	 * not sure.
	 *
	 * @param driver
	 * @param extensionName
	 */
	public ChromeExtension(RemoteWebDriver driver, String extensionName) {
		this.driver = driver;
		this.name = extensionName;
	}

	/**
	 * Provides the id of the extension installed on the remote browser. Will be uses internally, but can be helpful
	 * for writing test.
	 *
	 * @return The id of the extension provided by the remote Chrome browser
	 * TODO: I have to do it with xpath. That's really crappy at the moment ;)
	 */
	public String getId() {
		if (extensionId != null) {
			// if we have already the id, there is no need to do it twice.
			return extensionId;
		}

		// on this site every extension will be shown
		driver.get(EXTENSION_INSPECT_PAGE);

		// get the extensions list
		List<WebElement> span = driver.findElements(By.id("extensions"));

		for (WebElement elem : span) {
			String text = elem.getText();
			// the test element is much too large. We will split it, to receive the background.html url link
			String[] splits = text.split(" ");
			for (String part : splits) {
				// we check, if we got the background.html link
				if (part.startsWith(EXTENSION_URL_PROTOCOL) && part.endsWith(EXTENSION_SITE_BACKGROUND_URL)) {
					// removing all "overhead"
					part = part.replace(EXTENSION_URL_PROTOCOL, "");
					part = part.replace(EXTENSION_SITE_BACKGROUND_URL, "");
					extensionId = part;
					return part;
				}
			}
		}

		// we should not reach this line. If we reach it, that means our extension is not installed correctly.
		return null;
	}

	/**
	 * This method takes a page string (e.g. options.html) and navigates to this site.
	 *
	 * @param page The page we want to navigate to e.g. options.html
	 */
	public void navigateTo(String page) {
		driver.get(EXTENSION_URL_PROTOCOL + getId() + "/" + page);
	}

	/**
	 * Method for switching to the latest opened tab/window.
	 */
	public void switchToNewTab() {
		int handleSize = driver.getWindowHandles().size();
		driver.switchTo().window((String) driver.getWindowHandles().toArray()[handleSize - 1]);
	}

	/**
	 * Returns the remoteWebDriver.
	 *
	 * @return remoteWebDriver
	 */
	public RemoteWebDriver getDriver() {
		return this.driver;
	}

	/**
	 * For tearing down the web driver.
	 */
	public void tearDown() {
		this.driver.close();
		this.driver.quit();
	}
}

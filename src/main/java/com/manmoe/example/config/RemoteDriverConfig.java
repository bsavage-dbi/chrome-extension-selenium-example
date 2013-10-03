package com.manmoe.example.config;

import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class for configuring and build a ChromeWebDriver.
 *
 * @author Manuel Möhlmann <mail@manmoe.com>
 */
public class RemoteDriverConfig {
	/**
	 * This is the remote Url of the selenium head. You will get the url by an selenium grid provider, e.g. saucelabs.com
	 */
	private static final String REMOTE_URL = "";
	private static final String EXTENSION_FOLDER = "src/main/resources/firespotting.crx";

	protected DesiredCapabilities desiredCapabilities;
	protected ChromeOptions chromeOptions;

	/**
	 * Here web build the properties we need. Attention: the order matters!
	 */
	public RemoteDriverConfig() {
		buildChromeOptions();
		buildDesiredCapabilities();
	}

	/**
	 * Here we build the ChromeWebDriver we need to run the tests.
	 *
	 * @return Our full configured ChromeWebDriver ready for testing.
	 *
	 * @throws MalformedURLException If the remote URL is not appropriate
	 */
	public RemoteWebDriver buildRemoteDriver() {
		String remoteUrl = getRemoteUrl();

		if (remoteUrl == null) {
			remoteUrl = REMOTE_URL;
		}

		try {
			return createRemoteWebDriver(remoteUrl, desiredCapabilities);
		} catch (MalformedURLException e) {
			// TODO: add properly system logging
			System.out.println("Url for remote access is malformed. Please provide a valid URL.");
		}
		// should not happen.
		throw new RuntimeException("Remote Driver could not been build properly, because of an malformed remote url");
	}

	/**
	 * Here you can change the chrome options.
	 *
	 * @see https://code.google.com/p/chromedriver/wiki/CapabilitiesAndSwitches#Using_the_class
	 */
	protected void buildChromeOptions() {
		ChromeOptions options = createCromeOptions();
		options.addExtensions(new File(EXTENSION_FOLDER));
		options.addArguments("--start-maximized");

		this.chromeOptions = options;
	}

	/**
	 * Here you can add your capabilities you want to use.
	 *
	 * @see https://code.google.com/p/chromedriver/wiki/CapabilitiesAndSwitches#List_of_recognized_capabilities
	 */
	protected void buildDesiredCapabilities() {
		DesiredCapabilities capabilities = createDesiredCapabilitiesForChrome();

		// get Platform from environment
		String platformString = System.getenv("PLATFORM");
		if (platformString == null) {
			platformString = Platform.WINDOWS.toString();
		}
		Platform platform = Platform.valueOf(platformString);
		capabilities.setCapability("platform", platform);

		// if chrome options are not build yet, we have to handle it
		if (chromeOptions == null) {
			buildChromeOptions();
		}
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		this.desiredCapabilities = capabilities;
	}

	// ----------------- Just some helpful methods to make it more testable
	/**
	 * Just for creating a ChromeOptions instance.
	 */
	protected ChromeOptions createCromeOptions() {
		return new ChromeOptions();
	}

	/**
	 * Just for creating a DesiredCapabilities instance.
	 */
	protected DesiredCapabilities createDesiredCapabilitiesForChrome() {
		return DesiredCapabilities.chrome();
	}

	/**
	 * Method for getting the remoteUrl from environment variable, if set.
	 *
	 * @return the RemoteDriver url for starting the RemoteSeleniumDriver with.
	 */
	protected String getRemoteUrl() {
		return System.getenv("REMOTE_DRIVER_URL");
	}

	/**
	 * Creating the RemoteWebDriver.
	 */
	protected RemoteWebDriver createRemoteWebDriver(String remoteUrl, DesiredCapabilities desiredCapabilities) throws MalformedURLException {
		return new RemoteWebDriver(new URL(remoteUrl), desiredCapabilities);
	}

	/**
	 * Builds a local driver for debugging issues.
	 *
	 * @return local driver with same configuration as remote driver
	 */
	public ChromeDriver buildLocalDriver() {
		// it may be, that you must provide the path to the local driver here.
		//System.setProperty("webdriver.chrome.driver", "");
		return new ChromeDriver(desiredCapabilities);
	}
}

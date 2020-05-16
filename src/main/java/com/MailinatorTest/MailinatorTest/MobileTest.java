package com.MailinatorTest.MailinatorTest;

import org.testng.annotations.Test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.applitools.eyes.appium.Eyes;

public class MobileTest {

	@Test
	public void mobiletest() {
		// Set desired capabilities.
		DesiredCapabilities capabilities = new DesiredCapabilities();

		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("deviceName", "DEVICE_NAME");
		capabilities.setCapability("platformVersion", "PLATFORM_VERSION");
		// NOTE: 📣 Download this app from
		// https://bintray.com/applitools/Examples/Android_Demo_APK
		capabilities.setCapability("app", "app-debug.apk");
		capabilities.setCapability("browserName", "");
		capabilities.setCapability("automationName", "UiAutomator2");

	}
}

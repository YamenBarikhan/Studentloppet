package com.pvt152.StudentLoppet.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CountdownExtractor {

    public int extractCountdownDays() {
        String chromeDriverPath = System.getenv("CHROMEDRIVER_PATH");

        if (chromeDriverPath == null || chromeDriverPath.isEmpty()) {
            System.err.println("CHROMEDRIVER_PATH environment variable is not set.");
            return -1;
        }

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu", "--window-size=1920,1200", "--headless");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://midnattsloppet.com/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            WebElement countdownElement = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.cssSelector(".countdown__unit.js-countdown-days")));
            wait.until(ExpectedConditions
                    .not(ExpectedConditions.textToBe(By.cssSelector(".countdown__unit.js-countdown-days"), "")));

            String countdownText = countdownElement.getText();
            System.out.println("days left: " + countdownText);

            try {
                return Integer.parseInt(countdownText);
            } catch (NumberFormatException e) {
                System.out.println("Failed to convert countdown from text to integer: " + countdownText);
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            driver.quit();
        }
    }
}
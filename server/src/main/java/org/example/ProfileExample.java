package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;


public class ProfileExample {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();
        String appUsername = dotenv.get("APP_USERNAME");
        String appPassword = dotenv.get("APP_PASSWORD");

        // Initialize WebDriver with EdgeOptions and user-data-dir
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.addArguments("user-data-dir=./edge-profile"); // Set the profile directory
        edgeOptions.addArguments("profile-directory=Profile 99");
        WebDriver driver = new EdgeDriver(edgeOptions);

        // Target page URL
        String targetUrl = "https://kaizen-east.coppertreeanalytics.com/v3/#/clients/1794/buildings/5245";

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Navigate to the target page
        driver.get(targetUrl);

        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement signInButton = driver.findElement(By.className("btn-login"));

        // Enter credentials and click Sign In
        emailInput.sendKeys(appUsername);
        passwordInput.sendKeys(appPassword);
//        signInButton.click();

        // Wait for a brief moment (adjust as needed)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement spanElement = driver.findElement(By.xpath("//*[@id=\"page-content-wrapper\"]/div/div[1]/div[1]/breadcrumbs/ol/li[4]/span"));
        String text = spanElement.getText();
        System.out.println(text);

        // Close the browser
        driver.quit();
    }
}


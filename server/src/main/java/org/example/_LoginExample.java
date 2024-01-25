package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import java.io.*;
import java.util.Set;

public class _LoginExample {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();
        String appUsername = dotenv.get("APP_USERNAME");
        String appPassword = dotenv.get("APP_PASSWORD");

        // Initialize WebDriver (EdgeDriver in this case)
        WebDriver driver = new EdgeDriver();

        // Setup and navigate to the target page
        setupAndNavigate(driver, appUsername, appPassword);

        // Further automation logic here
        // ...

        // Close the browser
        driver.quit();
    }

    private static void setupAndNavigate(WebDriver driver, String appUsername, String appPassword) {
        // Load cookies if available
        loadCookies(driver, "cookies.ser");

        // Target page URL
        String targetUrl = "https://kaizen-east.coppertreeanalytics.com/v3/#/clients/1794/buildings/5245";

        // Navigate to the target page
        navigateToTargetPage(driver, targetUrl, appUsername, appPassword);
    }

    private static void navigateToTargetPage(WebDriver driver, String targetUrl, String appUsername, String appPassword) {
        // Navigate to the target URL
        driver.get(targetUrl);

        // Check if redirected to the login page
        if (isLoginPage(driver)) {
            // Login and obtain session cookies
            loginAndGetCookies(driver, appUsername, appPassword);

            // Refresh the page to apply the session
            driver.navigate().refresh();
        }

        // Allow time for the session to be applied
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Add any additional checks for successful navigation to the target page
        if (!isTargetPage(driver)) {
            // Handle the case where the navigation to the target page was not successful
            System.err.println("Failed to navigate to the target page.");
        }
    }

    private static boolean isLoginPage(WebDriver driver) {
        // Check if the login page is displayed based on the presence of a specific element
        try {
            WebElement passwordInput = driver.findElement(By.id("password"));
            return passwordInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isTargetPage(WebDriver driver) {
        // Check if the specified text is present on the target page
        try {
            WebElement spanElement = driver.findElement(By.xpath("//*[@id=\"page-content-wrapper\"]/div/div[1]/div[1]/breadcrumbs/ol/li[4]/span"));
            String text = spanElement.getText();
            return text.contains("Block T 伊利沙伯醫院日間醫療中心新翼");
        } catch (Exception e) {
            return false;
        }
    }

    private static void loginAndGetCookies(WebDriver driver, String appUsername, String appPassword) {
        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement signInButton = driver.findElement(By.className("btn-login"));

        // Enter credentials and click Sign In
        emailInput.sendKeys(appUsername);
        passwordInput.sendKeys(appPassword);
        signInButton.click();

        // Wait for a brief moment (adjust as needed)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if login was successful (you may need to add more validation)
        if (!isLoginPage(driver)) {
            // Login was successful, get and save session cookies
            Set<Cookie> cookies = driver.manage().getCookies();
            saveCookies(cookies, "cookies.ser"); // Specify the file name for storing cookies
        } else {
            // Handle the case where login was not successful
            System.err.println("Login failed.");
        }
    }

    private static void saveCookies(Set<Cookie> cookies, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(cookies);
            System.out.println("Cookies saved to: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadCookies(WebDriver driver, String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Set<Cookie> cookies = (Set<Cookie>) ois.readObject();

            // Iterate through each cookie and add it only if the domain matches
            String currentDomain = getDomainFromUrl(driver.getCurrentUrl());
            for (Cookie cookie : cookies) {
                String cookieDomain = getDomainFromCookie(cookie);
                if (cookieDomain.equals(currentDomain)) {
                    driver.manage().addCookie(cookie);
                } else {
                    System.err.println("Skipping cookie with invalid domain: " + cookie);
                }
            }

            System.out.println("Cookies loaded from: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            // Handle the case where loading cookies fails (optional)
            e.printStackTrace();
        }
    }

    private static String getDomainFromUrl(String url) {
        // Extract domain from the URL (simplified logic, adjust if needed)
        int startIndex = url.indexOf("://") + 3;
        int endIndex = url.indexOf("/", startIndex);
        if (endIndex == -1) {
            endIndex = url.length();
        }
        return url.substring(startIndex, endIndex);
    }

    private static String getDomainFromCookie(Cookie cookie) {
        // Extract domain from the cookie (simplified logic, adjust if needed)
        return cookie.getDomain().startsWith(".") ? cookie.getDomain().substring(1) : cookie.getDomain();
    }

}

package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class _MainChartsDelay {
    public static void main(String[] args) throws MalformedURLException {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();

        // Retrieve environment variables
        String appUsername = dotenv.get("APP_USERNAME");
        String appPassword = dotenv.get("APP_PASSWORD");
        String edgeUrl = dotenv.get("EDGE_URL");

        // Use the retrieved values as needed
        System.out.println("edgeUrl: " + edgeUrl);

        // Your application logic here
        WebDriver driver;

        if (edgeUrl != null && !edgeUrl.isEmpty()) {
            // RemoteWebDriver with EdgeOptions
            EdgeOptions edgeOptions = new EdgeOptions();
            // Set additional options if needed
            driver = new RemoteWebDriver(new URL(edgeUrl), edgeOptions);
        } else {
            // Local EdgeDriver with EdgeOptions
            EdgeOptions edgeOptions = new EdgeOptions();
            // Set additional options if needed
            driver = new EdgeDriver(edgeOptions);
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.get("https://kaizen-east.coppertreeanalytics.com/v3/#/signin");

            // Login
            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            emailInput.sendKeys(appUsername);
            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            passwordInput.sendKeys(appPassword);
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-login")));
            signInButton.click();

            // Navigate to the desired link
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Block T 伊利沙伯醫院日間醫療中心新翼"))).click();

            // List of table data XPath expressions
            List<String> tableDataXPaths = Arrays.asList(
                    "//span/b[text()='FDDA1-01']",
                    "//span/b[text()='FDDA1-03']"
                    // Add more XPath expressions if needed
            );

            // Iterate over table data XPaths
            for (String tableDataXPath : tableDataXPaths) {
                extractAndPrintTableData(driver, wait, tableDataXPath);
            }
        } finally {
            // Close the browser
            driver.quit();
        }
    }

    private static void extractAndPrintTableData(WebDriver driver, WebDriverWait wait, String tableDataXPath) {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Charts"))).click();

        // Click the associated view button
        WebElement targetElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(tableDataXPath)));
        WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(targetElement.findElement(By.xpath("./ancestor::div[@class='col-md-6']/following-sibling::div//a[@class='button']"))));
        viewButton.click();

        // Continue with the rest of the code for extracting data from the table
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@chart-name='instance.name' and contains(@id, 'logi-chart-')]")));

        WebElement firstIframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
        driver.switchTo().frame(firstIframe);

        WebElement secondIframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
        driver.switchTo().frame(secondIframe);

        try {
            // Extract data from the table
            StringBuilder allTableData = new StringBuilder();

            WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tblLowestValues")));

            for (WebElement row : table.findElements(By.tagName("tr"))) {
                for (WebElement column : row.findElements(By.tagName("td"))) {
                    allTableData.append(column.getText()).append("\t");
                }
                allTableData.append("\n"); // Move to the next line for the next row
            }

            // Output all table data
            System.out.println(allTableData.toString());
        } finally {
            // Reset to the default content
            driver.switchTo().defaultContent();
        }
    }

}

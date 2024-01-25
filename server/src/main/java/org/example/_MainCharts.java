package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class MainCharts {
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

        try {
            driver.get("https://kaizen-east.coppertreeanalytics.com/v3/#/signin");

            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
            // Login
            WebElement emailInput = driver.findElement(By.id("email"));
            emailInput.sendKeys(appUsername);
            WebElement passwordInput = driver.findElement(By.id("password"));
            passwordInput.sendKeys(appPassword);
            WebElement signInButton = driver.findElement(By.className("btn-login"));
            signInButton.click();

            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));
            // Navigate to the desired link
            driver.findElement(By.linkText("Block T 伊利沙伯醫院日間醫療中心新翼")).click();

            // List of table data XPath expressions
            List<String> tableDataXPaths = Arrays.asList(
                    "//span/b[text()='FDDA1-01']",
                    "//span/b[text()='FDDA1-03']"
                    // Add more XPath expressions if needed
            );

            // Iterate over table data XPaths
            for (String tableDataXPath : tableDataXPaths) {
                driver.findElement(By.linkText("Charts")).click();
                extractAndPrintTableData(driver, tableDataXPath);
            }
        } finally {
            // Close the browser
            driver.quit();
        }
    }

    private static void extractAndPrintTableData(WebDriver driver, String tableDataXPath) {
        // Click the associated view button
        WebElement targetElement = driver.findElement(By.xpath(tableDataXPath));
        WebElement viewButton = targetElement.findElement(By.xpath("./ancestor::div[@class='col-md-6']/following-sibling::div//a[@class='button']"));
        viewButton.click();

        // Continue with the rest of the code for extracting data from the table
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        // Locate the parent div based on its attributes
        WebElement parentDiv = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[@chart-name='instance.name' and contains(@id, 'logi-chart-')]")));

        // Locate the iframe inside the parent div
        WebElement firstIframe = parentDiv.findElement(By.tagName("iframe"));

        // Switch to the first iframe
        driver.switchTo().frame(firstIframe);

        // Now, locate the second iframe inside the first iframe
        WebElement secondIframe = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));

        // Switch to the second iframe
        driver.switchTo().frame(secondIframe);

        try {
            // Extract data from the table
            StringBuilder allTableData = new StringBuilder();

            WebElement table = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("tblLowestValues")));

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

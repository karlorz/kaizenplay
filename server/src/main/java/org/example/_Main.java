package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.edge.EdgeDriver;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class _Main {
    public static void main(String[] args) throws MalformedURLException {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();

        // Retrieve environment variables
        String appUsername = dotenv.get("APP_USERNAME");
        String appPassword = dotenv.get("APP_PASSWORD");
        String edgeUrl = dotenv.get("EDGE_URL");

        // Use the retrieved values as needed
//        System.out.println("APP_USERNAME: " + appUsername);
//        System.out.println("APP_PASSWORD: " + appPassword);
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

//        chromeOptions.setBrowserVersion("120.0");
//        driver = new RemoteWebDriver(new URL("http://192.168.88.196:4444"),chromeOptions);
        driver.get("https://kaizen-east.coppertreeanalytics.com/v3/#/signin");

        String title = driver.getTitle();
//        assertEquals("Bootstrap Datatables - examples & tutorial", title);

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.sendKeys(appUsername);
        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys(appPassword);

        // Find the "Sign In" button by class name and click it
        WebElement signInButton = driver.findElement(By.className("btn-login"));
        signInButton.click();

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));

        // Find and click the link using its link text
        WebElement link = driver.findElement(By.linkText("Block T 伊利沙伯醫院日間醫療中心新翼"));
        link.click();

        // Find the link with class "feature-bar-text" using its text content
        WebElement chartsLink = driver.findElement(By.linkText("Charts"));

        // Click on the "Charts" link
        chartsLink.click();

        // Find the element with the text "AHU Temperature out of range KPI Report (FA) R1"
//        WebElement targetElement = driver.findElement(By.xpath("//span/b[text()='AHU Temperature out of range KPI Report (FA) R1']"));
        WebElement targetElement = driver.findElement(By.xpath("//span/b[text()='FDDA1-01']"));

        // Click the associated view button
        WebElement viewButton = targetElement.findElement(By.xpath("./ancestor::div[@class='col-md-6']/following-sibling::div//a[@class='button']"));
        viewButton.click();

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


        // Now you can proceed with extracting data from the table inside the second iframe
        WebElement table = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("tblLowestValues")));

        // Extract data from the table
        StringBuilder allTableData = new StringBuilder();

        for (WebElement row : table.findElements(By.tagName("tr"))) {
            for (WebElement column : row.findElements(By.tagName("td"))) {
                allTableData.append(column.getText()).append("\t");
            }
            allTableData.append("\n"); // Move to the next line for the next row
        }

        // Output all table data
        System.out.println(allTableData.toString());
        // Close the browser
        driver.quit();
    }
}
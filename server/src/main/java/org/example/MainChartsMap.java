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
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainChartsDelay {
    public static void main(String[] args) throws MalformedURLException {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();

        // Retrieve environment variables
        String appUsername = dotenv.get("APP_USERNAME");
        String appPassword = dotenv.get("APP_PASSWORD");
        String edgeUrl = dotenv.get("EDGE_URL");

        // Use the retrieved values as needed
        System.out.println("edgeUrl: " + edgeUrl);

        String filePath1 = "/fdda_system.json";
        String[] systemNames = SystemReader.readSystemNames(filePath1);
        String filePath2 = "/fdda1_report.json";
        // Read report names from JSON file
        String[] reportNames = ReportReader.readReportNames(filePath2);

        // Initialize the HashMap array
        HashMap<String, String[]>[] allDataMaps = new HashMap[reportNames.length];

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

            // Navigate to the desired link for each reportName
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Block T 伊利沙伯醫院日間醫療中心新翼"))).click();

            for (int i = 0; i < reportNames.length; i++) {
                // Initialize the HashMap for the reportName
                HashMap<String, String[]> reportDataMap = new HashMap<>();

                // Generate XPath expressions based on the current reportName
                List<String> tableDataXPaths = Arrays.asList("//span/b[text()='" + reportNames[i] + "']");

                // Iterate over table data XPaths
                for (String tableDataXPath : tableDataXPaths) {
                    extractAndPopulateDataMap(driver, wait, tableDataXPath, reportDataMap);
                }

                // Set the reportDataMap to the corresponding index in allDataMaps
                allDataMaps[i] = reportDataMap;
            }
        } finally {
            // Close the browser
            driver.quit();
        }

        // Output the HashMap array
        for (HashMap<String, String[]> reportDataMap : allDataMaps) {
            for (String key : reportDataMap.keySet()) {
                System.out.println(key + ": " + Arrays.toString(reportDataMap.get(key)));
            }
        }
    }

    private static void extractAndPopulateDataMap(WebDriver driver, WebDriverWait wait, String tableDataXPath, HashMap<String, String[]> dataMap) {
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
            WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tblLowestValues")));

            for (WebElement row : table.findElements(By.tagName("tr"))) {
                List<WebElement> columns = row.findElements(By.tagName("td"));
                if (columns.size() >= 2) {
                    String systemName = columns.get(0).getText();
                    String reportValue = columns.get(1).getText();

                    // Populate the dataMap
                    if (!dataMap.containsKey(systemName)) {
                        dataMap.put(systemName, new String[]{reportValue});
                    } else {
                        dataMap.get(systemName)[0] = reportValue;
                    }
                }
            }
        } finally {
            // Reset to the default content
            driver.switchTo().defaultContent();
        }
    }
}

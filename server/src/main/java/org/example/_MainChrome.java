package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

public class Main {
    private static final String DEFAULT_VALUE = "-1";
    private static final int CONFIGURABLE_DELAY_SECONDS = 5;
    private static final int POLLING_INTERVAL_MILLIS = 500; // 500 milliseconds interval
    private static final int LONGER_INTERVAL_SECONDS = 10;
    private static final int LONGER_POLLING_MILLIS = 2000;

    public static void main(String[] args) throws MalformedURLException {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();

        // Retrieve environment variables
        String appUsername = dotenv.get("APP_USERNAME");
        String appPassword = dotenv.get("APP_PASSWORD");
        String edgeUrl = dotenv.get("EDGE_URL");
        String browser = dotenv.get("BROWSER");

        // Use the retrieved values as needed
        System.out.println("edgeUrl: " + edgeUrl);

        String filePath1 = "/fdda_system.json";
        String[] systemNames = SystemReader.readSystemNames(filePath1);
        String filePath2 = "/fdda1_report.json";
        // Read report names from JSON file
        String[] reportNames = ReportReader.readReportNames(filePath2);

        // Initialize the HashMap array
        HashMap<String, String[]>[] allDataMaps = new HashMap[reportNames.length];

        // Initialize the final data map
        HashMap<String, HashMap<String, String>> finalDataMap = new HashMap<>();

        // Your application logic here
        WebDriver driver;

        if ("chrome".equalsIgnoreCase(browser) && (edgeUrl == null || edgeUrl.isEmpty())) {
            // Use ChromeDriver locally
            ChromeOptions chromeOptions = new ChromeOptions();
            // Set additional options if needed
            driver = new ChromeDriver(chromeOptions);
        } else if ("edge".equalsIgnoreCase(browser) && (edgeUrl == null || edgeUrl.isEmpty())) {
            // Use EdgeDriver with EdgeOptions locally
            EdgeOptions edgeOptions = new EdgeOptions();
            // Set additional options if needed
            driver = new EdgeDriver(edgeOptions);
        } else {

            if ("chrome".equalsIgnoreCase(browser)) {
                ChromeOptions chromeOptions = new ChromeOptions();
                driver = new RemoteWebDriver(new URL(edgeUrl), chromeOptions);
            } else { // Default to Edge if browser is not specified or unknown
                EdgeOptions edgeOptions = new EdgeOptions();
                driver = new RemoteWebDriver(new URL(edgeUrl), edgeOptions);
            }
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(CONFIGURABLE_DELAY_SECONDS));
        wait.pollingEvery(Duration.ofMillis(POLLING_INTERVAL_MILLIS));

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

            // Iterate over report names
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

                // Populate the finalDataMap with preset systemNames and reportNames
                for (String systemName : systemNames) {
                    finalDataMap.computeIfAbsent(systemName, k -> new HashMap<>());
                    String value = reportDataMap.containsKey(systemName)
                            ? reportDataMap.get(systemName)[0]
                            : DEFAULT_VALUE;
                    finalDataMap.get(systemName).put(reportNames[i], value);
                }
            }
        } finally {
            // Close the browser
            driver.quit();
        }

        // Output the final data map
        System.out.print("System\t");
        for (String reportName : reportNames) {
            System.out.print(reportName + "\t");
        }
        System.out.println();

        for (String systemName : systemNames) {
            System.out.print(systemName + "\t");
            for (String reportName : reportNames) {
                String value = finalDataMap.get(systemName).get(reportName);
                System.out.print((value != null) ? value : "");
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    private static void extractAndPopulateDataMap(WebDriver driver, WebDriverWait wait, String tableDataXPath, HashMap<String, String[]> dataMap) {
        // Create a WebDriverWait with a longer interval for this specific condition
        WebDriverWait longerWait = new WebDriverWait(driver, Duration.ofSeconds(LONGER_INTERVAL_SECONDS));
        longerWait.pollingEvery(Duration.ofMillis(LONGER_POLLING_MILLIS));

        longerWait.until(ExpectedConditions.elementToBeClickable(By.linkText("Charts"))).click();

        // Click the associated view button
        WebElement targetElement = longerWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(tableDataXPath)));
        WebElement viewButton = longerWait.until(ExpectedConditions.elementToBeClickable(targetElement.findElement(By.xpath("./ancestor::div[@class='col-md-6']/following-sibling::div//a[@class='button']"))));
        viewButton.click();

        // Continue with the rest of the code for extracting data from the table
        longerWait.until(ExpectedConditions.presenceOfElementLocated(
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

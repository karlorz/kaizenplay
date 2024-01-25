package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class _envdemo {
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
        driver.get("https://mdbootstrap.com/docs/standard/data/datatables/");

        String title = driver.getTitle();
//        assertEquals("Bootstrap Datatables - examples & tutorial", title);

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        // Find the table element using its locator (e.g., XPath, CSS selector)
        WebElement table = driver.findElement(By.xpath("//*[@id=\"section-basic-example\"]/section[1]/div/section/div/div[1]/table"));

        // Get all rows from the table
        java.util.List<WebElement> rows = table.findElements(By.tagName("tr"));

        // Variable to store the table data
        StringBuilder tableData = new StringBuilder();

        // Iterate through each row and append data to the variable
        for (WebElement row : rows) {
            java.util.List<WebElement> cells = row.findElements(By.tagName("td"));

            for (WebElement cell : cells) {
                tableData.append(cell.getText()).append("\t");
            }

            tableData.append("\n"); // Move to the next line after each row
        }

        // Print the table data at the end
        System.out.println("Table Data:\n" + tableData);

        // Close the browser
        driver.quit();
    }
}
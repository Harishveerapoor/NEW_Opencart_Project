package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

public class BaseClass {

    public static WebDriver driver;
    public Logger logger; // Logger variable
    public Properties p; // Properties variable

    @BeforeClass(groups = {"Sanity", "Regreesion", "Master"})
    @Parameters({"os", "browser"})
    public void setup(String os, String br) throws IOException {

        // Initialize logger first
        logger = LogManager.getLogger(this.getClass());

        // Load config.properties
        FileReader file=new FileReader("./src//test//resourcess//config.properties");// Fixed path
        p = new Properties();
        p.load(file);

        // Selenium Grid or local execution
        if (p.getProperty("execution_env").equalsIgnoreCase("remote")) {

            DesiredCapabilities capbale = new DesiredCapabilities();

            // OS selection
            if (os.equalsIgnoreCase("window")) {
                capbale.setPlatform(Platform.WIN11);
            } else if (os.equalsIgnoreCase("mac")) {
                capbale.setPlatform(Platform.MAC);
            } else {
                logger.error("No matching OS found for Selenium Grid");
                return;
            }

            // Browser selection
            switch (br.toLowerCase()) {
                case "chrome":
                    capbale.setBrowserName("chrome");
                    break;
                case "firefox":
                    capbale.setBrowserName("firefox");
                    break;
                default:
                    logger.error("No matching browser found for Selenium Grid");
                    break;
            }

            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capbale);

        } else if (p.getProperty("execution_env").equalsIgnoreCase("local")) {

            // Local browser selection
            switch (br.toLowerCase()) {
                case "chrome":
                    driver = new ChromeDriver();
                    break;
                case "edge":
                    driver = new EdgeDriver();
                    break;
                case "firefox":
                    driver = new FirefoxDriver();
                    break;
                default:
                    logger.error("Invalid browser name provided");
                    return;
            }

            // Optional headless Chrome
            if (br.equalsIgnoreCase("chrome")) {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                driver = new ChromeDriver(options); // Only applies if you want headless Chrome
            }
        }

        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(50, TimeUnit.MILLISECONDS);
        driver.get(p.getProperty("URL"));
        driver.manage().window().maximize();

        logger.info("URL launched: " + p.getProperty("URL"));
    }

    @AfterClass(groups = {"Sanity", "Regreesion", "Master"})
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("Browser closed successfully");
        }
    }

    // ---------------- Random Methods (UNCHANGED) ----------------
    public String randomString() {
        String genaratedString = RandomStringUtils.randomAlphabetic(5);
        return genaratedString;
    }

    public String randomNumber() {
        String genaratedNumaber = RandomStringUtils.randomAlphanumeric(10);
        return genaratedNumaber;
    }

    public String randomAphaNumaric() {
        String genaratedString = RandomStringUtils.randomAlphabetic(5);
        String genaratedNumaber = RandomStringUtils.randomAlphanumeric(10);
        return (genaratedString + genaratedNumaber);
    }

    // ---------------- Capture Screenshot ----------------
    public String captureScreen(String tname) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

        String targetFilePath = System.getProperty("user.dir") + "\\screenshots\\" + tname + "_" + timeStamp + ".png";
        File targetFile = new File(targetFilePath);

        // Use FileUtils to reliably copy the file
        FileUtils.copyFile(sourceFile, targetFile);

        logger.info("Screenshot captured: " + targetFilePath);
        return targetFilePath;
    }
}


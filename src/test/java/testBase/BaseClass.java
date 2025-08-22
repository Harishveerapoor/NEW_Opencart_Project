package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseClass {

    public static WebDriver driver;
    public Properties p;
    public Logger logger;   // 🔹 Logger variable

    @BeforeClass
    public void setUp() throws IOException {
        // Initialize logger
        logger = LogManager.getLogger(this.getClass());

        // Load config.properties
        FileReader file = new FileReader("./src/test/resources/config.properties");
        p = new Properties();
        p.load(file);

        String env = p.getProperty("execution_env", "local").toLowerCase();

        if (env.equals("local")) {
            logger.info("Execution Environment: Local");
            driver = new ChromeDriver();   // local will run normal Chrome
        } 
        else if (env.equals("remote")) {
            logger.info("Execution Environment: Remote (Headless)");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            driver = new ChromeDriver(options);
        } 
        else if (env.equals("firefox")) {
            driver = new FirefoxDriver();
        } 
        else if (env.equals("edge")) {
            driver = new EdgeDriver();
        } 
        else {
            logger.error("Invalid browser/exec_env in config.properties");
            throw new RuntimeException("Browser not supported: " + env);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(p.getProperty("URL"));

        logger.info("URL launched: " + p.getProperty("URL"));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("Browser closed successfully");
        }
    }

    // Generate random string
    public String randomString() {
        String generatedString = RandomStringUtils.randomAlphabetic(5);
        return generatedString;
    }

    // Generate random number (10-digit alphanumeric)
    public String randomNumber() {
        String generatedNumber = RandomStringUtils.randomAlphanumeric(10);
        return generatedNumber;
    }

    // Generate random Alpha + Numeric combination
    public String randomAphaNumaric() {
        String generatedString = RandomStringUtils.randomAlphabetic(5);
        String generatedNumber = RandomStringUtils.randomNumeric(3);
        return (generatedString + generatedNumber);
    }

    // Capture Screenshot
    public String captureScreen(String tname) throws IOException {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        String targetPath = System.getProperty("user.dir") + "/screenshots/" + tname + ".png";
        File target = new File(targetPath);
        FileUtils.copyFile(source, target);

        logger.info("Screenshot captured: " + targetPath);
        return targetPath;
    }
}

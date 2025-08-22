package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
import org.testng.annotations.Parameters;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

    public static WebDriver driver;
    public Logger logger;
    public Properties p;

    @BeforeClass(groups = { "Sanity", "Regression", "Master" })
    @Parameters({ "browser" })
    public void setup(String br) throws IOException {

        // Initialize Logger
        logger = LogManager.getLogger(this.getClass());

        // Load config.properties
        FileReader file = new FileReader("./src//test//resourcess//config.properties");
        p = new Properties();
        p.load(file);

        logger.info("Execution Environment: " + p.getProperty("execution_env"));

        // Local execution
        if (p.getProperty("execution_env").equalsIgnoreCase("local")) {
            switch (br.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    driver = new ChromeDriver(options);
                    break;

                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                    break;

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    break;

                default:
                    logger.error("Invalid browser name in master.xml: " + br);
                    throw new RuntimeException("Browser not supported: " + br);
            }
        } else {
            logger.error("Currently only 'local' execution_env is supported.");
            throw new RuntimeException("Unsupported execution_env: " + p.getProperty("execution_env"));
        }

        // Browser setup
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        // Open URL from config
        driver.get(p.getProperty("URL"));
        logger.info("URL launched: " + p.getProperty("URL"));
    }

    @AfterClass(groups = { "Sanity", "Regression", "Master" })
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("Browser closed successfully");
        }
    }

    // --------------- Random Generators (Do not change) ---------------
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

    // --------------- Capture Screenshot ---------------
    public String captureScreen(String tname) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        String targetPath = System.getProperty("user.dir") + "\\screenshots\\" + tname + "_" + timeStamp + ".png";
        File target = new File(targetPath);
        source.renameTo(target);

        logger.info("Screenshot captured: " + targetPath);
        return targetPath;
    }
}

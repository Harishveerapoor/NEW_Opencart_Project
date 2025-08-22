package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.beust.jcommander.Parameter;

import org.apache.logging.log4j.Logger;  // this important one we need import for logger 
import org.apache.logging.log4j.LogManager;// these also important



public class BaseClass {

public static WebDriver driver;
// part 2-> we using here logger
public Logger logger;
// part 3-> property class
public Properties p;
	
	@BeforeClass(groups= {"Sanity","Regreesion","Master"})
	@Parameters({"os","browser"})
	public void setup(String os, String br) throws IOException {
		
		// part -4] property class
		
		FileReader file=new FileReader("./src//test//resourcess//config.properties");
		p=new Properties();
		p.load(file);
		
		// part -2]
	    logger=LogManager.getLogger(this.getClass());  // the logg method help to see what are the class are exicuted and storing each record
		                                         // after this we nedd to go for TC_AccountRegisterationTest.java class see their
	    
	    
	    
	    // part - 10 selenium grid
	    
	    if(p.getProperty("execution_env").equals("remote")) {
	    	
	    	DesiredCapabilities capbale=new DesiredCapabilities();
	    	
	    	// os
	    	
	    	if(os.equalsIgnoreCase("window")) {
	    		capbale.setPlatform(Platform.WIN11);
	    	}
	    	else if(os.equalsIgnoreCase("mac")) {
	    		
	    		capbale.setPlatform(Platform.MAC);
	    	}
	    	else {
	    		System.out.println("no matching os");
	    		return;
	    	}
	    	
	    	switch(br.toLowerCase()) {
	    	
	    	case "chrome":capbale.setBrowserName("chrome");break;
	    	case "firefox":capbale.setBrowserName("firefox");break;
	    	default:System.out.println("no browsing");break;
	    	}
	    	
	    	driver=new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),capbale);
	    }
	    
	    // these is for for opening a browser headed mode
	    
//	    if(p.getProperty("execution_env").equals("local")) {
//	    
//	    	// Part -3]
//		    switch(br.toLowerCase()) {
//		    
//		    case "chrome" : driver=new ChromeDriver();break;
//		    case "edge" : driver=new EdgeDriver();break;
//		    case "firefox" : driver=new FirefoxDriver();break;
//		    default : System.out.println("Invalid browser name...");return;
//	    
//	    }
	  
	    // this is for not opening broweser  =headless mode
	    
		    if (p.getProperty("execution_env").equals("local")) {
		        switch (br.toLowerCase()) {

		            case "chrome":
		                ChromeOptions chromeOptions = new ChromeOptions();
		                chromeOptions.addArguments("--headless");
		                chromeOptions.addArguments("--no-sandbox");
		                chromeOptions.addArguments("--disable-dev-shm-usage");
		                driver = new ChromeDriver(chromeOptions);
		                break;

		            case "firefox":
		                FirefoxOptions firefoxOptions = new FirefoxOptions();
		                firefoxOptions.addArguments("--headless");
		                driver = new FirefoxDriver(firefoxOptions);
		                break;

		            case "edge":
		                EdgeOptions edgeOptions = new EdgeOptions();
		                edgeOptions.addArguments("--headless=new");
		                driver = new EdgeDriver(edgeOptions);
		                break;

		            default:
		                System.out.println("Invalid browser name...");
		                return;
		        }
		    }

	    
	    
	    
	    // Part -3]
//	    switch(br.toLowerCase()) {
//	    
//	    case "chrome" : driver=new ChromeDriver();break;
//	    case "edge" : driver=new EdgeDriver();break;
//	    case "firefox" : driver=new FirefoxDriver();break;
//	    default : System.out.println("Invalid browser name...");return;
//	    } // excuite in master.xml 
		
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(50,TimeUnit.MILLISECONDS);
		
		driver.get(p.getProperty("URL"));  // reading from property file
		driver.manage().window().maximize();
		
		
	}
	

	
	@AfterClass(groups= {"Sanity","Regreesion","Master"})
	public void tearDown() {
		
		driver.quit();
	}
	public String randomString() {
		String genaratedString=RandomStringUtils.randomAlphabetic(5);
		return genaratedString;
		
	}
	
	public String randomNumber() {
		String genaratedNumaber=RandomStringUtils.randomAlphanumeric(10);
		return genaratedNumaber;
		
	}
	public String randomAphaNumaric() {
		String genaratedString=RandomStringUtils.randomAlphabetic(5);
		String genaratedNumaber=RandomStringUtils.randomAlphanumeric(10);
		return (genaratedString+genaratedNumaber);
		
	}
	
	
	// part 8
	  public String captureScreen(String tname) throws IOException {
	        
	        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
	        
	        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
	        File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

	        String targetFilePath = System.getProperty("user.dir") + "\\screenshots\\" + tname + "_" + timeStamp + ".png";
	        File targetFile = new File(targetFilePath);

	        // Copy screenshot to destination
	        sourceFile.renameTo(targetFile);

	        return targetFilePath;
	    }
}
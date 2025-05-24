package Bpack;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class PATclass {
	static WebDriver driver;
	 static Properties prop;

	public static void main(String[] args) throws InterruptedException, IOException {

		 prop = new Properties();
	     String propPath = System.getProperty("user.dir") + "\\src\\test\\java\\Bpack\\data.properties";
	     FileInputStream fis = new FileInputStream(propPath);
		 prop.load(fis);
		
		String browserName=prop.getProperty("browser");

		if(browserName.equalsIgnoreCase("chrome")) {
			
			WebDriverManager.chromedriver().setup();
			driver=new ChromeDriver();
		}else if(browserName.equalsIgnoreCase("firefox")) {
			WebDriverManager.firefoxdriver().setup();
			driver=new FirefoxDriver();
		}else if(browserName.equalsIgnoreCase("IE")) {
			WebDriverManager.iedriver().setup();
		    driver=new InternetExplorerDriver();
		}
		
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
		driver.get(prop.getProperty("url"));
		driver.findElement(By.xpath("//input[contains(@class,'searchField')]")).sendKeys(prop.getProperty("input"));
		driver.findElement(By.xpath("//button[@class='green']")).click();
		driver.findElement(By.xpath("(//li[contains(@class,'medNum card titlePreview flex center-h')])[1]"))
		.click();
		Thread.sleep(3000);
	 JavascriptExecutor js = (JavascriptExecutor) driver;
	 
	 //I found this snippet online; it retrieves hidden JSON data from the report card 
	        String jsonText = (String) js.executeScript(
	            "let elements = document.querySelectorAll('li.result.card');" +
	            "for (let el of elements) {" +
	            "  let hiddenDiv = el.querySelector('div[style*=\"display: none\"]');" +
	            "  if (hiddenDiv) return hiddenDiv.innerText;" +
	            "}" +
	            "return 'Not Found';"
	        );

	       
	        String filingDate = "Not Found";
	        for (String line : jsonText.split("\n")) {
	            if (line.contains("\"filing\"")) {
	                filingDate = line.split(":")[1].replace("\"", "").replace(",", "").trim();
	                break;
	            }
	            
	        }

	   
	        
	        boolean insideDates = false;
	        String publicationDate = "Not Found";

	        for (String line : jsonText.split("\n")) {
	            line = line.trim();

	            if (line.contains("\"dates\"")) {
	                insideDates = true;
	                continue;
	            }

	            if (insideDates) {
	                if (line.contains("\"publication\"")) {
	                    publicationDate = line.split(":")[1].replace("\"", "").replace(",", "").trim();
	                    break;
	                }
	                if (line.contains("}")) {
	                    insideDates = false;
	                }
	            }
	        }
	        if (filingDate.contains("T")) {
	        	filingDate = filingDate.split("T")[0];
	        }

	        
	        System.out.println("Filing Date: " + filingDate);

	        
	       if (publicationDate.contains("T")) {
	       publicationDate = publicationDate.split("T")[0];
	        }

	        System.out.println("Publication Date: " + publicationDate);
	       
	        
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	     
	     LocalDate filing = LocalDate.parse(filingDate, formatter);
	     LocalDate publication = LocalDate.parse(publicationDate, formatter);

	   
	     long daysBetween = ChronoUnit.DAYS.between(filing, publication);

	     System.out.println("Difference in Days: " + daysBetween);
	        driver.quit();
		}

	}

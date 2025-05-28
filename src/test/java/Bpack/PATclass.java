package Bpack;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class PATclass {
	static WebDriver driver;
	 static Properties prop;
	 static WebDriverWait wait;
	 

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

		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		if (prop.getProperty("input").isBlank()) {
			
			if(true) {
				
				Wait<WebDriver> wait = new FluentWait<>(driver)
					    .withTimeout(Duration.ofSeconds(30))
					    .pollingEvery(Duration.ofSeconds(2))
					    .ignoring(NoSuchElementException.class)
					    .ignoring(ElementClickInterceptedException.class);

					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='margin-right']"))).click();
				//System.out.println("*inside if*1*");
				//Thread.sleep(3000);
			
				
				 
				//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//button[@class='margin-right']")));
				//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//div[@style='display: none;'])[1]")));
			//driver.findElement(By.xpath("//button[@class='margin-right']")).click();
			//System.out.println("inside if *2*");
					
            //Actions action=new Actions(driver);
			//action.moveToElement(Button).build().perform();
			
			}}else {
				System.out.println("inside else*2*");
				driver.findElement(By.xpath("//input[contains(@class,'searchField')]")).sendKeys(prop.getProperty("input"));
				driver.findElement(By.xpath("//button[@class='margin-right']")).click();
			
			}
        
        
		driver.findElement(By.xpath("//button[@class=\"green\"]")).click();
		
		
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//li[contains(@class,'medNum card titlePreview flex center-h')])[1]"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//div[@style='display: none;'])[1]")));

	 JavascriptExecutor js = (JavascriptExecutor) driver;
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

	     
	       if (publicationDate.contains("T")) {
	         publicationDate = publicationDate.split("T")[0];
	        }

	       
	       
	      
	       if (!"null".equals(filingDate)) {
	    	    System.out.println("Filing Date: " + filingDate);
	    	} else {
	    	    System.out.println("Filing date missing – fetch from another table.");
	    	    
	    	    
	    	}

	    	if (!"null".equals(publicationDate)) {
	    	    System.out.println("Publication Date: " + publicationDate);
	    	} else 
	    	{
	    		System.out.println("Publication date iss missing – fetch from another table.");
	    		JavascriptExecutor js2 = (JavascriptExecutor) driver;

	    		
	    		String jsonText2 = (String) js2.executeScript(
	    		    "let el = document.evaluate(\"(//li[contains(@class,'result')])[2]\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
	    		    "if (el) {" +
	    		    "  let hiddenDiv = el.querySelector('div[style*=\"display: none\"]');" +
	    		    "  if (hiddenDiv) return hiddenDiv.textContent;" +
	    		    "}" +
	    		    "return null;"
	    		);

	    		
		        String filingDate2 = "Not Found";
		        for (String line : jsonText2.split("\n")) {
		            if (line.contains("\"filing\"")) {
		                filingDate2 = line.split(":")[1].replace("\"", "").replace(",", "").trim();
		                break;
		            }
		            
		        }
		        if (filingDate2.contains("T")) {
		        	filingDate2 = filingDate2.split("T")[0];
		        }
		        
		        try {
		        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			     
			     LocalDate filing1 = LocalDate.parse(filingDate, formatter2);
			     LocalDate anotherfiling = LocalDate.parse(filingDate2, formatter2);

			    
			     long daysBetween2 = ChronoUnit.DAYS.between(filing1, anotherfiling);
			     System.out.println("Filing Date from 2nd table: " + anotherfiling);

			     System.out.println("Difference in Days: " + daysBetween2);
		        }catch (DateTimeParseException e) {
		        	System.out.println("some minor xception were handled here");
		        }
	
    
	    	}
	    		
	        try {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	     
	     LocalDate filing = LocalDate.parse(filingDate, formatter);
	     LocalDate publication = LocalDate.parse(publicationDate, formatter);

	    
	     long daysBetween = ChronoUnit.DAYS.between(filing, publication);

	     System.out.println("Difference in Days: " + daysBetween);
	        
	        }catch (DateTimeParseException e) {
	        	System.out.println("some minor eception were handled here");
	        }
	        
	       driver.quit();
	  }
	}

	
	
	
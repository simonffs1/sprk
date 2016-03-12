package start;

import org.testng.asserts.*;
import org.testng.annotations.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;

public class smoke {
	
	//Java client 3.1
	//private AndroidDriver<WebElement> driver;
	
	//public
	AndroidDriver driver;
	WebDriverWait wait;
	TouchAction touch;
	
	@SuppressWarnings("rawtypes")

	@BeforeClass
	//Setup
	public void setup() throws MalformedURLException, InterruptedException {
		
		File appDir = new File("src");
		File app = new File(appDir, "ll-1.3.0.113-staging.apk");
		
		//appium specific configuration
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
		cap.setCapability(MobileCapabilityType.DEVICE_NAME,"Android device");
		cap.setCapability(MobileCapabilityType.APP,app.getAbsolutePath());
		
		//create objects
		driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"),cap);
		wait = new WebDriverWait(driver,30);
		touch = new TouchAction(driver);
	}
	
	public void dismissHint(){
		try{
			if(driver.findElement(By.id("com.sphero.sprk:id/notification_title")).isDisplayed() == true ){
			driver.findElementById("com.sphero.sprk:id/notification_close").click();
			System.out.println("Hint view exists, closed now");
			}
		}
		catch(Exception e){
			System.out.println("Hint view does not exist, skipping dismissal");
		}
		
	}
	
	/*
	@Test
	public void appLaunch(){
		System.out.println("Waiting for first screen after splash");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
		System.out.println(driver.currentActivity());
		System.out.println("Finished, loaded on first screen");
	}
	*/
	
	@Test
	//Precondition: Signed out
	public void signIn() throws Exception{
		//Check activity
		System.out.println(driver.currentActivity());
		
		//check if hint view is visible, if it is close it otherwise skip
		dismissHint();
		
		//Menu button
		driver.findElementByXPath("//*[@class='android.widget.ImageButton' and @index='0']").click();
		System.out.println("Clicked on Menu");
		//Sign in button
		driver.findElementById("com.sphero.sprk:id/profile_header_not_signed_in").click();
		System.out.println("Clicked on Pocket Nav Sign in");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/header")));
		driver.findElementById("com.sphero.sprk:id/username_et").sendKeys("s2");
		driver.findElementById("com.sphero.sprk:id/password_et").sendKeys("1");
		driver.findElementById("com.sphero.sprk:id/sign_in_button").click();
		System.out.println("Clicked on Pocket Nav Sign in");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
		
		//check if hint view is visible, if it is close it otherwise ski
		dismissHint();

	}
	
	@Test(dependsOnMethods = {"signIn"})
	public void signOut(){
		driver.findElementByXPath("//*[@class='android.widget.ImageButton' and @index='0']").click();
		driver.findElementById("com.sphero.sprk:id/profile_header_signed_in").click();
		System.out.println("Signed in");
		driver.findElementById("com.sphero.sprk:id/sign_out_button").click();
		driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click();
		System.out.println("Signing Out");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
		dismissHint();
		driver.findElementByXPath("//*[@class='android.widget.ImageButton' and @index='0']").click();
		driver.findElementById("com.sphero.sprk:id/profile_header_not_signed_in");
		System.out.println("Signed out confirmed");
		
	}
	@Test 
	//Check unauthenticated user gate prompt visibility
	public void userGates(){
		dismissHint();
		driver.findElementByXPath("//*[@class='android.widget.ImageButton' and @index='0']").click();
		//Click Explore from pocket nav
		//driver.findElementByXPath("//*[@class='android.widget.TextView' and @text='Explore']").click();
		//driver.findElementByXPath(("//android.widget.TextView[contains(@resource-id,'com.sphero.sprk:id/menu_item_text') and @text='Explore']")).click();
		List<WebElement> allTextView = driver.findElements(By.className("android.widget.TextView"));
		java.util.Iterator<WebElement> i = allTextView.iterator();
		while(i.hasNext()) {
		    WebElement row = i.next();
		    System.out.println(row.getText());
		    if(row.getText().equals("Explore")){
		    	row.click();
		    	System.out.println("Click explore");
		    }
		}
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
		dismissHint();
		//Click on the first program
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/program_image")));
		driver.findElementById("com.sphero.sprk:id/program_image").click();
		System.out.println("Clicked a program ");
		//Wait for the drawer to appear
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar")));
		//Click on View
		driver.findElementById("com.sphero.sprk:id/view_button").click();
		System.out.println("Clicked View");
		driver.findElementById("com.sphero.sprk:id/content");
		System.out.println("View User Gate Present");
		driver.findElementById("com.sphero.sprk:id/buttonDefaultNegative").click();
		System.out.println("Clicked Maybe Later");
		//Click on Make a Copy
		driver.findElementById("com.sphero.sprk:id/make_a_copy_button").click();
		driver.findElementById("com.sphero.sprk:id/content");
		System.out.println("Copy User Gate Present");
		driver.findElementById("com.sphero.sprk:id/buttonDefaultNegative").click();
		System.out.println("Clicked Maybe Later");
		//Close action sheet
		driver.findElementById("com.sphero.sprk:id/dialog_close").click();
		//Click on Media
		driver.findElementByXPath("//*[@class='android.support.v7.a.d' and @index='1']").click();
		//Wait for media to load
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/program_image")));
		//Click Add new media button
		driver.findElementById("com.sphero.sprk:id/floating_add_new_media_button").click();
		driver.findElementById("com.sphero.sprk:id/content");
		System.out.println("Media User Gate Present");
		driver.findElementById("com.sphero.sprk:id/buttonDefaultNegative").click();
	}

	
	@AfterClass
	public void teardown() throws InterruptedException{
		System.out.println("Sleeping for 5 seconds then teardown and quit");
		Thread.sleep(5000);
		driver.quit();
	}

}
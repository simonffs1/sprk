package start;

import org.testng.Assert;
import org.testng.Assert.*;
import org.testng.annotations.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;

public class draftTest extends androidInterface{
	//Java client 3.1
		//private AndroidDriver<WebElement> driver;
		
		//public
		AndroidDriver driver;
		AppiumDriver driverWeb;
		WebDriverWait wait;
		WebDriverWait waitShort;
		WebDriverWait waitLong;
		TouchAction touch;
		String presentScreen;
		Boolean uploadSuccess;
		
		@SuppressWarnings("rawtypes")

		@BeforeClass
		//Setup
		public void setup() throws MalformedURLException, InterruptedException {
			
			File appDir = new File("src");
			File app = new File(appDir, "ll-1.3.0.115-staging.apk");
			
			//appium specific configuration
			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
			cap.setCapability(MobileCapabilityType.DEVICE_NAME,"Android device");
			cap.setCapability(MobileCapabilityType.APP,app.getAbsolutePath());
			//.onboarding.OnboardingActivity
			
			
			//create objects
			driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"),cap);
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			//driver.startActivity("com.sphero.sprk", ".onboarding.OnboardingActivity");
			wait = new WebDriverWait(driver,60);
			waitShort = new WebDriverWait(driver,10);
			waitLong = new WebDriverWait(driver,240);
			touch = new TouchAction(driver);
			uploadSuccess = true;
			//remove this for bluetooth dialog at splash screen
			
		}
		
		@BeforeMethod
		//Always check if at Build
		public void startPoint() throws Exception{
			
			//back out of edit screen 
			if(uploadSuccess==false){
				System.out.println("Uploading failed, backing out to Build");
				driver.pressKeyCode(AndroidKeyCode.BACK);
				driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click(); //discard change confirm
			}
			
			//Wait if on splash screen
			String currentScreen = driver.currentActivity().toLowerCase();
			while(currentScreen.contains("splash")){
				System.out.println("On splash screen");
				Thread.sleep(5000);
				currentScreen = driver.currentActivity().toLowerCase();
			}
			if(currentScreen.contains("explore")){
				driver.pressKeyCode(AndroidKeyCode.BACK);
			}
			
			//wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
			String screen = driver.findElementById("com.sphero.sprk:id/toolbar_title").getText();
			Assert.assertEquals(screen,"Build");
			dismissHint();
			//String screen = driver.findElementByXPath("//android.view.View[@resource-id='com.sphero.sprk:id/toolbar']/android.widget.TextView").getText();
			//Assert.assertEquals(screen,"Build");
			//System.out.println("Starting on Build screen");
		}
		
		@Test 
		public void deleteAll(){
			testLogTitle("Delete Programs while signed in");
			
			while(countProgram()>1){
			driver.findElementById("com.sphero.sprk:id/overflow_menu").click();
			//Check Edit title
			String fragmentTitle = driver.findElement(By.id("com.sphero.sprk:id/dialog_title")).getText();
			Assert.assertEquals(fragmentTitle, "Edit");
			driver.findElementById("com.sphero.sprk:id/delete_button").click();
			clickOk();
			checkToolBarTitle("Build");
			//Assert.assertEquals(countProgram(), numOfProg-1);
			System.out.println("Program has been deleted");
			}
			//*check name
		}
		
		//@Test
		public void clickProfileLink(){
			driver.findElementByXPath("//*[@class='android.widget.ImageButton' and @index='0']").click();
			System.out.println("Clicked on Menu");
			//Sign in button
			driver.findElementById("com.sphero.sprk:id/profile_header_not_signed_in").click();
			System.out.println("Clicked on Pocket Nav Sign in");
			driver.findElement(By.id("com.sphero.sprk:id/forgot_password_button")).click();
			Set <String> contextNames = driver.getContextHandles();
			for (String contextName : contextNames) {
			System.out.println(contextNames); 
			}
			//driver.context(contextNames.toArray()[1]); // set context to WEBVIEW_1
			
		}
		


		
		//@Test
		public void clickSampleTab(){
			List<WebElement> tabs = driver.findElementsByXPath("//android.support.v7.a.d/android.widget.TextView");
			for(WebElement i : tabs){
				if(i.getText().equals("Sample Programs")){
					i.click();
					System.out.println("clicked on sample programs");
				}
			}
		}
		
		//@Test(dependsOnMethods = {"signIn"}) //finish all sign in required tests first before signing out
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
			driver.pressKeyCode(AndroidKeyCode.BACK);
			Assert.assertEquals(countProgram(), 1);
		}
		
		
		//@Test
		public void getActivity() throws Exception{
			String currentScreen = driver.currentActivity();
			System.out.println(driver.currentActivity());
			Thread.sleep(15000);
			String nextScreen = driver.currentActivity();
			System.out.println(driver.currentActivity());
			while(true){
				if (currentScreen.equals(nextScreen)){
					Thread.sleep(10000);
				}
				else{
					break;
				}
			}
			
		}
		
		
		
		public int countProgram(){
			String xpath = "//android.support.v7.widget.RecyclerView/descendant::android.widget.FrameLayout";
			List <WebElement> ListByXpath = driver.findElementsByXPath(xpath);
			return ListByXpath.size();   
		}
		
		
		public void testLogTitle(String s){
			System.out.println("===================START================");
			System.out.println("Test: " + s);
			System.out.println("========================================");
		}


		@AfterClass
		public void teardown() throws InterruptedException{
			System.out.println("Sleeping for 5 seconds then teardown and quit");
			//System.out.println("Delete all programs for signed in account");
			//Sign in
			//deleteAllPrograms();
			//Sign out
			Thread.sleep(5000);
			driver.quit();
		}

}

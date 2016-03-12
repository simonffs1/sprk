package start;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.asserts.SoftAssert;
import com.gargoylesoftware.htmlunit.html.Keyboard;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.internal.runners.statements.Fail;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
//import junit.framework.Assert;


@Listeners({ ScreenshotUtility.class })
public class Testscript extends androidInterface{
	
	//Java client 3.1
	//private AndroidDriver<WebElement> driver;
	
	//public
	//AndroidDriver driver;
	AppiumDriver driverWeb;
	//WebDriverWait wait;
	TouchAction touch;
	WebDriverWait waitShort;
	WebDriverWait waitLong;
	Boolean uploadSuccess;
	Boolean signedIn;
	FirefoxDriver web;
	private SoftAssert softAssert = new SoftAssert();
	
	//device screen
	int screenHeight;
	int screenWidth;
	
	@SuppressWarnings("rawtypes")

	@BeforeClass
	@Parameters("port")
	//Setup
	public void setup(String port) throws MalformedURLException, InterruptedException {
		
		File appDir = new File("src");
		File app = new File(appDir, "ll-1.3.1.132-staging.apk");
		
		//appium specific configuration
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
		cap.setCapability(MobileCapabilityType.DEVICE_NAME,"Android device");
		cap.setCapability(MobileCapabilityType.APP,app.getAbsolutePath());
		
		//create objects
		driver = new AndroidDriver(new URL("http://127.0.0.1:"+port+"/wd/hub"),cap);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver,60);
		waitShort = new WebDriverWait(driver,10);
		waitLong = new WebDriverWait(driver,240);
		touch = new TouchAction(driver);
		web = new FirefoxDriver();
		web.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		uploadSuccess = true;
		signedIn = false;
		
		screenHeight = driver.manage().window().getSize().getHeight();
		screenWidth = driver.manage().window().getSize().getWidth();
		
		//remove this for bluetooth dialog at splash screen
		
		
	}
	
	@BeforeMethod
	//Verify screen is build before each case
	public void startPoint() throws Exception{
		
		//Check if app crashed
		try{
			driver.findElement(By.id("android:id/message"));
			driver.closeApp();
			driver.launchApp();
		}
		catch(Exception crash){
			System.out.println("Crash not detected");
		}
		
		//back out of edit screen 
		if(uploadSuccess==false){
			System.out.println("Uploading failed, backing out to Build");
			driver.pressKeyCode(AndroidKeyCode.BACK);
			clickOk(); //discard change confirm
			//press back button if in explore
			//if(driver.findElementById("com.sphero.sprk:id/toolbar_title").getText().equals("Explore")){
			//	driver.pressKeyCode(AndroidKeyCode.BACK);
			//}
		}
		
		uploadSuccess = true; //reset back to original state
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
		
		checkToolBarTitle("Build");
		dismissHint();
		dismissNewsletter(false);
		System.out.println("Starting on Build screen");
	}
	
	@Test
	public void sortByFilter(){
		clickMenu();
		clickMenuItem("Explore");
		driver.findElement(By.id("com.sphero.sprk:id/explore_filter_toolbar_item")).click();
		driver.findElementByXPath("//android.widget.LinearLayout[@index='0']").click();
	}
	
	public void enablebluetoothDialog(){
		testLogTitle("Bluetooth dialog enable ");
		try{
			//wait for bluetooth dialog
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/title")));
			driver.findElementByName("Bluetooth Not Enabled");
			//enable bluetooth
			driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click();
		}
		catch(Exception e){
			//bluetooth dialog did not appear
			System.out.println("Bluetooth dialog did not appear");
		}
	}
	
	
	@Test 
	public void connectRobot() throws Exception{
		testLogTitle("Connect BB-8");
		
		WebDriverWait waitFirmware = new WebDriverWait(driver,120);
		clickMenu();
		try{
			driver.findElementById("com.sphero.sprk:id/header");
			System.out.println("Robot already connected");
		}
		catch(Exception e){
		System.out.println("Robot not connected");
		//Click Connect a Robot
		clickConnectRobot();
		//Click Sphero
		try{
			chooseSphero();
		}
		catch(Exception f){
			Assert.fail("Missing elements in Connect Sphero");
		}
		clickClose();
		//Click Ollie
		
		try{
			chooseOllie();
		}
		catch(Exception g){
			Assert.fail("Missing elements in Connect Ollie");
		}
		clickClose();
		//Click BB-8
		try{
			chooseBB8();
		}
		catch(Exception h){
			Assert.fail("Missing elements in Connect BB-8");
		}
		
		waitFirmware.until(ExpectedConditions.invisibilityOfElementWithText(By.id("com.sphero.sprk:id/dialog_title"), "Connect BB-8"));
		clickMenu();
		
		driver.findElementById("com.sphero.sprk:id/header");
		System.out.println("Robot is connected");
		//Close pocket nav
		driver.pressKeyCode(AndroidKeyCode.BACK);
		}
	}
	
	@Test(dependsOnMethods={"connectRobot"})
	public void renameRobot(){
		testLogTitle("Rename Robot");
		
		clickMenu();
		String originalName = driver.findElementById("com.sphero.sprk:id/robot_name").getText();
		driver.findElementById("com.sphero.sprk:id/edit_robot_name").click();
		driver.findElementById("com.sphero.sprk:id/edit_text").clear();
		driver.findElementById("com.sphero.sprk:id/edit_text").sendKeys("Auto!");
		driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click();
		String newName = driver.findElementById("com.sphero.sprk:id/robot_name").getText();
		Assert.assertNotEquals(newName, originalName);
		System.out.println("Robot name changed");
		driver.pressKeyCode(AndroidKeyCode.BACK);
	}
	
	@Test //(dependsOnMethods={"connectRobot"},groups="connection")
	public void firmwareUpdate() throws Exception{
		testLogTitle("Firmware update");
		
		WebDriverWait waitFirmware = new WebDriverWait(driver,120);
		//Go to Settings
		driver.findElementByXPath("//*[@class='android.widget.ImageButton' and @index='0']").click();
		driver.findElementById("com.sphero.sprk:id/settings").click();
		String screen = driver.findElementById("com.sphero.sprk:id/toolbar_title").getText();;
		Assert.assertEquals(screen,"Settings");
		//Verified on Settings screen
		driver.findElementById("com.sphero.sprk:id/firmware_update_button").click();
		driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click();
		
		System.out.println("Settings found, initiating firmware update");
		
		while(!driver.findElementById("com.sphero.sprk:id/toolbar_title").getText().equals("Build")){
			Thread.sleep(500);
			driver.pressKeyCode(AndroidKeyCode.BACK);
		}
		
		System.out.println("Firmware complete");
		/*		Thread.sleep(50000); //wait 40 seconds
		
		waitFirmware.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
		screen = driver.findElementById("com.sphero.sprk:id/toolbar_title").getText();;
		Assert.assertEquals(screen,"Settings");
		System.out.println("Returned back to settings, firmware update complete");
		Thread.sleep(2000);
		driver.pressKeyCode(AndroidKeyCode.BACK); //return to build*/
		
		
	}
	
	@Test (dependsOnMethods={"firmwareUpdate"})
	public void disconnectRobot() throws Exception{
		testLogTitle("Disconnect BB-8");
		
		clickMenu();

		try{
			//Click Connect a Robot
			driver.findElementById("com.sphero.sprk:id/header").click();
			System.out.println("Robot is connected");
			//Disconnect by tapping Sphero
			chooseSphero();
			driver.findElementById("com.sphero.sprk:id/dialog_close").click();
			driver.findElementById("com.sphero.sprk:id/dialog_close").click();
			//Open pocketnav
			Thread.sleep(5000);
			clickMenu();
			driver.findElementById("com.sphero.sprk:id/header_not_connected");
			System.out.println("Robot is disconnected");
		}
		catch(Exception e){
			System.out.println("Robot already disconnected");
		}
		//Close pocketnav
		driver.pressKeyCode(AndroidKeyCode.BACK);
	}
	
	@DataProvider(name = "signInInfo")
    public Object[][] dataProviderMethod1() {
        return new Object[][] { 
            {"s4","1","valid",false,false}, 
            //{"ffsqat@gmail.com","1asdf","invalid",false,true}, 
            //{"307201","307201","valid",true,true}, 
            //{"307201","307201342","invalid",true,true}
            };
    }
    
    @Test(dataProvider = "signInInfo")
    public void signIn(String username,String password, String type, Boolean Clever, Boolean signOut){
        testLogTitle("Sign in");
        System.out.println("Signing in with username:" + username +"| password:" + password);
        System.out.println("Expected result:" + type);
        if(Clever==true){
            System.out.println("Clever sign in");
        }
        
        //check type before running
        if("valid"!= type.intern() && "invalid"!=type.intern()){
            Assert.fail("Wrong type was chosen, test case not run");
        }
        //Menu button
        clickMenu();
        //Sign in button
        clickPocketNavSignIn();
        clickSignIn(false);
        //Regular sign in
        if(Clever==false){
        	driver.findElementById("com.sphero.sprk:id/username_et").click();
            driver.findElementById("com.sphero.sprk:id/username_et").sendKeys(username);
            driver.hideKeyboard();
            driver.findElementById("com.sphero.sprk:id/password_et").click();
            driver.findElementById("com.sphero.sprk:id/password_et").sendKeys(password);
            driver.hideKeyboard();
            clickSignIn(true);
        }
        //Clever sign in
        else{
            driver.findElementById("com.sphero.sprk:id/clever_sign_in_button").click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("android.widget.EditText")));
            List <WebElement> fields = driver.findElementsByClassName("android.widget.EditText");
            for(WebElement i : fields){
                if(i.getAttribute("name").equals("Username")){
                    i.sendKeys(username);
                }
                if(i.getAttribute("name").equals("Password")){
                    i.sendKeys(password);
                }
            }
            driver.hideKeyboard();
            driver.findElementByClassName("android.widget.Button").click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("com.sphero.sprk:id/nav_close")));
        }
        //Expected conditions for valid sign in
        if(type.toLowerCase().equals("valid")){
            checkToolBarTitle("Build");
            dismissHint();
            signedIn=true;
        }
        else if(type.toLowerCase().equals("invalid") && Clever==false){
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("android:id/progress")));
            clickOk(); //close invalid credentials dialog
            clickCloseSignIn(); //close sign in
            clickCloseSignIn(); //close get started

        }
        else if(type.toLowerCase().equals("invalid") && Clever==true){
            clickCloseSignIn(); //close clever
            clickCloseSignIn(); //close sign in
            clickCloseSignIn(); //close get started
        }
        clickMenu();
        //check pocketnav changes
        if(type.equals("valid")){
            driver.findElementById("com.sphero.sprk:id/profile_header_signed_in");
            System.out.println("Signed in confirmed");
        }
        else{
            driver.findElementById("com.sphero.sprk:id/profile_header_not_signed_in");
            System.out.println("Not signed in confirmed");
        }
        //close pocketnav
        driver.pressKeyCode(AndroidKeyCode.BACK);
        
        if(signOut==true){
            signOutNoDelete();
        }
        signedIn = true;
    }
	
	@Test
	public void signOut(){
		
		testLogTitle("Sign Out (Delete all programs)");
		
		//Delete all programs before signing out
		deleteAllPrograms();
		
		clickMenu();	
		clickProfile();
		clickSignOut();
		clickOk();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("android:id/progress")));
		dismissHint();
		clickMenu();
		driver.findElementById("com.sphero.sprk:id/profile_header_not_signed_in");
		System.out.println("Signed out confirmed");
		signedIn=false;
		driver.pressKeyCode(AndroidKeyCode.BACK);
	}
	
	@Test
	public void signOutNoDelete(){
		
		testLogTitle("Sign Out");
		
		clickMenu();
		try{
			clickProfile();
			clickSignOut();
			clickOk();
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("android:id/progress")));
			dismissHint();
			clickMenu();
			driver.findElementById("com.sphero.sprk:id/profile_header_not_signed_in");
			System.out.println("Signed out confirmed");
			signedIn=false;
			driver.pressKeyCode(AndroidKeyCode.BACK);
		}
		catch(Exception e){
			System.out.println("Already signed out");
			driver.pressKeyCode(AndroidKeyCode.BACK);
		}
	}
	

	@Test //open a sample program and copy
	public void copySampleProgram() throws Exception{
		testLogTitle("View and Copy a Sample Program");
		int numOfProg = countProgram();
		clickSampleProgramsTab();
		clickProgram();
		clickCopy();
		clickOk(); //confirm
		clickOk(); //ok
		checkToolBarTitle("Build");
		clickMyProgramsTab();
		int numOfProgPost = countProgram();
		Assert.assertEquals(numOfProgPost, numOfProg+1);
		System.out.println("A copy of the sample program has been made");
	}
	
	@Test //open a sample program and copy
	public void copySampleProgramRepeatedly() throws Exception{
		testLogTitle("View and Copy a Sample Program");
		int numOfProg = 0;
		
		clickSampleProgramsTab();
		//driver.findElementByXPath("//*[@class='android.support.v7.app.ActionBar$Tab' and @index='1']").click();
		//driver.findElementsByXPath("//android.support.v7.app.ActionBar$Tab[@index='1']");
		while(numOfProg<=500){
			clickProgram();
			clickCopy();
			clickOk(); //confirm
			clickOk(); //ok
			System.out.println("Program # " + numOfProg +"has been made");
			numOfProg++;
		}
	}
	
	@Test
	public void viewSampleProgram() throws Exception{
		testLogTitle("View a Sample Program");
		int numOfProg = countProgram();
		//click sample programs tab
		clickSampleProgramsTab();
		//click the first sample program
		clickProgram();
		//click view
		clickView();
		leaveCanvas();
		checkToolBarTitle("Build");
		clickMyProgramsTab();
		int numOfProgPost = countProgram();
		Assert.assertEquals(numOfProgPost, numOfProg);
		System.out.println("No copy is made viewing a program");
	}
	
	
	
	@Test
	public void createProgram(){
		testLogTitle("Create a program");
		int initialprograms = countProgram();
		System.out.println(initialprograms);
		clickAddNewProgram();
		sendKeys("Program created by automation!");
		//Tap Save
		clickOk();
		leaveCanvas();
		checkToolBarTitle("Build");
		//Check if program count has increased by 1
		int currentprograms = countProgram();
		System.out.println(currentprograms);
		Assert.assertEquals(currentprograms,initialprograms+1);
		System.out.println("Program created successfully");
		if(signedIn==true){
			driver.findElement(By.id("com.sphero.sprk:id/uploading_spinner"));
			System.out.println("Sync indicator found");
		}
	}
	
	@Test 
	public void deleteMyProgramSignIn(){
		testLogTitle("Delete Programs while signed in");
		int numOfProg = countProgram();
		driver.findElementById("com.sphero.sprk:id/overflow_menu").click();
		//Check Edit title
		String fragmentTitle = driver.findElement(By.id("com.sphero.sprk:id/dialog_title")).getText();
		Assert.assertEquals(fragmentTitle, "Edit");
		driver.findElementById("com.sphero.sprk:id/delete_button").click();
		clickOk();
		checkToolBarTitle("Build");
		Assert.assertEquals(countProgram(), numOfProg-1);
		System.out.println("Program has been deleted");
		//*check name
	}
	
	@Test
	public void deleteRenameProgramSignedOut(){
		testLogTitle("Delete Programs while signed in");
		int initialprograms = countProgram();
		System.out.println(initialprograms);
		driver.findElementById("com.sphero.sprk:id/overflow_menu").click();
		//Tap Rename
		driver.findElementByXPath("//*[@class='android.widget.LinearLayout' and @index='0']").click();
		driver.findElementById("com.sphero.sprk:id/edit_text").clear();
		sendKeys("Program renamed by automation!");
		clickOk();
		driver.findElementById("com.sphero.sprk:id/overflow_menu").click();
		//Tap Delete and confirm
		driver.findElementByXPath("//*[@class='android.widget.LinearLayout' and @index='1']").click();
		clickOk();
		System.out.println(countProgram());
		Assert.assertEquals(countProgram(),initialprograms-1);
		System.out.println("Program deleted successfully");
	}
	
	//@Test //(dependsOnMethods={'signIn'}
	public void deleteAllPrograms(){
		while (countProgram()>1){
			deleteMyProgramSignIn();
		}
		System.out.println("All programs deleted");
	}
	
	@Test 
	public void deleteAll(){
		testLogTitle("Delete Programs while signed in");
		int programCount = 0;
		while(countProgram()>1){
		driver.findElementById("com.sphero.sprk:id/overflow_menu").click();
		String fragmentTitle = driver.findElement(By.id("com.sphero.sprk:id/dialog_title")).getText();
		Assert.assertEquals(fragmentTitle, "Edit");
		driver.findElementById("com.sphero.sprk:id/delete_button").click();
		clickOk();
		checkToolBarTitle("Build");
		programCount++;
		//Assert.assertEquals(countProgram(), numOfProg-1);
		}
		System.out.println("Deleted all "+programCount+" Programs");
	}
	
	@Test 
	public void deleteAllProgramsSignedOut() throws Exception{
		while (countProgram()>1){
			driver.findElementById("com.sphero.sprk:id/overflow_menu").click();
			driver.findElementByXPath("//*[@class='android.widget.LinearLayout' and @index='1']").click();
			driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click();
			Thread.sleep(500);
			System.out.println(countProgram());
		}
		System.out.println("All programs deleted");
	}
	
	
	@Test
	public void editMyProgram() throws Exception{
		
		
		testLogTitle("Edit My Programs while signed in");
		
		
		
		driver.findElementById("com.sphero.sprk:id/overflow_menu").click();
		//Check Edit title
		String fragmentTitle = driver.findElement(By.id("com.sphero.sprk:id/dialog_title")).getText();
		Assert.assertEquals(fragmentTitle, "Edit");
		uploadSuccess=false;
		//Click robots
		driver.findElementById("com.sphero.sprk:id/choose_sphero").click();
		driver.findElementById("com.sphero.sprk:id/choose_ollie").click();
		driver.findElementById("com.sphero.sprk:id/choose_bb8").click();
		//Clear fields and type
		driver.findElementById("com.sphero.sprk:id/program_title").clear();
		driver.findElementById("com.sphero.sprk:id/program_title").sendKeys("Edited the program title");
		driver.pressKeyCode(AndroidKeyCode.BACK);
		driver.findElementById("com.sphero.sprk:id/program_description").clear();
		driver.findElementById("com.sphero.sprk:id/program_description").sendKeys("Edited the program description");
		driver.pressKeyCode(AndroidKeyCode.BACK);
		//Click switch and help icon
		driver.findElementById("com.sphero.sprk:id/program_public_switch").click(); //*Keep track of switch status later
		if(driver.findElementById("com.sphero.sprk:id/program_public_switch").getText().equals("ON")){
			String status = driver.findElementById("com.sphero.sprk:id/status_text").getText();
			Assert.assertEquals(status, "In Review");
		}
		driver.findElementById("com.sphero.sprk:id/public_program_help").click();
		clickOk();
		driver.findElementById("com.sphero.sprk:id/attach_media_text").click();
		//Image button
		driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click();
		//Choose picture on Nexus (may vary between devices) *use try later for various file viewers
		try{
			waitShort.until(ExpectedConditions.presenceOfElementLocated(By.id("com.android.documentsui:id/icon_thumb")));
			driver.findElementById("com.android.documentsui:id/icon_thumb").click();
		}
		catch(Exception e){
			System.out.println("Device is not using stock android document UI");
			touch.tap((int)(screenWidth*0.50), (int)(screenHeight/4)).perform();
			Thread.sleep(1500);
			touch.tap((int)(screenWidth*0.66),screenHeight/4).perform();
		}
		
		driver.findElementById("com.sphero.sprk:id/dialog_action").click();
		//if in review tap continue
		try{
			clickOk();
		}
		catch(Exception e){
			System.out.println("Not previously in review");
		}
		

		//wait.until(ExpectedConditions.presenceOfElementLocated(By.id("android:id/content")));
		//waitLong.until(ExpectedConditions.invisibilityOfElementLocated(By.id("com.sphero.sprk:id/content")));
		//System.out.println("Progress bar not visible");
		
		
		waitLong.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
		//check if able to go back to build, if not upload failed
		if(driver.findElementsById("com.sphero.sprk:id/dialog_title").size()==0){
			uploadSuccess=true;
			System.out.println("Upload success");
		}
		else{
			Assert.fail("Upload failed");
			//softAssert.fail("Upload failed");
			//clickClose();
			//clickOk(); //close discard change dialog
			System.out.println("Upload failed");
		}

		//touch.press(10,(driver.manage().window().getSize().getHeight())/2);
		//*Add discard dialog test case here later
		//Tap the left most middle area of the screen (TABLET ONLY)
	}
	
	@Test 
	public void copyMyProgram(){
		testLogTitle("Copy My Programs while signed in");
		int numOfProg = countProgram();
		driver.findElementById("com.sphero.sprk:id/overflow_menu").click();
		//Check Edit title
		String fragmentTitle = driver.findElement(By.id("com.sphero.sprk:id/dialog_title")).getText();
		Assert.assertEquals(fragmentTitle, "Edit");
		driver.findElementById("com.sphero.sprk:id/copy_button").click();
		clickOk(); //confirm
		clickOk(); //ok
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("com.sphero.sprk:id/dialog_title")));
		//driver.findElementByName("Programming Tutorial");
		Assert.assertEquals(countProgram(), numOfProg+1);
		System.out.println("Program has been copied");
		//*check name 
	}
	

	
	//Unit test for counting programs
	public int countProgram(){
		String xpath = "//android.support.v7.widget.RecyclerView/descendant::android.widget.FrameLayout";
		List <WebElement> ListByXpath = driver.findElements(By.xpath(xpath));
		return ListByXpath.size();   
	}
	
	//@Test
	public void countPrograms(){
		String xpath = "//android.support.v7.widget.RecyclerView/descendant::android.widget.FrameLayout";
		List <WebElement> ListByXpath = driver.findElements(By.xpath(xpath));
		System.out.println(ListByXpath.size());
	}
	
	
	public void scrollVertical(String s,int duration){

		int startx = (int) (screenWidth * 0.5); 
		int starty = 0;
		int endy = 0;
		
		if(s.equals("up")){
			starty = (int) (screenHeight * 0.30); //upper half of screen
			endy = (int)(screenHeight-1);
			System.out.println("Scrolling up");
			
		}
		else if (s.equals("down")){
			starty = (int) (screenHeight * 0.75); //lower half of screen
			endy = 5;
			System.out.println("Scrolling down");
			
		}
		//System.out.println(startx);
		//System.out.println(starty);
		driver.swipe(startx, starty, startx, endy, duration);
	}
		
	@Test
	public void scrollToBottomToTop() throws Exception{
		
		testLogTitle("Scroll to bottom and top of my programs");
		
		int scrollLimit ;
		
		
		if(screenWidth>screenHeight){
			scrollLimit = 5;
		}
		else{
			scrollLimit = 10;
		}
		
		signIn("s2","yyyyyy","valid",false,false);
		
		checkToolBarTitle("Build");

		dismissHint();
		
		int posFirst;
		int posNext;
		
		int scrollCount = 0;
		while(true){
			posFirst = driver.findElementByXPath("//android.support.v7.widget.RecyclerView/descendant::android.widget.FrameLayout[@index='0']").getLocation().y;
			scrollVertical("down",200);
			posNext = driver.findElementByXPath("//android.support.v7.widget.RecyclerView/descendant::android.widget.FrameLayout[@index='0']").getLocation().y;
			System.out.println(posFirst);
			System.out.println(posNext);
			scrollCount++;
			try{
				Boolean footer = driver.findElementsById("com.sphero.sprk:id/progress_bar").size()>0;
				if (footer == true){
					System.out.println("Spinner found, waiting until spinner is gone before scrolling again");
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("com.sphero.sprk:id/progress_bar")));
					//Thread.sleep(5000);
					footer = false;
					scrollCount = 0;
				}
				if (footer == false && scrollCount>scrollLimit){
					System.out.println("End of my programs");
					break;
				}
			}
			catch(Exception e){
				System.out.println("Footer refresh not found");
			}
		}
		
		//Assert that programming tutorial is found
		driver.findElementByName("Programming Tutorial");
		
		while (true){
			scrollVertical("up",200);
			try{
			Boolean header = driver.findElementsByXPath("//android.view.View[@resource-id='com.sphero.sprk:id/swipe_refresh']/android.widget.ImageView").size() > 0 ;
				if (header == true){
					System.out.println("Found refresh icon");
					break;
				}
			}
			catch(Exception e){
				System.out.println("Something went wrong scrolling up");
			}
		}
		//wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//android.view.View[@resource-id='com.sphero.sprk:id/swipe_refresh']/android.widget.ImageView")));
		//System.out.println("Refresh icon not visible, at top");
		
		signOutNoDelete();
		checkToolBarTitle("Build");
	}
@Test(invocationCount=20)
public void scrollToBottom() throws Exception{
		
		testLogTitle("Scroll to bottom and top of my programs");
		
		int scrollLimit ;
		
		if(screenWidth>screenHeight){
			scrollLimit = 5;
		}
		else{
			scrollLimit = 10;
		}
		
		signIn("i500","yyyyyy","valid",false,false);
		
		checkToolBarTitle("Build");

		dismissHint();
		
		
		int scrollCount = 0;
		while(true){
			scrollVertical("down",200);
			scrollCount++;
			try{
				Boolean footer = driver.findElementsById("com.sphero.sprk:id/progress_bar").size()>0;
				if (footer == true){
					System.out.println("Spinner found, waiting until spinner is gone before scrolling again");
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("com.sphero.sprk:id/progress_bar")));
					//Thread.sleep(5000);
					footer = false;
					scrollCount = 0;
				}
				if (footer == false && scrollCount>scrollLimit){
					System.out.println("End of my programs");
					break;
				}
			}
			catch(Exception e){
				System.out.println("Footer refresh not found");
			}
		}
		
		//Assert that programming tutorial is found
		driver.findElementByName("Programming Tutorial");
		
		signOutNoDelete();
		checkToolBarTitle("Build");
	}
	
	
	@Test
	public void gesturePocketNav(){
		testLogTitle("Pocket Nav open by gesture");
		int startx = 1; 
		int starty = screenHeight / 2; 
		int endx = (int) ((screenWidth * 0.7)); 
		driver.swipe(startx, starty, endx, starty, 500);
		driver.findElementById("com.sphero.sprk:id/settings");
		System.out.println("Found Settings, pocketNav Open");
		//Phone
		/*
		if(height>width){
			driver.tap(1, width-10, height/2, 100);
		}
		else{
			//Tablet
			driver.tap(1, size.width/2, size.height/2, 100);
		}
		*/
		driver.findElementById("com.sphero.sprk:id/floating_add_new_program_button").click();
		System.out.println("Tapped outside to dismiss");
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("com.sphero.sprk:id/settings")));
		System.out.println("Settings could not be found, pocketNav closed");
	}

	@Test
	public void pocketNav() throws Exception{
		testLogTitle("Pocket Nav click all items");
		//Click Sample programs tab then My Programs
		System.out.println("Go to Sample Programs tab and back to Build");
		clickSampleProgramsTab();
		clickMyProgramsTab();
		//Open Pocket Nav
		clickMenu();
		//Go to Profile
		try{
			clickPocketNavSignIn();
		}
		catch(Exception e){
			System.out.println("Already signed in");
		}
		System.out.println("View Profile title found");
		clickCloseSignIn();
		clickMenu();
		
		//Go to Connect robot
		try{
		driver.findElementById("com.sphero.sprk:id/header_not_connected").click();
		}
		catch(Exception e){
			driver.findElementById("com.sphero.sprk:id/header").click();
		}
		
		clickClose();
		goToActivity("explore");
		dismissHint();
		
		//click tabs
		clickMediaTab();
		clickTwitterTab();
		clickExploreTab();
	
		//Go to Drive
		goToActivity("drive");
		driver.pressKeyCode(AndroidKeyCode.BACK);
		Thread.sleep(2000);
		//Go to Settings
		clickMenu();
		driver.findElementById("com.sphero.sprk:id/settings").click();
		verifyToolbar("settings");
		//Go to Build
		clickMenu();
		clickMenuItem("Build");
		
		if(screenWidth>screenHeight){
			driver.findElementById("com.sphero.sprk:id/drive_toolbar_item").click();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/background")));
			System.out.println("Drive tool bar button opened drawer");
			clickMyProgramsTab();
			clickMenu();
			clickMenuItem("Build");
		}

		
	}
	
	@Test//(invocationCount=10)
	public void viewExploreProgram() throws Exception{
		
		testLogTitle("View Explore Programs");
		
		Boolean progress = false;
		
		int numOfProg = countProgram();
		
		clickMenu();
		clickMenuItem("Explore");
		dismissHint();
		
		clickProgram();
		//wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/dialog_title")));
		//handle progress indicator blocking tap
		try{
			driver.findElements(By.id("com.sphero.sprk:id/progress_bar"));
			System.out.println("Spinner was found");
			progress = driver.findElements(By.id("com.sphero.sprk:id/progress_bar")).size()>0;
		}
		catch(Exception preload){
			System.out.println("No spinner was found");
		}
		
		//handle progress indicator blocking tap
		/*
		try{
			Boolean progress = driver.findElements(By.id("com.sphero.sprk:id/progress_bar")).size()>0;
			if(progress == true){
				System.out.println("Waiting for progress spinner to disappear");
				while(driver.findElement(By.id("com.sphero.sprk:id/progress_bar")).isDisplayed()){
					Thread.sleep(2000);
				}
			}
		}
		catch(Exception e){
			System.out.println("No progress indicator was found");
		}
		*/
		
		if(driver.findElementsById("com.sphero.sprk:id/playable_media_overlay").size()==0){
			if(progress==true){
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("com.sphero.sprk:id/progress_bar")));
			}
			driver.findElementById("com.sphero.sprk:id/program_details_image").click();
			try{
				driver.findElementById("com.sphero.sprk:id/fullscreen_image");
				System.out.println("Full screen image opened");
				driver.pressKeyCode(AndroidKeyCode.BACK);
			}
			catch(Exception e){
				System.out.println("No user attached image");
			}
		}
		else{
			System.out.println("Video found");
			//implement view video
		}
		
		driver.findElementById("com.sphero.sprk:id/program_details_image");
		clickView();
		Thread.sleep(2000);
		if(driver.currentActivity().toLowerCase().contains("unity")){
			leaveCanvas();
			checkToolBarTitle("Explore");
			dismissHint();
			driver.pressKeyCode(AndroidKeyCode.BACK); // Return to Build
			checkToolBarTitle("Build");
			Assert.assertEquals(numOfProg, countProgram());
		}
		else{
			clickNo(); // close invalid lab file prompt
			clickClose(); //close program details
			driver.pressKeyCode(AndroidKeyCode.BACK); // Return to Build
			checkToolBarTitle("Build");
			Reporter.log("Viewed an invalid format lab file, did not enter canvas");
		}
	}
	
	@Test(dataProvider = "data-provider",invocationCount=10)
	public void viewExploreVideo(String section){
		testLogTitle("View video in Explore Programs");
		
		Boolean videoFound = false;
		clickMenu();
		clickMenuItem("Explore");
		dismissHint();
		if(section.toLowerCase().equals("media")){
			clickMediaTab();
		}
		
		while(videoFound==false){
			try{
				driver.findElementById("com.sphero.sprk:id/video_badge").click();
				if(section.toLowerCase().equals("media")){
					driver.findElementById("com.sphero.sprk:id/media_video_overlay").click(); //media
				}
				else{
					driver.findElementById("com.sphero.sprk:id/playable_media_overlay").click(); //program 
				}
				videoFound=true;
				System.out.println("Clicked video play icon");
				
				//Click youtube
				try{
					driver.findElementByName("YouTube").click();
				}
				catch(Exception e){
					System.out.println("Youtube icon doesn't exist");
				}
				//click once 
				try{
					driver.findElementById("android:id/button_once").click();
				}
				catch(Exception e){
					System.out.println("Just Once doesn't exist");
				}
				if(driver.currentActivity().toLowerCase().contains("youtube")){
					System.out.println("In youtube app");
					driver.pressKeyCode(AndroidKeyCode.BACK);
					break;
				}
				//click once 
				
			}
			catch(Exception e){
				System.out.println("Video badge not found");
			}
			scrollVertical("down",1500);
		}
		clickClose();
		clickMenu();
		clickMenuItem("Build");
	}
	
	@Test
	public void copyExploreProgram() throws Exception{
		
		testLogTitle("Copy Explore Programs");
		
		int numOfProg = countProgram();
		dismissHint();
		
		clickMenu();
		clickMenuItem("explore");
		clickProgram();
		clickClose();
		clickProgram();
		clickCopy();
		clickOk();//confirm name
		try{
			clickOk(); //ok
			checkToolBarTitle("Explore");
			
		}
		catch(Exception e){
			clickNo();
			driver.findElementById("com.sphero.sprk:id/dialog_close");
			System.out.println("Invalid lab file");
			clickClose();
			Reporter.log("Tried to copy invalid lab file");
		}
		driver.pressKeyCode(AndroidKeyCode.BACK); // Return to Build
		checkToolBarTitle("build");
		int numOfProgPost = countProgram();
		Assert.assertEquals(numOfProgPost, numOfProg+1);
		System.out.println("Copy of public program exists");
	}
	
	@Test
	public void viewMedia() throws Exception{
		
		testLogTitle("View Media");
		Boolean progress = false;
		
		clickMenu();
		clickMenuItem("Explore");
		dismissHint();
		//Click on Media
		clickMediaTab();
		//Compare program and title name
		//String programName = driver.findElementById("com.sphero.sprk:id/program_name").getText();
		driver.findElementById("com.sphero.sprk:id/program_name").click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/dialog_title")));
		Thread.sleep(2000);
		try{
			driver.findElements(By.id("com.sphero.sprk:id/progress_bar"));
			System.out.println("Spinner was found");
			progress = driver.findElements(By.id("com.sphero.sprk:id/progress_bar")).size()>0;
		}
		catch(Exception preload){
			System.out.println("No spinner was found");
		}
		
		//handle progress indicator blocking tap
		/*
		try{
			Boolean progress = driver.findElements(By.id("com.sphero.sprk:id/progress_bar")).size()>0;
			if(progress == true){
				System.out.println("Waiting for progress spinner to disappear");
				while(driver.findElement(By.id("com.sphero.sprk:id/progress_bar")).isDisplayed()){
					Thread.sleep(2000);
				}
			}
		}
		catch(Exception e){
			System.out.println("No progress indicator was found");
		}
		*/
		if(driver.findElements(By.id("com.sphero.sprk:id/media_video_overlay")).size()==0){
			if(progress==true){
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("com.sphero.sprk:id/progress_bar")));
			}
			if(progress==false){
				driver.findElementById("com.sphero.sprk:id/media_image").click();
				try{
					driver.findElementById("com.sphero.sprk:id/fullscreen_image");
				}
				catch(Exception e){
					driver.findElementById("com.sphero.sprk:id/media_image");
					driver.pressKeyCode(AndroidKeyCode.BACK);
					checkToolBarTitle("Explore");
					driver.pressKeyCode(AndroidKeyCode.BACK); // Return to Build
					checkToolBarTitle("Build");
					Assert.fail("Image did not go to full screen");
				}
				System.out.println("Full screen image opened");
				driver.pressKeyCode(AndroidKeyCode.BACK);
				driver.findElementById("com.sphero.sprk:id/media_image");
			}
		}
		
		/*
		
		int tapCount=0;
		//Click it repeatedly if spinner was seen
		while(progress==true && tapCount<20){
			try{
				driver.findElementById("com.sphero.sprk:id/media_image").click();
				tapCount++;
				driver.findElementById("com.sphero.sprk:id/fullscreen_image");
				System.out.println("Full screen image opened");
				break;
			}
			catch(Exception e){
				System.out.println("Could not open image full screen");
			}
		}
		*/
		
		
		driver.pressKeyCode(AndroidKeyCode.BACK);
		
		
		checkToolBarTitle("Explore");
		driver.pressKeyCode(AndroidKeyCode.BACK); // Return to Build
		checkToolBarTitle("Build");
	}
	
	@Test
	public void postMedia() throws Exception{
		
		testLogTitle("Post Media");
		
		uploadSuccess=false;
		
		clickMenu();
		clickMenuItem("explore");
		dismissHint();
		
		clickMediaTab();
		clickAddMedia();
		//Dismiss the page
		driver.findElementById("com.sphero.sprk:id/dialog_title").getText().contains("Add Media");
		clickClose();
		clickAddMedia();
		
		//Discard dialog
		driver.findElementById("com.sphero.sprk:id/dialog_title").getText().contains("Add Media");
		driver.findElementById("com.sphero.sprk:id/choose_sphero").click();
		clickClose();
		clickOk();
		clickAddMedia();
		driver.findElementById("com.sphero.sprk:id/dialog_title").getText().contains("Add Media");
		//Tap Publish Now
		driver.findElementById("com.sphero.sprk:id/publish_button").click();
		driver.findElementById("com.sphero.sprk:id/dialog_title").getText().contains("Add Media");
		driver.findElementById("com.sphero.sprk:id/media_description").sendKeys("Media description automated");
		driver.findElementById("com.sphero.sprk:id/media_title").sendKeys("Media title automated, selecting ollie");
		driver.hideKeyboard();
		driver.findElementById("com.sphero.sprk:id/choose_ollie").click();
		driver.findElementById("com.sphero.sprk:id/add_media_image").click();
		//Image button
		driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click();
		//Choose picture on Nexus (may vary between devices) *use try later for various file viewers
		try{
			waitShort.until(ExpectedConditions.presenceOfElementLocated(By.id("com.android.documentsui:id/icon_thumb")));
			driver.findElementById("com.android.documentsui:id/icon_thumb").click();
		}
		catch(Exception e){
			System.out.println("Device is not using stock android document UI");
			touch.tap((int)(screenWidth*0.50), (int)(screenHeight/4)).perform();
			Thread.sleep(1500);
			touch.tap((int)(screenWidth*0.66),screenHeight/4).perform();
		}
		
		driver.findElementById("com.sphero.sprk:id/publish_button").click();
		System.out.println("Clicked publish button");
		
		waitLong.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
		//check if able to go back to build, if not upload failed
		if(driver.findElementsById("com.sphero.sprk:id/dialog_title").size()==0){
			uploadSuccess=true;
			System.out.println("Upload success");
		}
		else{
			Assert.fail("Upload failed");
			System.out.println("Upload failed");
		}
	}
	
	@DataProvider(name = "data-provider")
    public Object[][] dataProviderMethod() {
        return new Object[][] { { "exploreprograms" }, { "media" } };
	}
	
	@Test
	//@Parameters("section")
	(dataProvider = "data-provider")
	public void scrollExplore(String section) throws Exception{
		
		if(section.toLowerCase().equals("media")){
			testLogTitle("Scroll Explore Media");
		}
		else{
			testLogTitle("Scroll Explore Programs");
		}
		
		int scrollLimit ;
		
		
		if(screenWidth>screenHeight){
			scrollLimit = 5;
		}
		else{
			scrollLimit = 10;
		}
		
		clickMenu();
		clickMenuItem("explore");
	
		dismissHint();
		
		if(section.toLowerCase().equals("media")){
			clickMediaTab();
		}
		
		int scrollCount = 0;
		while(true){
			scrollVertical("down",200);
			scrollCount++;
			try{
				Boolean footer = driver.findElementsById("com.sphero.sprk:id/progress_bar").size()>0;
				if (footer == true){
					System.out.println("Spinner found, waiting 5 seconds before scrolling again");
					Thread.sleep(5000);
					footer = false;
					scrollCount = 0;
				}
				if (footer == false && scrollCount>scrollLimit){
					System.out.println("End of my programs");
					break;
				}
			}
			catch(Exception e){
				System.out.println("Footer refresh not found");
			}
		}
		
		
		while (true){
			scrollVertical("up",200);
			try{
			Boolean header = driver.findElementsByXPath("//android.view.View[@resource-id='com.sphero.sprk:id/swipe_refresh']/android.widget.ImageView").size() > 0 ;
				if (header == true){
					System.out.println("Found refresh icon");
					break;
				}
			}
			catch(Exception e){
				System.out.println("Something went wrong scrolling up");
			}
		}
		
		clickMenu();
		clickMenuItem("build");
	}

	
	@Test
	@Parameters("date")
	public void scrollTwitter(String date){
		testLogTitle("Scroll through Twitter");

		clickMenu();
		clickMenuItem("Explore");
		clickTwitterTab();
		
		int scrollCount = 0;
		Boolean found = false;
		
		while(scrollCount<30){
			scrollVertical("down",400);
			scrollCount++;
			try{
				found = driver.findElementById("com.sphero.sprk:id/tw__tweet_timestamp").getText().toLowerCase().contains(date.toLowerCase());
				if(found==true){
					System.out.println("Twttier post date found");
					break;
				}
			}
			catch(Exception e){
				driver.pressKeyCode(AndroidKeyCode.BACK);
				System.out.println("Date not found");
			}
		}

		//Go to Build
		clickMenu();
		clickMenuItem("build");
		if(found==false){
			Assert.fail("Twitter post date not found");
		}
	}
	
	//@Test(dependsOnMethods={"signIn"})
	public void pullToRefresh(){
		testLogTitle("Refresh gesture");
		/*
		Dimension size = driver.manage().window().getSize(); 
		int startx = (int) (size.width * 0.5); 
		int starty = (int)(size.height * 0.5); 
		int endy = (int) (size.height-1); 
		driver.swipe(startx, starty, startx, endy, 3000);
		driver.swipe(startx, starty, startx, endy, 3000);
		WebElement refreshIcon = driver.findElementByXPath("//android.view.View[@resource-id='com.sphero.sprk:id/swipe_refresh']/android.widget.ImageView");
		System.out.println("Found refresh icon");
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//android.view.View[@resource-id='com.sphero.sprk:id/swipe_refresh']/android.widget.ImageView")));
		System.out.println("Refresh icon gone");
		*/
		
		scrollVertical("up",500);
		try{
		Boolean header = driver.findElementsByXPath("//android.view.View[@resource-id='com.sphero.sprk:id/swipe_refresh']/android.widget.ImageView").size() > 0 ;
		if (header == true){
			System.out.println("Found refresh icon");
			}
		}
		catch(Exception e){
				System.out.println("Something went wrong scrolling up");
			}
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//android.view.View[@resource-id='com.sphero.sprk:id/swipe_refresh']/android.widget.ImageView")));
		System.out.println("Refresh icon not visible, at top");
	}


	//Check unauthenticated user gate prompt visibility
	@Test
	public void userGates(){
		
		testLogTitle("User gates from signed out");
		
		clickMenu();
		clickMenuItem("Explore");
		//goToActivity("explore");
		
		//wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
		dismissHint();
		//Click on the first program
		clickProgram();
		//Wait for the drawer to appear
		clickView();
		driver.findElementById("com.sphero.sprk:id/content");
		System.out.println("View User Gate Present");
		clickNo();
		System.out.println("Clicked Maybe Later");
		//Click on Make a Copy
		clickCopy();
		driver.findElementById("com.sphero.sprk:id/content");
		System.out.println("Copy User Gate Present");
		clickNo();
		System.out.println("Clicked Maybe Later");
		
		clickClose();
		clickMediaTab();
		clickAddMedia();
		driver.findElementById("com.sphero.sprk:id/content");
		System.out.println("Media User Gate Present");
		clickNo();
		clickMenu();
		clickMenuItem("build");
	}
	
	
	@Test
	public void viewProgramStatus(){
		
		WebElement publicProgram = driver.findElement(By.id("com.sphero.sprk:id/program_image"));
		
		
		List<WebElement> programs = driver.findElements(By.xpath("//android.support.v7.widget.RecyclerView/descendant::android.widget.FrameLayout"));
		for(WebElement temp : programs){
			System.out.println(temp.getText());
		}
		
		checkPublicProgramOrder();
		
		//overflow button for public
		driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='0']//android.widget.ImageButton")).click();
		Assert.assertEquals(driver.findElementById("com.sphero.sprk:id/status_text").getText(), "Public"); //check status
		Assert.assertEquals(driver.findElement(By.id("com.sphero.sprk:id/program_public_switch")).getAttribute("checked"), "true");//check switch status
		driver.findElement(By.id("com.sphero.sprk:id/share_button")); //check visibility of share btuton
		clickSphero();
		clickSave();
		clickNo();
		clickSphero();
		clickClose();
		
		driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='1']//android.widget.ImageButton")).click();
		Assert.assertEquals(driver.findElementById("com.sphero.sprk:id/status_text").getText(), "Rejected");
		Assert.assertEquals(driver.findElement(By.id("com.sphero.sprk:id/program_public_switch")).getAttribute("checked"), "false");
		clickClose();
		
		driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='2']//android.widget.ImageButton")).click();
		Assert.assertEquals(driver.findElementById("com.sphero.sprk:id/status_text").getText(), "In Review");
		Assert.assertEquals(driver.findElement(By.id("com.sphero.sprk:id/program_public_switch")).getAttribute("checked"), "true");
		clickSphero();
		clickSave();
		clickNo();
		clickSphero();//undo
		clickClose();

	}
	@Test
	public void publicProgramGate(){
		
		checkPublicProgramOrder();
		//overflow button for public
		driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='0']")).click();
		clickOk(); //go to canvas
		leaveCanvas();
		checkToolBarTitle("Build");
		driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='0']")).click();
		clickNo(); //cancel
		
		driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='1']")).click();
		leaveCanvas();
		checkToolBarTitle("Build");
		driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='2']")).click();
		clickOk(); //continue
		leaveCanvas();
		checkToolBarTitle("Build");
		driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='2']")).click();
		clickNo(); //cancel	
		checkPublicProgramOrder();
	}
	
	public void checkPublicProgramOrder(){
		String program0 = driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='0']//android.widget.TextView[@index='0']")).getText();
		String program1 = driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='1']//android.widget.TextView[@index='0']")).getText();
		String program2 = driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='2']//android.widget.TextView[@index='0']")).getText();
		String program3 = driver.findElement(By.xpath("//android.support.v7.widget.RecyclerView/android.widget.FrameLayout[@index='3']//android.widget.TextView[@index='0']")).getText();
		
		//0 public, 1 rejected, pos 2 : in review , pos3: video
		Assert.assertEquals(program0, "public");
		Assert.assertEquals(program1, "rejected");
		Assert.assertEquals(program2, "inreview");
		Assert.assertEquals(program3, "video");
	}
	
	//Where the menu button is visible
	public void goToActivity(String s){
		System.out.println("Going to " + s);
		clickMenu();
		List<WebElement> allTextView = driver.findElements(By.id("com.sphero.sprk:id/menu_item_text")); 

		//find the string in the list of webelements and click
		for (WebElement temp : allTextView) {
			System.out.println(temp.getText());
			if(temp.getText().toLowerCase().equals(s.toLowerCase())){
				temp.click();
				break;
			}	
		}
		//Use a different method to check screen for drive for Tablets since it's a drawer
		if(s.toLowerCase().equals("drive") && screenWidth>screenHeight){
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/background")));
			System.out.println("Tablet with Drive drawer appeared");
		}
		else{
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
			//Verify screen is Explore
			String screen = driver.findElementById("com.sphero.sprk:id/toolbar_title").getText();
			Assert.assertEquals(screen.toLowerCase(),s.toLowerCase());
			System.out.println(s + " screen was found");
		}
	}
	
	public void verifyToolbar(String s){
		String screen = driver.findElementById("com.sphero.sprk:id/toolbar_title").getText().toLowerCase();
		Assert.assertEquals(screen,s.toLowerCase());
		System.out.println(s + " title found");
	}
	
	public void testLogTitle(String s){
		System.out.println("===================START================");
		System.out.println("Test: " + s);
		System.out.println("========================================");
	}
	
	@Test
	public void signUp12YearsUnder(){
		clickMenu();
		clickPocketNavSignIn();
		driver.findElementById("com.sphero.sprk:id/sign_up_button").click();
		driver.findElementById("com.sphero.sprk:id/select_student").click();
		driver.findElementById("com.sphero.sprk:id/continue_button").click();
		driver.findElementById("com.sphero.sprk:id/initials_et").sendKeys("abc");
		//driver.hideKeyboard();
		driver.findElementById("com.sphero.sprk:id/guardian_email_et").sendKeys("asd23fk31efvm@sadlkfjkasdfjesac.com");
		driver.hideKeyboard();
		driver.findElementById("com.sphero.sprk:id/request_access_button").click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/continue_button")));
		driver.findElementById("com.sphero.sprk:id/continue_button").click();
		checkToolBarTitle("Build");
	}
	
	@Parameters({"student_username","student_email","password"})
	@Test
	public void signUp13Years(String student_username,String student_email,String password) throws Exception{
		clickMenu();
		clickPocketNavSignIn();
		driver.findElementById("com.sphero.sprk:id/sign_up_button").click();
		driver.findElementById("com.sphero.sprk:id/select_student").click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("android:id/pickers")));
		driver.findElementByXPath("//android.widget.NumberPicker[@index='2']/android.widget.EditText").click();
		driver.findElementByXPath("//android.widget.NumberPicker[@index='2']/android.widget.EditText").clear();
		driver.findElementByXPath("//android.widget.NumberPicker[@index='2']/android.widget.EditText").sendKeys("2000");
		
		
		driver.findElementByXPath("//android.widget.NumberPicker[@index='0']/android.widget.EditText").clear();
		driver.findElementByXPath("//android.widget.NumberPicker[@index='0']/android.widget.EditText").sendKeys("A");
		
		//driver.findElementByXPath("//android.widget.NumberPicker[@index='1']/android.widget.EditText").clear();
		//driver.findElementByXPath("//android.widget.NumberPicker[@index='1']/android.widget.EditText").sendKeys("11");
		
		
		driver.hideKeyboard();
		driver.findElementById("com.sphero.sprk:id/continue_button").click();
		
		driver.findElementById("com.sphero.sprk:id/class_name_helper").click();
		driver.findElementById("com.sphero.sprk:id/buttonDefaultNeutral").click();
		//driver.findElementById("com.sphero.sprk:id/class_name_et").clear();
		//driver.findElementById("com.sphero.sprk:id/class_name_et").sendKeys("classname");
		driver.findElementById("com.sphero.sprk:id/reenter_password_et").sendKeys(password);
		driver.findElementById("com.sphero.sprk:id/password_et").sendKeys(password);
		driver.findElementById("com.sphero.sprk:id/email_et").sendKeys(student_email);
		driver.findElementById("com.sphero.sprk:id/username_et").sendKeys(student_username);
		driver.pressKeyCode(AndroidKeyCode.BACK);
		Thread.sleep(2000); //sign up button to shift
		driver.findElementById("com.sphero.sprk:id/create_account_button").click();
		
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/verify_account_button")));
		Thread.sleep(10000); //wait for email to be sent
		Firefox();
		
		Thread.sleep(30000);
		driver.findElementById("com.sphero.sprk:id/verify_account_button").click();
		checkToolBarTitle("Build");
		dismissHint();
		signOut();
		//do automated gmail account verification
		
		//com.sphero.sprk:id/verify_account_button
		//com.sphero.sprk:id/do_this_later_button
	}
	@Parameters({"instructor_username","instructor_email","password"})
	@Test
	public void signUpInstructor(String instructor_username,String instructor_email,String password) throws Exception{
		clickMenu();
		clickPocketNavSignIn();
		driver.findElementById("com.sphero.sprk:id/sign_up_button").click();
		driver.findElementById("com.sphero.sprk:id/select_instructor").click();
		driver.findElementById("com.sphero.sprk:id/reenter_password_et").click();
		driver.findElementById("com.sphero.sprk:id/reenter_password_et").sendKeys(password);
		driver.findElementById("com.sphero.sprk:id/password_et").click();
		driver.findElementById("com.sphero.sprk:id/password_et").sendKeys(password);
		driver.findElementById("com.sphero.sprk:id/email_et").click();
		driver.findElementById("com.sphero.sprk:id/email_et").sendKeys(instructor_email);
		driver.findElementById("com.sphero.sprk:id/username_et").click();
		driver.findElementById("com.sphero.sprk:id/username_et").sendKeys(instructor_username);
		driver.pressKeyCode(AndroidKeyCode.BACK);
		Thread.sleep(2000); //sign up button to shift
		driver.findElementById("com.sphero.sprk:id/continue_signup_button").click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/verify_account_button")));
		Thread.sleep(10000); //wait for email to be sent
		Firefox();
		
		Thread.sleep(30000);
		driver.findElementById("com.sphero.sprk:id/verify_account_button").click();
		checkToolBarTitle("Build");
		dismissHint();
		signOut();
		
	
	}
	
	@Test
	public void Firefox(){
		WebDriverWait waitWeb = new WebDriverWait(web,30);
		web.get("https://www.gmail.com"); 
		
		// Store the current window handle
		String winHandleBefore = driver.getWindowHandle();
		
		web.findElementByXPath(".//*[@id='Email']").sendKeys("ffsqatsignup@gmail.com");
		web.findElementByXPath(".//*[@id='next']").click();
		web.findElementByXPath(".//*[@id='Passwd']").sendKeys("middlefinger");
		web.findElementByXPath(".//*[@id='signIn']").click();
		waitWeb.until(ExpectedConditions.urlContains("https://mail.google.com/mail/#inbox"));
		//web.findElementByXPath("//*[contains(text(), 'Activate your SPRK Lightning Lab Account!')]").click();
		
		//web.findElementByXPath(".//span[text()='Activate your SPRK Lightning Lab Account!']").click();
		web.findElementByXPath("//*[text()='Activate your SPRK Lightning Lab Account!']").click();
		web.findElementByXPath("//a[text()='Activate Account']").click();
		
	
		// Switch to new window opened
		for(String winHandle : web.getWindowHandles()){
		    web.switchTo().window(winHandle);
		}

		// Close the new window, if that window no more required
		web.close();

		// Switch back to original browser (first window)
		driver.switchTo().window(winHandleBefore);
		
		web.findElementByXPath("//*[aria-label='Delete']").click();
		/*
		while(web.findElementByXpath("//*[text()='Sphero']").size()>0){
			web.findElementByXpath("//*[text()='Sphero']").click();
			web.findElementByXPath("//*[aria-label='Delete']").click();
			
		
		}
		*/
	}
	
	



	@AfterClass
	public void tearDown() throws InterruptedException{
		System.out.println("Sleep for 3 seconds then quit");
		Thread.sleep(3000);
		driver.quit();
	}

}
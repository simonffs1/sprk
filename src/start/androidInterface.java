package start;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;

public class androidInterface implements clickableObjects {
	
	static AndroidDriver driver;
	WebDriverWait wait;
	List<WebElement> allTextView;
	
	//device screen
	int screenHeight;
	int screenWidth;
	
	String connect = "hey";

	public void clickMenu(){
		driver.findElementByXPath("//*[@class='android.widget.ImageButton' and @index='0']").click();	
		System.out.println("Clicked on menu");
		//Verify the pocket nav is open by checking settings exists
		driver.findElement(By.id("com.sphero.sprk:id/settings"));
		allTextView = driver.findElements(By.id("com.sphero.sprk:id/menu_item_text")); 
	}
	public void clickMenuItem(String s){
		for (WebElement temp : allTextView) {
			//System.out.println(temp.getText());
			if(temp.getText().toLowerCase().equals(s.toLowerCase())){
				temp.click();
				System.out.println("Clicked " + s);
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
	
	public void clickOk(){
		//Find dialog
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("android:id/content")));
		//Click No
		driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click();
		System.out.println("Clicked Yes/Ok");
	}
	public void clickNo(){
		//Find dialog
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("android:id/content")));
		//Click No
		driver.findElementById("com.sphero.sprk:id/buttonDefaultNegative").click();
		System.out.println("Clicked No/Cancel");
		//Wait for dialog to disappear
		//wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("com.sphero.sprk:id/content")));
	}
	public void sendKeys(String s){
		driver.findElementById("com.sphero.sprk:id/edit_text").sendKeys(s);
	}
	public void checkToolBarTitle(String s){
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/toolbar_title")));
		String screen = driver.findElementById("com.sphero.sprk:id/toolbar_title").getText();
		Assert.assertEquals(screen.toLowerCase(),s.toLowerCase());
	}
	
	//Build
	public void clickAddNewProgram(){
		driver.findElementById("com.sphero.sprk:id/floating_add_new_program_button").click();
		//Verify text field appears
		driver.findElementById("com.sphero.sprk:id/edit_text");
	}
	public void clickMyProgramsTab(){
		driver.findElementByXPath("//*[@class='android.support.v7.a.d' and @index='0']").click();
		driver.findElementById("com.sphero.sprk:id/program_image");
		driver.findElementById("com.sphero.sprk:id/floating_add_new_program_button");
	}
	public void clickSampleProgramsTab(){
		driver.findElementByXPath("//*[@class='android.support.v7.a.d' and @index='1']").click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/program_image")));
	}
	public void clickProgramEdit(){
		driver.findElementById("com.sphero.sprk:id/overflow_menu").click();
		
	}
	public void clickSave(){
		driver.findElementById("com.sphero.sprk:id/dialog_action").click();
	}
	
	
	//Shared between build and explore
	public void clickProgram(){
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/program_image")));
		driver.findElementById("com.sphero.sprk:id/program_image").click();
		System.out.println("Clicked a program");
	}
	public void clickCopy(){
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/dialog_title")));
		driver.findElementById("com.sphero.sprk:id/make_a_copy_button").click();
		System.out.println("Clicked Copy");
	}
	public void clickView(){
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/dialog_title")));
		//Click on View
		driver.findElementById("com.sphero.sprk:id/view_button").click();
		System.out.println("Clicked View");
	}
	public void clickClose(){
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/dialog_title")));
		driver.findElementById("com.sphero.sprk:id/dialog_close").click();
		System.out.println("Clicked close");
	}
	public void closeSignIn(){
		
	}
	public void leaveCanvas(){
		int attempt =0 ;
		String s = driver.currentActivity();
		while ((driver.currentActivity()).toLowerCase().contains("unity")){
			System.out.println("IN Canvas");
			driver.pressKeyCode(AndroidKeyCode.BACK);
			attempt++;
			if(attempt>10){
				Assert.fail("Could not leave canvas");
				break;
			}
		}
		Assert.assertNotEquals(s, driver.currentActivity());
		System.out.println("Left Canvas");
	}
	public void clickSphero(){
		driver.findElementById("com.sphero.sprk:id/choose_sphero").click();
	}
	public void clickOllie(){
		driver.findElementById("com.sphero.sprk:id/choose_ollie").click();
	}
	public void clickBB8(){
		driver.findElementById("com.sphero.sprk:id/choose_bb8").click();
	}
	
	public void dismissHint(){
		try{
			if(driver.findElement(By.id("com.sphero.sprk:id/notification_title")).isDisplayed() == true ){
			driver.findElementById("com.sphero.sprk:id/notification_close").click();
			System.out.println("Hint view exists, clicked X");
			}
		}
		catch(Exception e){
			System.out.println("Hint view does not exist, skipping dismissal");
		}
	}
	
	public void dismissNewsletter(Boolean trigger){
		try{
			if(driver.findElement(By.id("com.sphero.sprk:id/subscribe_button")).isDisplayed() == true ){
			
				if(trigger==true){
					//Dismiss
					driver.findElementById("com.sphero.sprk:id/skip_button").click();
					System.out.println("Newsletter found, clicked X");
				}
				else{
					//Sign up
					driver.findElementById("com.sphero.sprk:id/age_confirmation_checkbox").click();
					driver.findElementById("com.sphero.sprk:id/email").sendKeys("simon@fingerfoodstudios.com");
					driver.pressKeyCode(AndroidKeyCode.BACK);
					driver.findElementById("com.sphero.sprk:id/subscribe_button").click();
					driver.findElementById("com.sphero.sprk:id/buttonDefaultPositive").click();
				}
			}
		}
		catch(Exception e){
			System.out.println("Newsletter not found, skipping dismissal");
		}
	}
	//Explore
	public void clickExploreTab(){
		driver.findElementByXPath("//*[@class='android.support.v7.a.d' and @index='0']").click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/program_image")));
		System.out.println("Explore tab clicked");
	}
	public void clickMediaTab(){
		driver.findElementByXPath("//*[@class='android.support.v7.a.d' and @index='1']").click();
		driver.findElementById("com.sphero.sprk:id/floating_add_new_media_button");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/program_image")));
		System.out.println("Media tab clicked");
	}
	public void clickTwitterTab(){
		driver.findElementByXPath("//*[@class='android.support.v7.a.d' and @index='2']").click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.sphero.sprk:id/tw__tweet_view")));
		System.out.println("Twitter tab clicked");
	}
	public void clickAddMedia(){
		driver.findElementById("com.sphero.sprk:id/floating_add_new_media_button").click();
		//Check result add media or user gate??
		System.out.println("Add Media button clicked");
	}
	
	//Sign in
	public void clickPocketNavSignIn(){
		driver.findElementById("com.sphero.sprk:id/profile_header_not_signed_in").click();
		//Wait for sign in container to appear
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.sphero.sprk:id/fragment_container")));
		System.out.println("Clicked on Pocket Nav Sign in");
	}
    public void clickSignIn(Boolean actual){
        driver.findElementById("com.sphero.sprk:id/sign_in_button").click();
        System.out.println("Clicked on Sign in button");
        if(actual == true){
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("android:id/progress")));
            System.out.println("Sign in progress indicator appeared");
        }
    }
	public void clickProfile(){
		driver.findElementById("com.sphero.sprk:id/profile_header_signed_in").click();
		//Verify sign out button, profile pic exist
		driver.findElementById("com.sphero.sprk:id/sign_out_button");
		driver.findElementById("com.sphero.sprk:id/profile_image");
	}
	public void clickCloseSignIn(){
		//driver.findElementById("com.sphero.sprk:id/close_button").click();
		driver.findElementById("com.sphero.sprk:id/nav_button").click();
	}
	public void clickSignOut(){
		driver.findElementById("com.sphero.sprk:id/sign_out_button").click();
		System.out.println("Clicked on Sign out button");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("android:id/content")));
		System.out.println("Sign confirmation appeared");
	}
	
	//Pocketnav
	public void clickConnectRobot(){
		driver.findElementById("com.sphero.sprk:id/header_not_connected").click();
		//Verify robot buttons exists
		driver.findElementById("com.sphero.sprk:id/choose_sphero_button");
		driver.findElementById("com.sphero.sprk:id/choose_ollie_button");
		driver.findElementById("com.sphero.sprk:id/choose_bb8_button");
		System.out.println("Connect Sphero clicked and elements found");
	}
	
	public void chooseSphero(){
		driver.findElementById("com.sphero.sprk:id/choose_sphero_button").click();
		//Verify title and close button exists
		driver.findElementByXPath("//*[@resource-id='com.sphero.sprk:id/dialog_title' and @text='Connect Sphero']");
		driver.findElementById("com.sphero.sprk:id/dialog_close");
		driver.findElementById("com.sphero.sprk:id/find_and_connect_button");
		System.out.println("Connect Sphero clicked and elements found");
	}
	public void chooseOllie(){
		driver.findElementById("com.sphero.sprk:id/choose_ollie_button").click();
		//Verify title and close button exists
		driver.findElementByXPath("//*[@resource-id='com.sphero.sprk:id/dialog_title' and @text='Connect Ollie']");
		driver.findElementById("com.sphero.sprk:id/dialog_close");
		System.out.println("Connect Ollie clicked and elements found");
	}
	public void chooseBB8(){
		driver.findElementById("com.sphero.sprk:id/choose_bb8_button").click();
		//Verify title and close button exists
		driver.findElementByXPath("//*[@resource-id='com.sphero.sprk:id/dialog_title' and @text='Connect BB-8']");
		driver.findElementById("com.sphero.sprk:id/dialog_close");
		System.out.println("Connect BB8 clicked and elements found");
	}
	public void clickSettings(){
		
	}
	

}

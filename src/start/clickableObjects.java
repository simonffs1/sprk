package start;

public interface clickableObjects {
	

	//Global
	void clickMenu();
	void clickMenuItem(String s);
	void clickOk();
	void clickNo();
	void sendKeys(String s);
	
	//Build
	void clickAddNewProgram();
	void clickMyProgramsTab();
	void clickSampleProgramsTab();
	void clickProgramEdit();
	
	//Build - Edit Program
	
	//Build - Sample program details
	
	//Shared between build and explore
	void clickProgram();
	void clickCopy();
	void clickView();
	void leaveCanvas();
	void dismissHint();
	void dismissNewsletter(Boolean trigger);

	
	//Explore 
	void clickExploreTab();
	void clickMediaTab();
	void clickTwitterTab();
	void clickAddMedia();
	
	//Pocketnav
	void clickConnectRobot();

}

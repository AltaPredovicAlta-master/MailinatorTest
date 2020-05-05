package com.MailinatorTest.MailinatorTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.Mailinator.factory.DataProviderFactory;
import com.github.javafaker.Faker;
import com.sun.xml.xsom.impl.scd.Iterators.Map;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class EmailTest {

	WebDriver driver;
	String emailname = null;
	String emailsubject = null;
	String sDate1 = null;
	Date emailDateTime = null;
	String output = null;

	@Test(enabled = false)
	public void EmailTestCase() throws InterruptedException, ParseException {

		/*
		 * to get system current date and time
		 */
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date systemcurrentDateTime = new Date();
		String currentDateTimeString = dateFormat.format(systemcurrentDateTime);
		System.out.println("systemcurrentDateTime :" + currentDateTimeString);

		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://www.mailinator.com/v3/index.jsp?zone=public&query=raheel#/#inboxpane");
		WebDriverWait wait = new WebDriverWait(driver, 20);

		Thread.sleep(2000);

		/*
		 * get count of all email from inbox
		 */
		List rows = driver.findElements(By.xpath(".//*[@id=\"inboxpane\"]/div/div/div/table/tbody/tr"));
		System.out.println("Total No of rows are : " + rows.size());

		// disabled cookies option before perform any operation on element
		driver.findElement(By.linkText("Got it!")).click();

		/*
		 * start the loop count on email which we before get
		 */
		for (int i = 1; i <= rows.size(); i++) {

			// Click on email by using using loop count, (int i) is the Variable which hold
			// the value of loop
			driver.findElement(
					By.xpath("/html/body/div[2]/div/div[3]/div/div[8]/div/div/div/table/tbody/tr[" + i + "]/td[3]"))
					.click();
			System.out.println("step1 : Click on one by one item");

			Thread.sleep(2000);

			// get date and time of every email by loop count
			emailname = driver.findElement(By.xpath("//*[@id=\"msgpane\"]/div/div/div[1]/table/tbody/tr[4]/td[2]/b"))
					.getText();
			System.out.println("step2 : get date and time of email");

			// get email subject of every email by loop count
			emailsubject = driver.findElement(By.xpath("//*[@id=\"msgpane\"]/div/div/div[1]/table/tbody/tr[1]/td[2]/b"))
					.getText();
			System.out.println("step3 : get subject of email");

			// format the email date and time as per the system date and time
			sDate1 = emailname.substring(0, 3) + "," + emailname.substring(3, 24);
			DateFormat df = new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss");
			DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			emailDateTime = df.parse(sDate1);
			output = outputformat.format(emailDateTime);
			System.out.println("emailDateTimeString :" + output);

			System.out.println("step4 : compare system dateTime with email dateTime");

			// in this step Compare the system date and time with email date and time, and
			// get the difference in seconds
			long seconds = (systemcurrentDateTime.getTime() - emailDateTime.getTime()) / 1000;

			if (seconds >= 1 && seconds <= 300) {

				System.out.println("inside pass condition with time difference is 300sec");

				// switch to email body because it used iframe
				driver.switchTo().frame("msg_body");

				// in this step compare the email subject for verify email and join care
				if (emailsubject.compareToIgnoreCase("Fwd: Cares-QA Verify your email address") == 0) {
					// click on verify link
					// WebDriverWait wait1 = new WebDriverWait(driver, 500);
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Verify and activate")))
							.click();
					System.out.println("step5 : click on Verify and activate button");
				} else if (emailsubject
						.compareToIgnoreCase("Fwd: [QA Environment] New invite to join care circle") == 0) {
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Join Care Circle"))).click();
					System.out.println("step5 : click on Join Care Circle on button");
				}

				// closed the current browser tab after Completing of process
				Set<String> handlesSet = driver.getWindowHandles();
				List<String> handlesList = new ArrayList<String>(handlesSet);
				driver.switchTo().window(handlesList.get(1));
				driver.close();
				driver.switchTo().window(handlesList.get(0));
				System.out.println("step6 : closed current browser tab");

				// click on back to inbox after closed the current browser and complete the
				// process
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath("//*[@id=\"msgpane\"]/div/div/div[2]/a"))).click();

				System.out.println("step7 : back to inbox");

				// print result
				System.out.println("email verify at:" + emailname);
			} else {

				System.out.println("inside else condition with time difference is not 300sec");

				// click on back to inbox link
				WebDriverWait wait1 = new WebDriverWait(driver, 20);
				wait1.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath("//*[@id=\"msgpane\"]/div/div/div[2]/a"))).click();

				System.out.println("step8 : back to inbox");
			}

		}

		driver.quit();
	}

	@Test(enabled = false)
	public void amazonTestCase() throws InterruptedException {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://www.amazon.com");

		// Implicit wait till page loads
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// driver.findElement(By.cssSelector("img[alt='Track your package']")).click();

		WebElement links = driver.findElement(By.cssSelector(
				"div[class='a-section a-spacing-none feed-carousel first-carousel']>div[class='a-section feed-carousel-viewport']>ul"));
		List<WebElement> items = links.findElements(By.tagName("img"));
		System.out.println("items count :" + items.size());

		for (WebElement element : items) {

			if (element.getAttribute("alt").equalsIgnoreCase("Track your package")) {
				System.out.println("item found name :" + element.getAttribute("alt"));
				driver.findElement(By.cssSelector("img[alt='Track your package']")).click();
				break;
			}
		}

		/*
		 * WebElement links =
		 * driver.findElement(By.cssSelector("#widgetFilters > div:nth-child(2)"));
		 * List<WebElement> items = links.findElements(By.tagName("span"));
		 * System.out.println("items count :" + items.size()); for(WebElement element :
		 * items) { System.out.println("item Name :" + element.getText());
		 * 
		 * if(element.getText().trim().equals("Deal of the Day")) {
		 * System.out.println("Deal of the Day found");
		 * driver.findElement(By.linkText("Deal of the Day")).click(); break; }
		 * 
		 * }
		 */

		// String val = driver.findElement(By.cssSelector("#widgetFilters >
		// div:nth-child(2) > span:nth-child(3)")).getText();

		// driver.findElement(By.linkText("Prime Early Access Deals")).click();

		// ==============================================================
		// Select selectbytext = new
		// Select(driver.findElement(By.cssSelector("select[name='sortOptions']")));
		// selectbytext.selectByVisibleText("Discount - Low to High");

		/*
		 * Select select = new
		 * Select(driver.findElement(By.cssSelector("select[name='sortOptions']")));
		 * List<WebElement> allOptions = select.getOptions();
		 * 
		 * for(int i=0; i<allOptions.size(); i++) {
		 * 
		 * 
		 * if(allOptions.get(i).getText().trim().equals("Discount - High to Low")) {
		 * System.out.println("value match :" + allOptions.get(i).getText().trim());
		 * select.selectByVisibleText(allOptions.get(i).getText().trim()); break; } }
		 */

		/*
		 * driver.findElement(By.cssSelector("span.a-button-text.a-declarative")).click(
		 * );
		 * 
		 * //Implicit wait till page loads
		 * driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
		 * 
		 * List<WebElement> links =
		 * driver.findElements(By.cssSelector("li.a-dropdown-item"));
		 * 
		 * for(WebElement element: links) {
		 * if(element.getText().trim().equals("Discount - High to Low")) {
		 * System.out.println("Item Match :" + element.getText());
		 * driver.findElement(By.linkText(element.getText().trim())).click();
		 * 
		 * }
		 * 
		 * }
		 */

		Thread.sleep(3000);
		driver.quit();
	}

	public String genphonenumber() {
		Random rand = new Random();
		int num1 = (rand.nextInt(7) + 1) * 100 + (rand.nextInt(8) * 10) + rand.nextInt(8);
		int num2 = rand.nextInt(743);
		int num3 = rand.nextInt(10000);

		DecimalFormat df3 = new DecimalFormat("000"); // 3 zeros
		DecimalFormat df4 = new DecimalFormat("0000"); // 4 zeros

		String phoneNumber = df3.format(num1) + "-" + df3.format(num2) + "-" + df4.format(num3);

		return phoneNumber;

	}

	public void updateproFile(String value, String Case, String status) throws IOException {

		Faker faker = new Faker();

		String firstName = faker.name().firstName();
		String lastName = faker.name().lastName();
		String phone = genphonenumber();
		String email = firstName + lastName + "@gmail.com";
		
		FileOutputStream fileOut = null;
        FileInputStream fileIn = null;
        
            Properties configProperty = new Properties();
            
            if(value.equalsIgnoreCase("APITestData1") && Case.equalsIgnoreCase("Case1"))
            {
            
            File file = new File("./configs/RandomData2.properties");
            fileIn = new FileInputStream(file);
            configProperty.load(fileIn);
            configProperty.setProperty("Case1fname", firstName);
    		configProperty.setProperty("Case1lname", lastName);
    		configProperty.setProperty("Case1phone", phone);
    		configProperty.setProperty("Case1email", email);
    		configProperty.setProperty("Case1status", status);
            fileOut = new FileOutputStream(file);
            configProperty.store(fileOut, null);
            fileIn.close();
            } else if(value.equalsIgnoreCase("updatestatus") && Case.equalsIgnoreCase("Case1"))
            {
            	File file = new File("./configs/RandomData2.properties");
                fileIn = new FileInputStream(file);
                configProperty.load(fileIn);
                configProperty.setProperty("Case1status", status);
                fileOut = new FileOutputStream(file);
                configProperty.store(fileOut, null);
                fileIn.close();
            }

		/*
		FileOutputStream fileOut = null;
		FileInputStream fileIn = null;

		Properties configProperty = new Properties();

		File file = new File("./configs/RandomData.properties");
		fileIn = new FileInputStream(file);

		configProperty.load(fileIn);
		configProperty.setProperty("fname4", firstName);
		configProperty.setProperty("lname4", lastName);
		configProperty.setProperty("phone4", phone);
		configProperty.setProperty("email4", email);
		fileOut = new FileOutputStream(file);
		configProperty.store(fileOut, "");
		*/
	}

	@Test(enabled = false)
	public void APITestData2() throws IOException {

		updateproFile("APITestData1","Case1","DataSaved");

		String statusvalue = DataProviderFactory.getRandomDataProperty().getValue("Case1status");
		
		if (statusvalue.equalsIgnoreCase("DataSaved"))
		{
			System.out.println("Call Salesforce API and set status as UserCreated");
			updateproFile("updatestatus","Case1","UserCreated");
			
		}
		
		System.out
				.println("Fname from Data Provider :" + DataProviderFactory.getRandomDataProperty().getValue("Case1status"));
		//System.out
		//		.println("Lname from Data Provider :" + DataProviderFactory.getRandomDataProperty().getValue("lname4"));
		/*
		 * System.out .println("phone from Data Provider :" +
		 * DataProviderFactory.getRandomDataProperty().getValue("phone6")); System.out
		 * .println("email from Data Provider :" +
		 * DataProviderFactory.getRandomDataProperty().getValue("email6"));
		 */

	}

	@Test
	public void APITestData() throws IOException {

		//updateproFile("APITestData1");
		String  requiresTTY = "false";
		String isCareRecipient = "true";
		String middlename = "";
		String fName =  DataProviderFactory.getRandomDataProperty().getValue("Case1fname");
		String lastName = DataProviderFactory.getRandomDataProperty().getValue("Case1lname");
		String phone = DataProviderFactory.getRandomDataProperty().getValue("Case1phone");
		String email = DataProviderFactory.getRandomDataProperty().getValue("Case1email");
		/////// Call API

		Response res = given()
				  .log().all()
				  . header("Content-Type","application/json")
				  . header("APIKey","sAUvHEgIxduLmPiwoJy0FelsW0GqcjLgqrRKAz4RIPw=")
				  . header("DeviceID", "357470096094051")
				  . header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VyQ29kZSI6IjMxOTMxOSIsIk1vYmlsZU5vIjoiOTIzMjEyNDg0Nzk0IiwibmJmIjoxNTc0Njc3NzI4LCJleHAiOjE1OTM1OTM5NDUyOCwiaWF0IjoxNTc0Njc3NzI4fQ.6an-tGD8zSLqXP7kF17GGAsRm7-07n4Gzv1XTBK0GYA")
				  . body("{\n" + "  \"requiresTTY\": \"" + requiresTTY + "\"," + "\n" +
						  		 "  \"firstName\": \"" + fName + "\"," + "\n" +  
						  		 "  \"middlename\": \"" + middlename + "\"," + "\n" +
						  		"  \"lastName\": \"" + lastName + "\"," + "\n" +
						  		 "  \"language\": {" +
												  		 "  \"value\": \"English\", \n" + 
												  		 "  \"type\": \"Primary\" \n" +
												  "}," +
								"  \"phones\": [" +
												"{\n" +
													"  \"number\": \"" + phone + "\"," + "\n" +
													"  \"type\": \"Home\", \n" + 
													"  \"order\": 1 \n" +
													"}" +
												"]," +
								"  \"emails\": [" +
												"{\n" +
													"  \"address\": \"" + email + "\"," + "\n" +
													"  \"type\": \"Personal\" \n" + 
												"}" +
											"]," +
								"  \"isCareRecipient\": \"" + isCareRecipient + "\"," + "\n" +
								"  \"dateOfBirth\": \"1943-09-03\", \n" + 
								"  \"addresses\": [" +
												"{\n" +
													"  \"line1\": \"111 Lawrence St\", \n" + 
													"  \"line2\": \"\", \n" + 
													"  \"city\": \"Framingham\", \n" + 
													"  \"zip\": \"01702\", \n" + 
													"  \"country\": \"US\", \n" + 
													"  \"addressType\": \"Home\", \n" + 
													"  \"state\": \"MA\" \n" + 
												"}" +
										"]," +
								"  \"gender\": \"Male\" \n" + 
						  "}")
				  .when()
				  . post("https://altpay.company/GatewayConsumerService/ConsumerApp/LoadDiscounts")
				  .then().assertThat()
				  . statusCode(200)
				  . extract().response();
		  
		  String headerValue = res.header("Authorization");
		  System.out.println(headerValue);
		
		
		  /*Response res = given()
				  .log().all()
				  . header("Content-Type","application/json")
				  . header("APIKey","sAUvHEgIxduLmPiwoJy0FelsW0GqcjLgqrRKAz4RIPw=")
				  . header("DeviceID", "357470096094051")
				  . header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VyQ29kZSI6IjMxOTMxOSIsIk1vYmlsZU5vIjoiOTIzMjEyNDg0Nzk0IiwibmJmIjoxNTc0Njc3NzI4LCJleHAiOjE1OTM1OTM5NDUyOCwiaWF0IjoxNTc0Njc3NzI4fQ.6an-tGD8zSLqXP7kF17GGAsRm7-07n4Gzv1XTBK0GYA")
				  . body("{\n" + "  \"AccountType\": \"DigiCash\",\n" +
						  		 "  \"City\": \"fasalabad\", \n" + 
						  		 "  \"ProductName\": \"Altron\"\n" + "}")
				  .when()
				  . post("https://altpay.company/GatewayConsumerService/ConsumerApp/LoadDiscounts")
				  .then().assertThat()
				  . statusCode(200)
				  . extract().response();
		  
		  String headerValue = res.header("Authorization");
		  System.out.println(headerValue);*/
		 
/*
		RestAssured.baseURI = "https://altpay.company/GatewayConsumerService/ConsumerApp/LoadDiscounts";

		RequestSpecification httpRequest = RestAssured.given();

		JSONObject updateData = new JSONObject();
		updateData.put("AccountType", "DigiCash");
		updateData.put("City", "fasalabad");
		updateData.put("ProductName", "Altron");
		
		httpRequest.header("Content-Type", "application/json");
		httpRequest.header("APIKey", "sAUvHEgIxduLmPiwoJy0FelsW0GqcjLgqrRKAz4RIPw=");
		httpRequest.header("DeviceID", "357470096094051");
		httpRequest.header("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VyQ29kZSI6IjMxOTMxOSIsIk1vYmlsZU5vIjoiOTIzMjEyNDg0Nzk0IiwibmJmIjoxNTc0Njc3NzI4LCJleHAiOjE1OTM1OTM5NDUyOCwiaWF0IjoxNTc0Njc3NzI4fQ.6an-tGD8zSLqXP7kF17GGAsRm7-07n4Gzv1XTBK0GYA");

		httpRequest.body(updateData.toJSONString());

		Response response = httpRequest.request(Method.POST);

		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 200);

		JsonPath newData = response.jsonPath();
		String CampaignTitle = newData.getString("Data[0].CampaignTitle");
		System.out.println("CampaignTitle :" + CampaignTitle);*/

	}

	public void ReadExcelData() throws IOException {
		try {

			String ExcelData = null;

			File src = new File("configs/SalesforceData.xlsx");

			// load file
			FileInputStream fis = new FileInputStream(src);

			// Load workbook
			XSSFWorkbook wb = new XSSFWorkbook(fis);

			// Load sheet- Here we are loading first sheetonly
			XSSFSheet sh1 = wb.getSheetAt(0);

			// Find number of rows in excel file

			int rowCount = sh1.getLastRowNum() - sh1.getFirstRowNum();

			System.out.println("rowsCount :" + rowCount);
			// Create a loop over all the rows of excel file to read it

			for (int i = 1; i < rowCount + 1; i++) {

				Row row = sh1.getRow(i);

				// Create a loop to print cell values in a row

				for (int j = 0; j < row.getLastCellNum(); j++) {

					// Print Excel data in console
					ExcelData = row.getCell(j).getStringCellValue();
					// System.out.println(ExcelData);

					if (row.getCell(j) != null && (row.getCell(j).toString().equalsIgnoreCase(ExcelData))) {
						sh1.removeRow(row);

						System.out.println("Data Remove :" + ExcelData);
						// sheet.shiftRows(i, lastIndex, -1);
					}
				}
			}
			FileOutputStream fileOut = new FileOutputStream(src);
			wb.write(fileOut);
			fileOut.close();

		} catch (

		Exception e) {

			System.out.println(e.getMessage());

		}
	}

	@Test(enabled = false)
	public void ExcelTestData() throws IOException {
		ReadExcelData();
	}
}

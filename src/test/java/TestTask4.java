import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TestTask4 {
	static WebDriver userCreationDriver;
	static WebDriver testDriver;
	WebDriverWait wait;
	static Random rand = new Random();

	static String email = "random13222221" + rand.nextInt(1000) + rand.nextInt(1000) + "@gmail.com";
	
  
	@BeforeClass
	public static void setUp() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
		ChromeOptions options = new ChromeOptions();
		options.addArguments(new String[] { "--no-sandbox" });
		WebDriverManager.chromedriver().setup();
		userCreationDriver = new ChromeDriver(options);
		createUser();
	}

	@Test
	public void test1() throws InterruptedException {
		testDriver = new ChromeDriver();
		addToCartProducts("data1.txt");
		checkout();
		fillAddress();
		endCheckout();
		testDriver.quit();
	}

	@Test
	public void test2() throws InterruptedException {
		testDriver = new ChromeDriver();
		addToCartProducts("data2.txt");
		checkout();
		endCheckout();
	}

	private static void createUser() throws InterruptedException {

		userCreationDriver.get("https://demowebshop.tricentis.com/");

//		2. Spausti 'Log in'
		WebElement loginButtonLink = userCreationDriver.findElement(By.className("ico-login"));

		loginButtonLink.click();

//		3. Spausti 'Register' skiltyje 'New Customer'
		WebElement registerButtonLink = userCreationDriver
				.findElement(By.cssSelector(".register-block .buttons .register-button"));

		registerButtonLink.click();

//		4. Užpildyti registracijos formos laukus
		WebElement genderMaleRadio = userCreationDriver.findElement(By.id("gender-male"));
		WebElement firstNameInput = userCreationDriver.findElement(By.id("FirstName"));
		WebElement lastNameInput = userCreationDriver.findElement(By.id("LastName"));
		WebElement emailInput = userCreationDriver.findElement(By.id("Email"));
		WebElement passwordInput = userCreationDriver.findElement(By.id("Password"));
		WebElement repeatPasswordInput = userCreationDriver.findElement(By.id("ConfirmPassword"));

		genderMaleRadio.click();
		firstNameInput.sendKeys("Medas");
		lastNameInput.sendKeys("Sirokinas");
		emailInput.sendKeys(email);
		passwordInput.sendKeys("passwordas");
		repeatPasswordInput.sendKeys("passwordas");

//		5. Spausti 'Register'
		WebElement registerButton = userCreationDriver.findElement(By.id("register-button"));

		registerButton.click();

//		6. Spausti 'Continue'
		WebElement continueButton = userCreationDriver.findElement(By.className("register-continue-button"));

		continueButton.click();
	}

	private void addToCartProducts(String dataFilename) throws InterruptedException {
//		1. Atsidaryti tinklalapį https://demowebshop.tricentis.com/
		testDriver.get("https://demowebshop.tricentis.com/");

//		2. Spausti 'Log in'
		testDriver.findElement(By.className("ico-login")).click();

//		3. Užpildyti 'Email:', 'Password:' ir spausti 'Log in'
		WebElement emailInput = testDriver.findElement(By.id("Email"));
		WebElement passwordInput = testDriver.findElement(By.id("Password"));
		WebElement loginButton = testDriver.findElement(By.className("login-button"));

		emailInput.sendKeys(email);
		passwordInput.sendKeys("passwordas");

		loginButton.click();

//		4. Šoniniame meniu pasirinkti 'Digital downloads'		
		testDriver.findElement(By.linkText("Digital downloads")).click();

//		5. Pridėti į krepšelį prekes nuskaitant tekstinį failą (pirmam testui skaityti iš data1.txt, antram testui skaityti iš data2.txt)
		List<WebElement> products = testDriver.findElements(By.className("details"));
		try {
			BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/" + dataFilename));
			String line;

			while ((line = reader.readLine()) != null) {
				for (WebElement product : products) {
					if (product.findElement(By.className("product-title")).getText().contains(line)) {
						product.findElement(By.className("product-box-add-to-cart-button")).click();

						Thread.sleep(1000);
						break;
					}
				}
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void checkout() throws InterruptedException {
//		6. Atsidaryti 'Shopping cart'
		testDriver.findElement(By.className("ico-cart")).click();

//		7. Paspausti 'I agree' varnelę ir mygtuką 'Checkout'
		testDriver.findElement(By.id("termsofservice")).click();
		testDriver.findElement(By.id("checkout")).click();
	}

	private void fillAddress() throws InterruptedException {
//		8. 'Billing Address' pasirinkti jau esantį adresą arba supildyti naujo adreso laukus
		WebElement countryDropdown = testDriver.findElement(By.id("BillingNewAddress_CountryId"));
		Select countrySelect = new Select(countryDropdown);
		countrySelect.selectByVisibleText("Lithuania");

		testDriver.findElement(By.id("BillingNewAddress_City")).sendKeys("Vilnius");
		testDriver.findElement(By.id("BillingNewAddress_Address1")).sendKeys("Fabijoniskes");
		testDriver.findElement(By.id("BillingNewAddress_ZipPostalCode")).sendKeys("LT-1234");
		testDriver.findElement(By.id("BillingNewAddress_PhoneNumber")).sendKeys("860000000");

	}

	private void endCheckout() {
//		8. spausti 'Continue'
		wait = new WebDriverWait(testDriver, Duration.ofSeconds(5));
		testDriver.findElement(By.cssSelector("input[value='Continue']")).click();

//		9. 'Payment Method' spausti 'Continue'
		wait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector(".tab-section.allow.active input[value='Continue']"))).click();

//		10. 'Payment Information' spausti 'Continue'
		wait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector(".tab-section.allow.active input[value='Continue']"))).click();

//		11. 'Confirm Order' spausti 'Confirm'
		wait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector(".tab-section.allow.active input[value='Confirm']"))).click();

//		12. Įsitikinti, kad užsakymas sėkmingai užskaitytas.		
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".section.order-completed")));
		Assert.assertEquals("Your order has been successfully processed!",
				testDriver.findElement(By.className("title")).getText());
	}

	@AfterClass
	public static void tearDown() {
		testDriver.quit();
		userCreationDriver.quit();
	}
}
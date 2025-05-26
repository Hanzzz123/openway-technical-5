package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;


public class PeriplusCartTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private StringBuilder testLog;

    Dotenv dotenv = Dotenv.load();

    private final String email = dotenv.get("EMAIL");
    private final String password = dotenv.get("PASSWORD");

    private final String seleniumMode = dotenv.get("SELENIUM_MODE");
    public String res;

    public PeriplusCartTest() {
        testLog = new StringBuilder();
    }

    @BeforeMethod
    public void setUp() throws MalformedURLException {

        if (Objects.equals(email, "")) {
            throw new IllegalArgumentException("Please provide an email in the .env file");
        }

        if (Objects.equals(password, "")){
            throw new IllegalArgumentException("Please provide a Password in the .env file");
        }

        if (seleniumMode == "DOCKER"){
            driver = new RemoteWebDriver(
                    new URL("http://localhost:4444/wd/hub"),
                    new ChromeOptions()
            );
        }
        else{
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        }
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();
        testLog.append("Browser opened.\n");
    }

    @Test
    public void testFullWorkflow(){
        try {
            testLogin();
            searchProduct();
            res = addToCart();
            verifyCart();
        } catch (Exception e) {
            testLog.append("Test FAILED: " + e.getMessage() + "\n");
            Assert.fail("Test failed: " + e.getMessage());
        }
    }

    private void testLogin() {
        try {

            driver.get("https://www.periplus.com/");
            testLog.append("Navigated to Periplus.\n");
            driver.findElement(By.partialLinkText("Sign In")).click(); // Updated locator
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            System.out.println("Attempting to Login with .env username and password");
            driver.findElement(By.name("email")).sendKeys(email);
            driver.findElement(By.name("password")).sendKeys(password);
            driver.findElement(By.id("button-login")).click();
            testLog.append("Logged in.\n");
            System.out.println("Log In Successful");
        } catch (Exception e) {
            testLog.append("Test FAILED: " + e.getMessage() + "\n");
            Assert.fail("Login failed: " + e.getMessage()); // Fail the test on exception
        }
    }

    private void searchProduct() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("filter_name")));
            System.out.println("Searching for The Daily Dad");
            driver.findElement(By.id("filter_name")).sendKeys("The Daily Dad: 366 Meditations on Parenting");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            testLog.append("Searched for 'The daily dad'.\n");
            System.out.println("Searching Successful");
        } catch (Exception e) {
            testLog.append("Search FAILED: " + e.getMessage() + "\n");
            throw e;
        }
    }
    public String addToCart() {
        String addedProductName;
        try {
            System.out.println("Adding product to cart");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-content")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            WebElement product = driver.findElement(By.cssSelector(".product-content"));
            addedProductName = product.findElement(By.cssSelector("h3 a")).getText().trim();
            WebElement productLink = product.findElement(By.cssSelector("h3 a"));
            wait.until(ExpectedConditions.elementToBeClickable(productLink));
            productLink.click();
            testLog.append("Navigated to product details page for '" + addedProductName + "'.\n");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            WebElement addToCartButton = driver.findElement(By.cssSelector(".btn.btn-add-to-cart"));
            wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
            addToCartButton.click();
            System.out.println("Add to cart Successful");
            testLog.append("Added product '" + addedProductName + "' to cart.\n");
        } catch (Exception e) {
            testLog.append("Add to Cart FAILED: " + e.getMessage() + "\n");
            throw e;
        }
        return addedProductName;
    }


    private void verifyCart() throws InterruptedException {
        try {
            System.out.println("Verifying Cart");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("show-your-cart")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            WebElement cart =  driver.findElement(By.id("show-your-cart"));
            WebElement cartLink = cart.findElement(By.cssSelector("a[href='https://www.periplus.com/checkout/cart']"));
            Thread.sleep(3000);
            wait.until(ExpectedConditions.elementToBeClickable(cartLink));
            cartLink.click();
            testLog.append("Navigated to cart page.\n");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-name.limit-lines")));
            String cartProductName = driver.findElement(By.cssSelector(".product-name.limit-lines a")).getText().trim();
            testLog.append("Comparing cart product name: '" + cartProductName + "' with added product name: '" + res + "'.\n");
             if (cartProductName.startsWith(res.replace("...", ""))) {
                testLog.append("Test PASSED: Product '" + cartProductName + "' found in cart.\n");
            } else {
                testLog.append("Test FAILED: Expected '" + res + "', but found '" + cartProductName + "' in cart.\n");
                Assert.fail("Cart verification failed");
            }
            System.out.println("Verifying Successful");
        } catch (Exception e) {
            testLog.append("Cart Verification FAILED: " + e.getMessage() + "\n");
            throw e;
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            testLog.append("Browser closed.\n");
        }
        System.out.println(testLog.toString());
    }

}
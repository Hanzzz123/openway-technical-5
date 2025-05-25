package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;
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


public class PeriplusCartTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private StringBuilder testLog;

    Dotenv dotenv = Dotenv.load();

    private final String email = dotenv.get("EMAIL");
    private final String password = dotenv.get("PASSWORD");
    public String res;

    public PeriplusCartTest() {
        testLog = new StringBuilder();
    }

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        WebDriverManager.chromedriver().setup();

        //testing locally (comment this if testing portable with docker)
        //WebDriverManager.chromedriver().setup();
        //driver = new ChromeDriver();

        //testing portable with docker (comment this out if testing locally)
        driver = new RemoteWebDriver(
                new URL("http://localhost:4444/wd/hub"),
                new ChromeOptions()
        );

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
            driver.findElement(By.name("email")).sendKeys(email);
            driver.findElement(By.name("password")).sendKeys(password);
            driver.findElement(By.id("button-login")).click();
            testLog.append("Logged in.\n");
        } catch (Exception e) {
            testLog.append("Test FAILED: " + e.getMessage() + "\n");
            Assert.fail("Login failed: " + e.getMessage()); // Fail the test on exception
        }
    }

    private void searchProduct() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("filter_name")));
            driver.findElement(By.id("filter_name")).sendKeys("The Daily Dad: 366 Meditations on Parenting");
            // Wait for preloader to disappear
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            // Click the search button
            WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            testLog.append("Searched for 'The daily dad'.\n");
        } catch (Exception e) {
            testLog.append("Search FAILED: " + e.getMessage() + "\n");
            throw e;
        }
    }
    public String addToCart() {
        String addedProductName;
        try {
            // Wait for search results to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-content")));
            // Ensure the preloader is gone before proceeding
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            // Select the first product and get its name
            WebElement product = driver.findElement(By.cssSelector(".product-content"));
            addedProductName = product.findElement(By.cssSelector("h3 a")).getText().trim();
            // Click the product link to navigate to the product details page
            WebElement productLink = product.findElement(By.cssSelector("h3 a"));
            wait.until(ExpectedConditions.elementToBeClickable(productLink));
            productLink.click();
            testLog.append("Navigated to product details page for '" + addedProductName + "'.\n");

            // Wait for the product details page to load
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            // Click the Add to Cart button
            WebElement addToCartButton = driver.findElement(By.cssSelector(".btn.btn-add-to-cart"));
            wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
            addToCartButton.click();
            testLog.append("Added product '" + addedProductName + "' to cart.\n");
        } catch (Exception e) {
            testLog.append("Add to Cart FAILED: " + e.getMessage() + "\n");
            throw e;
        }
        return addedProductName;
    }


    private void verifyCart() throws InterruptedException {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("show-your-cart")));
            // Ensure the preloader is gone before proceeding
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            WebElement cart =  driver.findElement(By.id("show-your-cart"));
            WebElement cartLink = cart.findElement(By.cssSelector("a[href='https://www.periplus.com/checkout/cart']"));
            Thread.sleep(3000);
            //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("Notification-Modal")));
            //WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn.btn-modal-close.close")));
            //closeButton.click();
            wait.until(ExpectedConditions.elementToBeClickable(cartLink));
            cartLink.click();
            testLog.append("Navigated to cart page.\n");
            // Wait for cart items to load
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("preloader")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-name.limit-lines")));
            // Verify the product in the cart
            String cartProductName = driver.findElement(By.cssSelector(".product-name.limit-lines a")).getText().trim();
            //String cartProductName = driver.findElement(By.cssSelector(".product-name limit-lines h3 a")).getText().trim();
            testLog.append("Comparing cart product name: '" + cartProductName + "' with added product name: '" + res + "'.\n");
            // Handle truncation by checking if the cart product name starts with the added product name (ignoring ellipsis)// Handle truncation by checking if the cart product name starts with the added product name (ignoring ellipsis)
            if (cartProductName.startsWith(res.replace("...", ""))) {
                testLog.append("Test PASSED: Product '" + cartProductName + "' found in cart.\n");
            } else {
                testLog.append("Test FAILED: Expected '" + res + "', but found '" + cartProductName + "' in cart.\n");
                Assert.fail("Cart verification failed");
            }
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
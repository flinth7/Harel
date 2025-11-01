package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.Duration.*;

public class TravelInsuranceTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "https://digital.harel-group.co.il/travel-policy";

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, ofSeconds(15));
        driver.manage().window().maximize();
    }

    @Test
    public void travelInsuranceFlow() {
        driver.get(BASE_URL);

        // 2. לחץ על כפתור "לרכישה בפעם הראשונה"
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'לרכישה בפעם הראשונה')]"))).click();

        // 3. בחר אחת מהיבשות
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[data-hrl-bo='europe']"))).click(); // לדוגמה אירופה

        // 4. לחץ על כפתור "הלאה לבחירת תאריכי הנסיעה"
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'הלאה לבחירת תאריכי הנסיעה')]"))).click();

        // 5. בחר תאריכים
        LocalDate departureDate = LocalDate.now().plusDays(7);
        LocalDate returnDate = departureDate.plusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter pickerFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        WebElement departureInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[data-hrl-bo='startDateInput_input']")));
        WebElement returnInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[data-hrl-bo='endDateInput_input']")));

        departureInput.clear();
        departureInput.sendKeys(departureDate.format(formatter));
        returnInput.clear();
//        returnInput.sendKeys(returnDate.format(formatter));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[data-hrl-bo='arrow-forward']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(.,'" + returnDate.format(pickerFormatter) + "')]"))).click();
        returnInput.sendKeys(Keys.TAB);

        // 6. ודא שסה"כ ימים מופיע באופן תקין
        WebElement totalDaysElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalDays")));
        int expectedDays = 30;
        int actualDays = Integer.parseInt(totalDaysElement.getText().trim());
        Assert.assertEquals(actualDays, expectedDays, "סה\"כ ימים לא תואם");

        // 7. לחץ על כפתור "הלאה לפרטי הנוסעים"
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'הלאה לפרטי הנוסעים')]"))).click();

        // 8. וודא שהדף נפתח
        boolean pageLoaded = wait.until(ExpectedConditions.urlContains("travel-policy/insured-details"));
        Assert.assertTrue(pageLoaded, "דף פרטי הנוסעים לא נטען כראוי");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
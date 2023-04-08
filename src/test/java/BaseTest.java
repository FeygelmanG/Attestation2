import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;


public class BaseTest {

    static String units = "units=imperial";
    static String lang = "lang=en";
    static String mode = "mode=json";
    static String appid = "appid=0657c8a7673042ceb3c35ae9b848e5ba";

    @BeforeAll
    public static void setBaseUri() {
        RestAssured.baseURI = "https://api.openweathermap.org/data/2.5/weather";
    }
}

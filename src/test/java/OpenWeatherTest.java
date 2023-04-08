import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class OpenWeatherTest extends BaseTest {
    RequestSpecification requestSpec;
    ResponseSpecification responseOpenWeatherDto;

    @BeforeEach
    public void setUp() {
        requestSpec = RestAssured.given();
        ResponseSpecBuilder specBuilder = new ResponseSpecBuilder()
                .expectStatusCode(200);
        responseOpenWeatherDto = specBuilder.build();
    }

    //test 1
    @Test
    public void shouldGetOpenWeatherByAllParameters() {
        requestSpec
                .get("?q=Kazan&id=551487&lat=49.1221&lon=55.7887&zip=420000" + units + "&" + lang + "&" + mode + "&" + appid)
                .then()
                .spec(responseOpenWeatherDto)
                .body("id", equalTo(551487));
    }

    //test 2
    @ParameterizedTest
    @ValueSource(strings = {"Kazan", "Innopolis", "Arsk"})
    public void shouldGetOpenWeatherByCityName(String city) {
        requestSpec
                .get("?q=" + city + "&" + units + "&" + lang + "&" + mode + "&" + appid)
                .then()
                .spec(responseOpenWeatherDto)
                .body("timezone", equalTo(10800));
    }

    //test 3
    @ParameterizedTest
    @ValueSource(strings = {"Marsel", "Kaibici", "Konstantinopol"})
    public void shouldNotGetOpenWeatherByCityName(String city) {
        RequestSpecification request = RestAssured.given();

        Response response = request.get("?q=" + city + "&" + units + "&" + lang + "&" + mode + "&" + appid);
        response.then()
                .statusCode(404)
                .body("message", containsString("city not found"));
    }

    //test 4
    @Test
    public void shouldNotGetOpenWeatherWithNotParameters() {
        RequestSpecification request = RestAssured.given();

        Response response = request.get("?q" + units + "&" + lang + "&" + mode + "&" + appid);
        response.then()
                .statusCode(400)
                .assertThat()
                .body("message", containsString("Nothing to geocode"));
    }

    //test 5
    @Test
    public void shouldGetOpenWeatherByCoord() {
        requestSpec
                .get("?lat=49.1221&lon=55.7887&" + units + "&" + lang + "&" + mode + "&" + appid)
                .then()
                .spec(responseOpenWeatherDto)
                .body("name", equalTo("Taskopa"), "id", equalTo(608073));
    }

    //test 6
    @Test
    public void shouldGetOpenWeatherByBadCoord() {
        RequestSpecification request = RestAssured.given();

        Response response = request.get("?lat=1000&lon=1000&" + units + "&" + lang + "&" + mode + "&" + appid);
        response.then()
                .statusCode(400)
                .assertThat()
                .body("message", containsString("wrong latitude"));

    }

    //test 7
    @Test
    public void shouldGetOpenWeatherByZip() {
        requestSpec
                .get("?zip=95050" + "&" + units + "&" + lang + "&" + mode + "&" + appid)
                .then()
                .spec(responseOpenWeatherDto)
                .body("name", equalTo("Santa Clara"), "timezone", equalTo(-25200));
    }

    //test 8
    @ParameterizedTest
    @ValueSource(strings = {"Tuluza", "Lion", "Gavre"})
    public void shouldGetOpenWeatherCountryByWithExtracting(String city) {
        String country = requestSpec
                .get("?q=" + city + "&" + units + "&" + lang + "&" + mode + "&" + appid)
                .then()
                .spec(responseOpenWeatherDto)
                .extract()
                .path("sys.country");

        assertThat(country).isEqualTo("FR");
    }

}

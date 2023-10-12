import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.User;
import org.example.UserSteps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class UserLoginParameterizedTest {
    private static String email;
    private static String password;
    private final User user;

    public UserLoginParameterizedTest(User user) {
        this.user = user;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Parameterized.Parameters
    public static Object[][] getUserData() {

        email = RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru";
        password = RandomStringUtils.randomAlphanumeric(10, 20);

        return new Object[][] {
                {new User("", password)},
                {new User(email, "")},
                {new User(email, password)}
        };
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/login for the user login without(or wrong) one of required fields" ) // имя теста
    @Description("User login test for /api/auth/login endpoint without(or wrong) one of required fields")
    public void getUserLoginStatusCodeAndBodyWithoutOrWrongOneParam() {
        Response responseLogin = UserSteps.sendPostRequestUserLogin(user);

        responseLogin.then().assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}

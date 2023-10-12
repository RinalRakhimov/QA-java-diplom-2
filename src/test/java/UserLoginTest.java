import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.User;
import org.example.UserSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest {

    private static String name;
    private static String email;
    private static String password;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        email = RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru";
        password = RandomStringUtils.randomAlphanumeric(10, 20);
        name = RandomStringUtils.randomAlphanumeric(4, 20);
        UserSteps.sendPostRequestUserCreation(new User(email, password, name));
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/login for exist user") // имя теста
    @Description("Basic test for /api/auth/login endpoint")
    public void getStatusCodeAndBodyForExistUserLogin(){
        Response responseLoginOfExistUser = UserSteps.sendPostRequestUserLogin(new User(email, password, null));
        accessToken = responseLoginOfExistUser.then().extract().path("accessToken");

        responseLoginOfExistUser.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user", notNullValue())
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(name));
    }

    @After
    public void dataClear() {
        UserSteps.deleteUser(accessToken);
    }
}

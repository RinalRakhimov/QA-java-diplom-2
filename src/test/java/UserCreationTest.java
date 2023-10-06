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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class UserCreationTest {

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
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/register for new user") // имя теста
    @Description("Basic test for /api/auth/register endpoint")
    public void getNewUserSuccessCreationStatusCodeAndBody(){
        Response responseNewUserCreation = UserSteps.sendPostRequestUserCreation(new User(email, password, name));
        responseNewUserCreation.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user", notNullValue())
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        accessToken = responseNewUserCreation.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/register for the already existing user") // имя теста
    @Description("Already existing user creation test for /api/auth/register endpoint")
    public void getAlreadyExistingUserCreationStatusCodeAndBody() {
        Response responseNewUserCreation = UserSteps.sendPostRequestUserCreation(new User(email, password, name));

        Response responseAlreadyExistingUserCreation = UserSteps.sendPostRequestUserCreation(new User(email, password, name));

        responseAlreadyExistingUserCreation.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));

        accessToken = responseNewUserCreation.then().extract().path("accessToken");
    }

    @After
    public void dataClear() {
        UserSteps.deleteUser(accessToken);
    }
   }

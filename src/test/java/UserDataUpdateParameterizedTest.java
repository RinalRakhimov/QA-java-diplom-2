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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class UserDataUpdateParameterizedTest {

    private static String email;
    private static String password;
    private static String name;

    private final User user;
    private String accessToken;

    public UserDataUpdateParameterizedTest(User user) {
        this.user = user;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        email = RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru";
        password = RandomStringUtils.randomAlphanumeric(10, 20);
        name = RandomStringUtils.randomAlphanumeric(4, 20);
        Response responseNewUserCreation = UserSteps.sendPostRequestUserCreation(new User(email, password, name));
        accessToken = responseNewUserCreation.then().extract().path("accessToken");
        System.out.println(responseNewUserCreation.body().asString());
    }

    @Parameterized.Parameters
    public static Object[][] getUserData() {

        return new Object[][] {
                {new User("oblako1234@mail.ru", null, name)},
                {new User(email, null, "charlie")},
                {new User(RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru", null, "charlie")}
        };
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/register for new user with authorization") // имя теста
    @Description("User data update test for /api/auth/register endpoint")
    public void setNewUserDataWithAuthorization() {
        Response responseUpdateUserData = UserSteps.updateUserDataWithAuthorization(accessToken, user);
        Response getUpdateUserData = UserSteps.getUserData(accessToken);

        responseUpdateUserData.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(getUpdateUserData.then().extract().path("user.email")))
                .body("user.name", equalTo(getUpdateUserData.then().extract().path("user.name")));

        System.out.println(responseUpdateUserData.body().asString());

    }

    @Test
    @DisplayName("Check status code and body of /api/auth/register for new user without authorization") // имя теста
    @Description("User data update test for /api/auth/register endpoint")
    public void setNewUserDataWithoutAuthorization() {
        Response responseUpdateUserData = UserSteps.updateUserDataWithoutAuthorization(user);

        responseUpdateUserData.then().assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));

        System.out.println(responseUpdateUserData.body().asString());
    }

    @After
    public void dataClear() {
        UserSteps.deleteUser(accessToken);
    }
}

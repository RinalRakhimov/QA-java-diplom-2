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
import static org.hamcrest.Matchers.equalTo;

public class UserDataUpdateWithRepeatEmailTest {


    private static String emailFirstUser;
    private static String passwordFirstUser;
    private static String nameFirstUser;

    private static String emailSecondUser;
    private static String passwordSecondUser;
    private static String nameSecondUser;
    private String accessTokenFirstUser;
    private String accessTokenSecondUser;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/user for user data updating with already used email") // имя теста
    @Description("User data update test for /api/auth/user endpoint")
    public void setUserAlreadyUsedEmail() {
        emailFirstUser = RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru";
        passwordFirstUser = RandomStringUtils.randomAlphanumeric(10, 20);
        nameFirstUser = RandomStringUtils.randomAlphanumeric(4, 20);
        Response responseNewFirstUserCreation = UserSteps.sendPostRequestUserCreation(new User(emailFirstUser, passwordFirstUser, nameFirstUser));
        accessTokenFirstUser = responseNewFirstUserCreation.then().extract().path("accessToken");

        emailSecondUser = RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru";
        passwordSecondUser = RandomStringUtils.randomAlphanumeric(10, 20);
        nameSecondUser = RandomStringUtils.randomAlphanumeric(4, 20);
        Response responseNewSecondUserCreation = UserSteps.sendPostRequestUserCreation(new User(emailSecondUser, passwordSecondUser, nameSecondUser));
        accessTokenSecondUser = responseNewSecondUserCreation.then().extract().path("accessToken");

        Response responseUpdateSecondUserData = UserSteps.updateUserDataWithAuthorization(accessTokenSecondUser, new User(emailFirstUser, null, nameSecondUser));

        responseUpdateSecondUserData.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));

        System.out.println(responseUpdateSecondUserData.body().asString());
    }

    @After
    public void dataClear() {
        UserSteps.deleteUser(accessTokenFirstUser);
        UserSteps.deleteUser(accessTokenSecondUser);
    }
}

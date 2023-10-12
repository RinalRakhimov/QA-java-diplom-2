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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class UserCreationParameterizedTest {
    private static String email;
    private static String password;
    private static String name;

    public UserCreationParameterizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = "Создание пользователя. Тестовые данные: {0}, {1}, {2}")
    public static Object[][] getUserData() {

        email = RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru";
        password = RandomStringUtils.randomAlphanumeric(10, 20);
        name = RandomStringUtils.randomAlphanumeric(4, 20);

        return new Object[][] {
                {"", password, name},
                {email, "", name},
                {email, password, ""}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/register for the user registration without one of required fields" ) // имя теста
    @Description("User creation test for /api/auth/register endpoint without one of required fields")
    public void getUserCreationStatusCodeAndBodyWithoutOneParam() {
        Response responseNewUserCreation = UserSteps.sendPostRequestUserCreation(new User(email, password, name));

        responseNewUserCreation.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}

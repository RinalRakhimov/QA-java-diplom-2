import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.Order;
import org.example.OrderSteps;
import org.example.User;
import org.example.UserSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class GetOrderOfUserTest {


    private static String email;
    private static String password;
    private static String name;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        email = RandomStringUtils.randomAlphanumeric(6, 10) + "@yandex.ru";
        name = RandomStringUtils.randomAlphanumeric(4, 20);
        password = RandomStringUtils.randomAlphanumeric(10, 20);
    }

    @Test
    @DisplayName("Check status code and body of /api/orders for order receiving with user authorization") // имя теста
    @Description("Order receiving with user authorization")
    public void getOrderWithAuthorizationAndIngredients() {
        UserSteps.sendPostRequestUserCreation(new User(email, password, name));
        Response responseNewUserLogin = UserSteps.sendPostRequestUserLogin(new User(email, password, null));
        accessToken = responseNewUserLogin.then().extract().path("accessToken");
        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72"};
        OrderSteps.sendPostRequestOrderCreationWithAuthorization(accessToken, new Order(ingredients));
        Response responseGetOrdersOfAuthorizedUser = OrderSteps.sendGetOrderRequestForAuthorizedUser(accessToken);

        responseGetOrdersOfAuthorizedUser.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Check status code and body of /api/orders for order receiving with user authorization") // имя теста
    @Description("Order receiving with user authorization")
    public void getOrderWithUnauthorization() {
        Response responseNewUserCreation = UserSteps.sendPostRequestUserCreation(new User(email, password, name));
        accessToken = responseNewUserCreation.then().extract().path("accessToken");
        Response responseGetOrdersOfUnauthorizedUser = OrderSteps.sendGetOrderRequestForUnauthorizedUser();

        responseGetOrdersOfUnauthorizedUser.then().assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void dataClear() {
        UserSteps.deleteUser(accessToken);
    }
}

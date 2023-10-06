import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class OrderCreationTest {

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
    @DisplayName("Check status code and body of /api/orders for order creation with authorization and ingredients") // имя теста
    @Description("Order creation with authorization and ingredients")
    public void setOrderWithAuthorizationAndIngredients() {
        UserSteps.sendPostRequestUserCreation(new User(email, password, name));
        Response responseNewUserLogin = UserSteps.sendPostRequestUserLogin(new User(email, password, null));
        accessToken = responseNewUserLogin.then().extract().path("accessToken");
        
        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72"};
        Response responseNewOrderCreation = OrderSteps.sendPostRequestOrderCreationWithAuthorization(accessToken, new Order(ingredients));

        responseNewOrderCreation.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", notNullValue())
                .body("order", notNullValue())
                .body("order.number", notNullValue())
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check status code and body of /api/orders for order creation with authorization and without ingredients") // имя теста
    @Description("Order creation with authorization and without ingredients")
    public void setOrderWithAuthorizationAndNoIngredients() {
        UserSteps.sendPostRequestUserCreation(new User(email, password, name));
        Response responseNewUserLogin = UserSteps.sendPostRequestUserLogin(new User(email, password, null));
        accessToken = responseNewUserLogin.then().extract().path("accessToken");

        String[] ingredients = {};
        Response responseNewOrderCreation = OrderSteps.sendPostRequestOrderCreationWithAuthorization(accessToken, new Order(ingredients));

        responseNewOrderCreation.then().assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Check status code and body of /api/orders for order creation with authorization and not correct ingredients") // имя теста
    @Description("Order creation with authorization and not correct ingredients")
    public void setOrderWithAuthorizationAndNotCorrectIngredients() {
        UserSteps.sendPostRequestUserCreation(new User(email, password, name));
        Response responseNewUserLogin = UserSteps.sendPostRequestUserLogin(new User(email, password, null));
        accessToken = responseNewUserLogin.then().extract().path("accessToken");

        String[] ingredients = {"123", "abc", "123abc"};
        Response responseNewOrderCreation = OrderSteps.sendPostRequestOrderCreationWithAuthorization(accessToken, new Order(ingredients));

        responseNewOrderCreation.then().assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void dataClear() {
       UserSteps.deleteUser(accessToken);
    }
}

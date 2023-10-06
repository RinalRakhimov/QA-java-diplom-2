import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Order;
import org.example.OrderSteps;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class OrderCreationNegativeTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Check status code and body of /api/orders for order creation with authorization and ingredients") // имя теста
    @Description("Order creation with authorization and ingredients")
    public void setOrderWithoutAuthorizationAndWithIngredients() {

        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72"};
        Response responseNewOrderCreation = OrderSteps.sendPostRequestOrderCreationWithoutAuthorization(new Order(ingredients));

        responseNewOrderCreation.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", notNullValue())
                .body("order", notNullValue())
                .body("order.number", notNullValue())
                .body("success", equalTo(true));
    }
}

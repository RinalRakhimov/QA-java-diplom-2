package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    private static final String ORDER_ENDPOINT = "/api/orders";

    @Step("Send POST request to /api/orders with authorization")
    @Description("Order creation")
    public static Response sendPostRequestOrderCreationWithAuthorization(String accessToken, Object ingredients){
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(ingredients)
                .post(ORDER_ENDPOINT);
    }
    @Step("Send GET request to /api/orders without authorization")
    @Description("Order creation")
    public static Response sendPostRequestOrderCreationWithoutAuthorization(Object ingredients){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(ingredients)
                .post(ORDER_ENDPOINT);
    }
    @Step("Send GET request to /api/orders for authorized user")
    @Description("Order getting for authorized user")
    public static Response sendGetOrderRequestForAuthorizedUser(String accessToken){
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .get(ORDER_ENDPOINT);
    }
    @Step("Send GET request to /api/orders for unauthorized user")
    @Description("Order getting for unauthorized user")
    public static Response sendGetOrderRequestForUnauthorizedUser(){
        return given()
                .header("Content-type", "application/json")
                .and()
                .get(ORDER_ENDPOINT);
    }
}

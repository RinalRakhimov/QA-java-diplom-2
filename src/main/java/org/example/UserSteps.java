package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserSteps {

    private static final String CREATION_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String USER_GET_UPDATE_DELETE_ENDPOINT = "/api/auth/user";

    @Step("Send POST request to /api/auth/register")
    @Description("User creation")
    public static Response sendPostRequestUserCreation(Object json){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .post(CREATION_ENDPOINT);
    }

    @Step("Send POST request to /api/auth/login")
    @Description("User login")
    public static Response sendPostRequestUserLogin(Object json){
        return given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .post(LOGIN_ENDPOINT);
    }

    @Step("Get user data by accessToken")
    @Description("Sent GET request on /api/auth/user")
    public static Response getUserData(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .get(USER_GET_UPDATE_DELETE_ENDPOINT);
    }

    @Step("Update user data by accessToken")
    @Description("Sent PATCH request on /api/auth/user")
    public static Response updateUserDataWithAuthorization(String accessToken, Object json) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .patch(USER_GET_UPDATE_DELETE_ENDPOINT);
    }

    @Step("Update user data by accessToken")
    @Description("Sent PATCH request on /api/auth/user")
    public static Response updateUserDataWithoutAuthorization(Object json) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .patch(USER_GET_UPDATE_DELETE_ENDPOINT);
    }

    @Step("Delete courier by accessToken")
    @Description("Sent DELETE request on /api/auth/user")
    public static void deleteUser(String accessToken) {
        given()
                .header("Authorization", accessToken)
                .delete(USER_GET_UPDATE_DELETE_ENDPOINT)
                .then()
                .statusCode(SC_ACCEPTED)
                .and()
                .body("success", equalTo(true))
                .body("message", equalTo("User successfully removed"));
        }
}

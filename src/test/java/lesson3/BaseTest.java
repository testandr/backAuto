package lesson3;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class BaseTest {
    static Properties properties = new Properties();
    static String token;
    static String username;
    static ResponseSpecification positiveResponseSpecification;
    static ResponseSpecification negativeResponseSpecification;
    static RequestSpecification requestAuth;



    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
//        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "https://api.imgur.com/3";
        getProperties();
        token = properties.getProperty("token");
        username = properties.getProperty("username");

        positiveResponseSpecification = new ResponseSpecBuilder()
                .expectBody("status", equalTo(200))
                .expectBody("success", is(true))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

        negativeResponseSpecification = new ResponseSpecBuilder()
                .expectBody("status", equalTo(400))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(400)
                .build();

        requestAuth = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .build();
    }

    private static void getProperties(){
        try (InputStream output = new FileInputStream("src/test/resources/resources.properties")) {
            properties.load(output);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

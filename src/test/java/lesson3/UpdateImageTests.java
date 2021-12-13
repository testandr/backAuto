package lesson3;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;

import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static lesson4.EndPoints.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class UpdateImageTests extends BaseTest{

    private MultiPartSpecification base64MultiPartSpecification;
    static RequestSpecification requestSpecificationWithAuthWithBase64;
    private Response response;
    private String imageId;
    private String deleteHash;
    private String encodedFile;
    static String pathToImage;
    static String linkToImage;
    static String pathToFile;


    @BeforeEach
    void setUp(){
        pathToImage = properties.getProperty("pathToImage");
        linkToImage = properties.getProperty("linkToImage");
        pathToFile = properties.getProperty("pathToFile");

        byte[] byteArray = getFileContent(pathToImage);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpecification = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(base64MultiPartSpecification)
                .build();

        response = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response();

        imageId = response.jsonPath().getString("data.id");
        deleteHash = response.jsonPath().getString("data.deletehash");
    }

    @DisplayName("Изменение title")
    @Test
    void updateFileTitle(){
        given()
                .headers("Authorization", token)
                .param("title", "Test")
                .expect()
                .statusCode(200)
                .when()
                .put(UPLOAD_IMAGE + IMAGE_HASH, imageId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response();

        String givenTitle = given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .when()
                .get(UPLOAD_IMAGE + IMAGE_HASH, imageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.title");

        assertThat("Title", givenTitle, equalTo("Test"));
    }




    private  byte[] getFileContent(String imagePath) {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(imagePath));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return  byteArray;
    }
}

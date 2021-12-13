package lesson3;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static lesson4.EndPoints.UPLOAD_IMAGE;

public class DeleteFile extends BaseTest{

    private MultiPartSpecification base64MultiPartSpecification;
    static RequestSpecification requestSpecificationWithAuthWithBase64;
    static RequestSpecification requestSpecificationWithAuthWithLink;
    static RequestSpecification requestSpecificationWithAuthWithFile;

    private String imageId;
    private String deleteHashBase64;
    private String deleteHashLink;
    private String encodedFile;
    private Response responseBase64;
    private Response responseLink;

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

        requestSpecificationWithAuthWithLink = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart("image", linkToImage)
                .build();

        requestSpecificationWithAuthWithFile = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart("image", pathToFile)
                .build();

        responseBase64 = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response();

        deleteHashBase64 = responseBase64.jsonPath().getString("data.deletehash");

        responseLink = given(requestSpecificationWithAuthWithLink, positiveResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response();

        deleteHashLink = responseLink.jsonPath().getString("data.deletehash");

    }



    @Test
    @Order(1)
    void deleteFileBase64() {
        given(requestAuth)
                .delete(UPLOAD_IMAGE + deleteHashBase64)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    void deleteFileLink() {
        given(requestAuth)
                .delete(UPLOAD_IMAGE + deleteHashLink)
                .then()
                .statusCode(200);
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

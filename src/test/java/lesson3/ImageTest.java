package lesson3;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static org.hamcrest.CoreMatchers.is;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageTest extends BaseTest{
    private final String PATH_TO_IMAGE = "src/test/resources/Image1.jpg";
    private final String PATH_TO_FILE = "src/test/resources/fortest.txt";
    private final String LINK_TO_IMAGE = "https://klike.net/uploads/posts/2019-05/1556708032_1.jpg";

    static String encodedFile;
    String uploadedImageId;


    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
    }

    @Test()
    @Order(1)
    void uploadFileBase64() {
        uploadedImageId =
            given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
            .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
            .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
            .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    @Order(2)
    void getImage() {
        given()
            .headers("Authorization", token)
        .expect()
            .body("success", is(true))
        .when()
            .get("https://api.imgur.com/3/image/" + uploadedImageId)
            .prettyPeek()
        .then()
            .extract()
            .response()
            .jsonPath();
    }

    @Test
    @Order(3)
    void deleteFile() {
        given()
            .headers("Authorization", token)
            .multiPart("image", new File(PATH_TO_IMAGE))
        .when()
            .delete("https://api.imgur.com/3/image" + uploadedImageId)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(4)
    void uploadFileLink() {
        uploadedImageId =
            given()
                .headers("Authorization", token)
                .multiPart("image", LINK_TO_IMAGE)
            .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
            .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
            .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }

    @Test
    @Order(5)
    void uploadFileNotAnImage() {
        uploadedImageId =
            given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_FILE))
            .expect()
                .statusCode(400)
            .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
            .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }

    @Test
    @Order(6)
    void uploadFileEmpty() {
        given()
            .headers("Authorization", token)
        .expect()
            .statusCode(400)
        .when()
            .post("https://api.imgur.com/3/image")
            .prettyPeek()
        .then()
            .extract()
            .response()
            .jsonPath()
            .getString("data.id");
    }

    @Test
    @Order(7)
    void uploadFile() {
        uploadedImageId =
            given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
            .expect()
                .statusCode(200)
            .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
            .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }




    private  byte[] getFileContent() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return  byteArray;
    }
}

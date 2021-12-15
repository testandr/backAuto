package lesson5;

import com.github.javafaker.Faker;
import lesson5.dto.Category;
import lesson5.dto.Product;
import lesson5.enums.CategoryType;
import lesson5.service.CategoryService;
import lesson5.service.ProductService;
import lesson5.utils.RetrofitUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;


import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class ProductTests {
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    Faker faker = new Faker();
    Product product;
    int lastItemAddedId;

    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().dish())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());
    }

    @Test
    void postProductTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        System.out.println(product.getTitle());
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        lastItemAddedId = response.body().getId();
        System.out.println("postProductTest" + lastItemAddedId);
    }


    @Test
    void getProductsListTest() throws IOException {
        Response<ArrayList<Product>> response = productService.getProducts().execute();
        assertThat(response.body().size(), notNullValue());
        boolean status = false;
        for(int i=0;i<=response.body().size()-1;i++){
            System.out.println(response.body().get(i).getId());
            if(response.body().get(i).getId() == lastItemAddedId){
                status = true;
            }
        }
        assertThat(status, equalTo(true));
    }


    @Test
    void getProductWithBadIdTest() throws IOException {
        Response<Product> response = productService.getProduct(lastItemAddedId).execute();
        assertThat(response.toString(), containsString("code=404"));
    }


    @Test
    void deleteProductWithBadIdTest() throws IOException {
        Response<Product> response = productService.deleteProduct(111111).execute();
        assertThat(response.toString(), containsString("code=500"));
    }

    @Test
    void deleteProductWithIdTest() throws IOException {
        Response<Product> response = productService.deleteProduct(lastItemAddedId).execute();
        assertThat(response.toString(), containsString("code=200"));
    }

    @Test
    void getCategoryByIdTest() throws IOException {
        Integer id = CategoryType.FOOD.getId();
        Response<Category> response = categoryService
                .getCategory(id)
                .execute();
//        log.info(response.body().toString());
        assertThat(response.body().getTitle(), equalTo(CategoryType.FOOD.getTitle()));
        assertThat(response.body().getId(), equalTo(id));
    }
}

package lesson6;

import com.github.javafaker.Faker;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import lesson6.db.dao.ProductsMapper;
import lesson6.dto.Product;
import lesson6.enums.CategoryType;
import lesson6.service.CategoryService;
import lesson6.service.ProductService;
import lesson6.utils.DbUtils;
import lesson6.utils.RetrofitUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProductTests {
    int productId;
    static ProductsMapper productsMapper;
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    Faker faker = new Faker();
    Product product;

    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
        productsMapper = DbUtils.getProductsMapper();
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
        Integer countProductsBefore = DbUtils.countProducts(productsMapper);
        Response<Product> response = productService.createProduct(product).execute();
        Integer countProductsAfter = DbUtils.countProducts(productsMapper);
        assertThat(countProductsAfter, equalTo(countProductsBefore+1));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        productId = response.body().getId();
    }

    @Test
    void getProductWithBadIdTest() throws IOException {
        Integer countProductsBefore = DbUtils.countProducts(productsMapper);
        Response<ResponseBody> response = productService.deleteProduct(productId).execute();
        Integer countProductsAfter = DbUtils.countProducts(productsMapper);
        assertThat(response.toString(), containsString("code=200"));
    }

//    @Test
//    void getCategoryByIdTest() throws IOException {
//        Integer id = CategoryType.FOOD.getId();
//        Response<Category> response = categoryService
//                .getCategory(id)
//                .execute();
////        log.info(response.body().toString());
//        assertThat(response.body().getTitle(), equalTo(CategoryType.FOOD.getTitle()));
//        assertThat(response.body().getId(), equalTo(id));
//    }

    @AfterEach
    void tearDown() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(productId).execute();
        assertThat(response.isSuccessful(), is(true));
    }
}

package cart.controller;

import cart.dao.ItemDao;
import cart.dto.ItemRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static cart.Pixture.CREATE_ITEM1;
import static cart.Pixture.CREATE_ITEM2;
import static org.hamcrest.core.IsEqual.equalTo;

@Sql({"classpath:test_init.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminControllerTest {

    @Autowired
    private ItemDao itemDao;

    @BeforeEach
    void setUp(@LocalServerPort int port) {
        RestAssured.port = port;
        itemDao.save(CREATE_ITEM1);
        itemDao.save(CREATE_ITEM2);
    }

    @DisplayName("상품 추가 테스트")
    @Test
    void addItemTest() {
        //given
        ItemRequest itemRequest = new ItemRequest("국밥", "c", 30000);

        //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemRequest)
                .when().post("/admin/items/new")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .body(equalTo("ok"));
    }

    @Test
    @DisplayName("상품 수정 테스트")
    void updateItemTest() {
        //given
        ItemRequest itemRequest = new ItemRequest("국밥", "c", 30000);

        //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemRequest)
                .when().post("/admin/items/edit/1")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .body(equalTo("ok"));
    }

    @Test
    @DisplayName("상품 삭제 테스트")
    void deleteItemTest() {
        //then
        RestAssured.given().log().all()
                .when().post("/admin/items/delete/1")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .body(equalTo("ok"));
    }

    @ParameterizedTest
    @DisplayName("상품명에 공백이 들어갔을 경우 테스트")
    @ValueSource(strings = {"", "    "})
    void newItemNameBlankTest(String name) {
        //given
        String message = "상품명은 공백일 수 없습니다.";
        ItemRequest itemRequest = new ItemRequest(name, "c", 10000);

        //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemRequest)
                .when().post("/admin/items/new")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("name", equalTo(message));
    }

    @Test
    @DisplayName("상품명의 길이가 30을 넘는 경우 테스트")
    void newItemNameLengthOverSizeTest() {
        //given
        String name = "ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ";
        String message = "상품명의 길이는 30자 이하여야합니다.";
        ItemRequest itemRequest = new ItemRequest(name, "c", 10000);

        //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemRequest)
                .when().post("/admin/items/new")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("name", equalTo(message));
    }

    @ParameterizedTest
    @DisplayName("상품 이미지 url에 공백이 들어간 경우 테스트")
    @ValueSource(strings = {"", "    "})
    void newItemImageUrlFailTest(String url) {
        //given
        String message = "이미지 url은 공백일 수 없습니다.";
        ItemRequest itemRequest = new ItemRequest("name", url, 10000);

        //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemRequest)
                .when().post("/admin/items/new")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("imageUrl", equalTo(message));
    }

    @Test
    @DisplayName("상품 가격에 공백이 들어간 경우 테스트")
    void newItemPriceBlackFailTest() {
        //given
        Integer price = null;
        String message = "가격은 공백일 수 없습니다.";
        ItemRequest itemRequest = new ItemRequest("국밥", "c", price);

        //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemRequest)
                .when().post("/admin/items/new")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("price", equalTo(message));
    }

    @Test
    @DisplayName("상품 가격이 0원 이하인 경우 테스트")
    void newItemPriceNegativeFailTest() {
        //given
        Integer price = 0;
        String message = "가격은 최소 1원 이상이어야합니다.";
        ItemRequest itemRequest = new ItemRequest("국밥", "c", price);

        //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemRequest)
                .when().post("/admin/items/new")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("price", equalTo(message));
    }

    @Test
    @DisplayName("상품 가격이 100만원 이상인 경우 테스트")
    void newItemPriceOver1MFailTest() {
        //given
        Integer price = 1000001;
        String message = "가격은 최대 100만원 이하여야합니다.";
        ItemRequest itemRequest = new ItemRequest("국밥", "c", price);

        //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemRequest)
                .when().post("/admin/items/new")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("price", equalTo(message));
    }

    @Test
    @DisplayName("상품 번호가 없는 경우 수정 테스트")
    void updateInvalidItemIdFailTest() {
        //given
        ItemRequest itemRequest = new ItemRequest("국밥", "c", 30000);
        String message = "잘못된 상품 번호를 입력하셨습니다.";

        //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemRequest)
                .when().post("/admin/items/edit/3")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("message", equalTo(message));
    }

    @Test
    @DisplayName("상품 번호가 없는 경우 삭제 테스트")
    void deleteInvalidItemIdFailTest() {
        //given
        String message = "잘못된 상품 번호를 입력하셨습니다.";

        //then
        RestAssured.given().log().all()
                .when().post("/admin/items/delete/3")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("message", equalTo(message));
    }
}

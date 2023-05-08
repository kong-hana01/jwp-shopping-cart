package cart.service;

import cart.dao.ItemDao;
import cart.dao.MemberDao;
import cart.dto.AuthorizationInformation;
import cart.dto.ItemResponse;
import cart.exception.ServiceIllegalArgumentException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static cart.Pixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql({"classpath:test_init.sql"})
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private MemberDao memberDao;

    @BeforeEach
    void setUp() {
        itemDao.save(CREATE_ITEM1);
        itemDao.save(CREATE_ITEM2);

        memberDao.save(AUTH_MEMBER1);

        AuthorizationInformation authorizationInformation = new AuthorizationInformation(AUTH_MEMBER1.getEmail(), AUTH_MEMBER1.getPassword());
        cartService.putItemIntoCart(1L, authorizationInformation);
    }


    @DisplayName("장바구니에 상품을 추가할 수 있다.")
    @Test
    void putItemIntoCart_success() {
        AuthorizationInformation authorizationInformation = new AuthorizationInformation(AUTH_MEMBER1.getEmail(), AUTH_MEMBER1.getPassword());
        cartService.putItemIntoCart(2L, authorizationInformation);
    }

    @DisplayName("없는 상품을 장바구니에 추가할 수 없다.")
    @Test
    void putItemIntoCart_fail_invalidItem() {
        AuthorizationInformation authorizationInformation = new AuthorizationInformation(AUTH_MEMBER1.getEmail(), AUTH_MEMBER1.getPassword());

        assertThatThrownBy(() -> cartService.putItemIntoCart(3L, authorizationInformation))
                .isInstanceOf(ServiceIllegalArgumentException.class)
                .hasMessage("상품 정보를 다시 입력해주세요.");
    }

    @DisplayName("장바구니에 이미 상품을 담은 경우 상품을 추가할 수 없다.")
    @Test
    void save_fail() {
        AuthorizationInformation authorizationInformation = new AuthorizationInformation(AUTH_MEMBER1.getEmail(), AUTH_MEMBER1.getPassword());

        Assertions.assertThatThrownBy(() -> cartService.putItemIntoCart(1L, authorizationInformation))
                .isInstanceOf(ServiceIllegalArgumentException.class)
                .hasMessage("이미 장바구니에 담은 상품입니다.");
    }

    @DisplayName("올바르지 않은 사람에 대한 장바구니에 상품을 추가할 수 없다.")
    @Test
    void putItemIntoCart_fail_invalidMember() {
        AuthorizationInformation authorizationInformation = new AuthorizationInformation(AUTH_MEMBER2.getEmail(), AUTH_MEMBER2.getPassword());

        assertThatThrownBy(() -> cartService.putItemIntoCart(1L, authorizationInformation))
                .isInstanceOf(ServiceIllegalArgumentException.class)
                .hasMessage("email과 password를 다시 입력해주세요.");
    }

    @DisplayName("장바구니에 있는 상품을 조회할 수 있다.")
    @Test
    void findAllItemByAuthInfo_success() {
        AuthorizationInformation authorizationInformation = new AuthorizationInformation(AUTH_MEMBER1.getEmail(), AUTH_MEMBER1.getPassword());

        List<ItemResponse> itemResponses = cartService.findAllItemByAuthInfo(authorizationInformation);

        ItemResponse expected = new ItemResponse(ITEM1.getId(), ITEM1.getName(), ITEM1.getImageUrl(), ITEM1.getPrice());
        assertAll(
                () -> assertThat(itemResponses).hasSize(1),
                () -> assertThat(itemResponses.get(0))
                        .usingRecursiveComparison()
                        .isEqualTo(expected)
        );
    }

    @DisplayName("장바구니에 있는 상품을 삭제할 수 있다.")
    @Test
    void deleteItemByItemId_success() {
        AuthorizationInformation authorizationInformation = new AuthorizationInformation(AUTH_MEMBER1.getEmail(), AUTH_MEMBER1.getPassword());

        cartService.deleteItemFromCart(1L, authorizationInformation);

        List<ItemResponse> itemResponses = cartService.findAllItemByAuthInfo(authorizationInformation);

        assertThat(itemResponses).isEmpty();
    }

    @DisplayName("장바구니에 없는 상품을 삭제하면 예외가 발생한다.")
    @Test
    void deleteItemByItemId_fail() {
        AuthorizationInformation authorizationInformation = new AuthorizationInformation(AUTH_MEMBER1.getEmail(), AUTH_MEMBER1.getPassword());

        assertThatThrownBy(() -> cartService.deleteItemFromCart(2L, authorizationInformation))
                .isInstanceOf(ServiceIllegalArgumentException.class)
                .hasMessage("상품 정보를 다시 입력해주세요.");
    }
}

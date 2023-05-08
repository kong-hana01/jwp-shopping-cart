package cart.dao;

import cart.entity.Cart;
import cart.entity.PutCart;
import cart.exception.DaoDuplicateException;
import cart.exception.ServiceIllegalArgumentException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcCartDao implements CartDao {

    private static final String EXISTS_ITEM_IN_CART_MESSAGE = "이미 장바구니에 담은 상품입니다.";

    private final JdbcTemplate jdbcTemplate;

    public JdbcCartDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(PutCart putCart) {
        if (isCartExists(putCart)) {
            throw new DaoDuplicateException(EXISTS_ITEM_IN_CART_MESSAGE);
        }
        String sql = "insert into cart(member_id, item_id) values(?, ?)";

        jdbcTemplate.update(sql, putCart.getMemberId(), putCart.getProductId());
    }

    @Override
    public List<Cart> findAllByMemberId(Long id) {
        String sql = "select id, member_id, item_id from cart where member_id = ?";

        return jdbcTemplate.query(sql, mapRow(), id);
    }

    private RowMapper<Cart> mapRow() {
        return (rs, rowNum) -> {
            Long id = rs.getLong(1);
            Long memberId = rs.getLong(2);
            Long itemId = rs.getLong(3);

            return new Cart(id, memberId, itemId);
        };
    }

    @Override
    public boolean isCartExists(PutCart putCart) {
        String sql = "select exists(select id from cart where member_id = ? and item_id = ?)";

        return jdbcTemplate.queryForObject(sql, Boolean.class, putCart.getMemberId(), putCart.getProductId());
    }

    @Override
    public int delete(PutCart putCart) {
        String sql = "delete from cart where member_id = ? and item_id = ?";

        return jdbcTemplate.update(sql, putCart.getMemberId(), putCart.getProductId());
    }
}

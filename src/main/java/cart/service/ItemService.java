package cart.service;

import cart.dao.ItemDao;
import cart.dto.ItemRequest;
import cart.dto.ItemResponse;
import cart.entity.CreateItem;
import cart.entity.Item;
import cart.exception.ServiceIllegalArgumentException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private static final int ZERO_AFFECTED_ROW = 0;
    private static final String INVALID_ITEM_ID_MESSAGE = "잘못된 상품 번호를 입력하셨습니다.";

    private final ItemDao itemDao;

    public ItemService(final ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public void save(ItemRequest itemRequest) {
        CreateItem createItem = itemRequest.toCreateItem();

        itemDao.save(createItem);
    }

    public List<ItemResponse> findAll() {
        List<Item> items = itemDao.findAll();
        return convertItemsToItemResponses(items);
    }

    private List<ItemResponse> convertItemsToItemResponses(final List<Item> items) {
        return items.stream()
                .map(Item::toItemResponse)
                .collect(Collectors.toList());
    }

    public void updateItem(Long itemId, ItemRequest itemRequest) {
        CreateItem createItem = itemRequest.toCreateItem();

        int updatedRow = itemDao.update(itemId, createItem);
        validateItemId(updatedRow);
    }

    private void validateItemId(int changedRow) {
        if (isInvalidItemId(changedRow)) {
            throw new ServiceIllegalArgumentException(INVALID_ITEM_ID_MESSAGE);
        }
    }

    private boolean isInvalidItemId(int changedRow) {
        return changedRow == ZERO_AFFECTED_ROW;
    }

    public void deleteItem(Long itemId) {
        int deleteRow = itemDao.delete(itemId);
        validateItemId(deleteRow);
    }
}

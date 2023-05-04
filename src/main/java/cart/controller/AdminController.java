package cart.controller;

import cart.dto.ItemRequest;
import cart.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ItemService itemService;

    public AdminController(final ItemService itemService) {
        this.itemService = itemService;
    }


    @GetMapping
    @ModelAttribute
    public String displayItemList(Model model) {
        model.addAttribute("products", itemService.findAll());
        return "admin";
    }

    @PostMapping("/items/new")
    @ResponseStatus(HttpStatus.CREATED)
    public String addItem(@Valid @RequestBody ItemRequest itemRequest) {
        itemService.save(itemRequest);
        return "ok";
    }

    @PostMapping("/items/edit/{itemId}")
    public String editItem(@PathVariable Long itemId, @Valid @RequestBody ItemRequest itemRequest) {
        itemService.updateItem(itemId, itemRequest);
        return "ok";
    }

    @PostMapping("/items/delete/{itemId}")
    public String deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return "ok";
    }
}

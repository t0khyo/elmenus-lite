package spring.practice.elmenus_lite.conroller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.request.MenuItemCreateRequest;
import spring.practice.elmenus_lite.dto.request.MenuItemUpdateRequest;
import spring.practice.elmenus_lite.dto.response.MenuItemResponse;
import spring.practice.elmenus_lite.service.MenuItemService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping("/menus/{menuId}/items")
    public ResponseEntity<Map<String, Integer>> createMenuItem(
            @PathVariable Integer menuId,
            @Valid @RequestBody MenuItemCreateRequest request) {
        Integer menuItemId = menuItemService.createMenuItem(menuId, request);
        return new ResponseEntity<>(Map.of("menuItemId", menuItemId), HttpStatus.CREATED);
    }


    @GetMapping("/restaurants/{restaurantId}/items")
    public ResponseEntity<Page<MenuItemResponse>> getMenuItemsByRestaurantId(
            @PathVariable Integer restaurantId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DecimalMin(value = "0.01", message = "Min price must be at least 0.01") BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MenuItemResponse> menuItems = menuItemService.getMenuItemsByRestaurantId(
                restaurantId, name, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(menuItems);
    }


    @GetMapping("/menu-items/search")
    public ResponseEntity<Page<MenuItemResponse>> searchMenuItems(
            @RequestParam @NotBlank(message = "Keyword cannot be empty") String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MenuItemResponse> menuItems = menuItemService.searchMenuItems(
                keyword, pageable);
        return ResponseEntity.ok(menuItems);
    }


    @PutMapping("/menu-items/{menuItemId}")
    public ResponseEntity<Void> updateMenuItem(
            @PathVariable Integer menuItemId,
            @Valid @RequestBody MenuItemUpdateRequest request) {
        menuItemService.updateMenuItem(menuItemId, request);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/menu-items/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Integer menuItemId) {
        menuItemService.deleteMenuItem(menuItemId);
        return ResponseEntity.ok().build();
    }
}

package spring.practice.elmenus_lite.conroller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.request.MenuCreateRequest;
import spring.practice.elmenus_lite.dto.request.MenuUpdateRequest;
import spring.practice.elmenus_lite.dto.response.MenuResponse;
import spring.practice.elmenus_lite.service.MenuService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }


    @PostMapping("/restaurants/{restaurantId}/menus")
    public ResponseEntity<Map<String, Integer>> createMenu(
            @PathVariable Integer restaurantId,
            @Valid @RequestBody MenuCreateRequest request) {
        Integer menuId = menuService.createMenu(restaurantId, request);
        return new ResponseEntity<>(Map.of("menuId", menuId), HttpStatus.CREATED);
    }


    @GetMapping("/restaurants/{restaurantId}/menus")
    public ResponseEntity<List<MenuResponse>> getMenusByRestaurantId(
            @PathVariable Integer restaurantId) {
        List<MenuResponse> menus = menuService.getMenusByRestaurantId(restaurantId);
        return ResponseEntity.ok(menus);
    }


    @PutMapping("/menus/{menuId}")
    public ResponseEntity<Void> updateMenu(
            @PathVariable Integer menuId,
            @Valid @RequestBody MenuUpdateRequest request) {
        menuService.updateMenu(menuId, request);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(
            @PathVariable Integer menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok().build();
    }
}

package spring.practice.elmenus_lite.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spring.practice.elmenus_lite.dto.request.MenuItemCreateRequest;
import spring.practice.elmenus_lite.dto.request.MenuItemUpdateRequest;
import spring.practice.elmenus_lite.dto.response.MenuItemResponse;

import java.math.BigDecimal;

public interface MenuItemService {

    Integer createMenuItem(Integer menuId, MenuItemCreateRequest request);


    Page<MenuItemResponse> getMenuItemsByRestaurantId(
            Integer restaurantId, String name, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);


    Page<MenuItemResponse> searchMenuItems(String keyword, Pageable pageable);

    void updateMenuItem(Integer menuItemId, MenuItemUpdateRequest request);

    void deleteMenuItem(Integer menuItemId);
}

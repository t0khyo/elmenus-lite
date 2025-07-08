package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.request.MenuCreateRequest;
import spring.practice.elmenus_lite.dto.request.MenuUpdateRequest;
import spring.practice.elmenus_lite.dto.response.MenuResponse;

import java.util.List;

public interface MenuService {

    Integer createMenu(Integer restaurantId, MenuCreateRequest request);

    List<MenuResponse> getMenusByRestaurantId(Integer restaurantId);

    void updateMenu(Integer menuId, MenuUpdateRequest request);

    void deleteMenu(Integer menuId);
}

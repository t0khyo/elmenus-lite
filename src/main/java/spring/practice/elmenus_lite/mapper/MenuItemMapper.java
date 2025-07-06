package spring.practice.elmenus_lite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.practice.elmenus_lite.dto.request.MenuItemCreateRequest;
import spring.practice.elmenus_lite.dto.response.MenuItemResponse;
import spring.practice.elmenus_lite.model.MenuItem;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {

    MenuItem toMenuItemEntity(MenuItemCreateRequest request);

    @Mapping(target = "menuId", source = "menu.id") // Map nested menu ID
    MenuItemResponse toMenuItemResponse(MenuItem menuItem);

}

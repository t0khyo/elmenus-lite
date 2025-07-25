package spring.practice.elmenus_lite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.practice.elmenus_lite.dto.response.CartItemResponse;
import spring.practice.elmenus_lite.dto.response.CartResponse;
import spring.practice.elmenus_lite.dto.response.MenuItemResponse;
import spring.practice.elmenus_lite.model.Cart;
import spring.practice.elmenus_lite.model.CartItem;
import spring.practice.elmenus_lite.model.MenuItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartResponse toCartResponse(Cart cart);

    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Mapping(source = "menu.id", target = "menuId")
    MenuItemResponse toMenuItemResponse(MenuItem menuItem);

}

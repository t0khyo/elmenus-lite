package spring.practice.elmenus_lite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.practice.elmenus_lite.dto.CartItemResponse;
import spring.practice.elmenus_lite.dto.CartResponse;
import spring.practice.elmenus_lite.dto.MenuItemResponse;
import spring.practice.elmenus_lite.model.Cart;
import spring.practice.elmenus_lite.model.CartItem;
import spring.practice.elmenus_lite.model.MenuItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(source = "id", target = "cartId")
    CartResponse toCartResponse(Cart cart);

    @Mapping(source = "id",target = "cartItemId")
    @Mapping(target = "totalPrice", expression = "java(BigDecimal.valueOf(cartItem.getQuantity()).multiply(cartItem.getMenuItem().getPrice()))")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Mapping(source = "menu.id" , target = "menuId")
    @Mapping(source = "id", target = "menuItemId")
    MenuItemResponse toMenuItemResponse(MenuItem menuItem);


}

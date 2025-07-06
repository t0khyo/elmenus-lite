package spring.practice.elmenus_lite.dto.response;

public record CartItemResponse(
        Integer id,
        MenuItemResponse menuItem,
        Integer quantity
) {
}


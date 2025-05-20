package spring.practice.elmenus_lite.dto;

public record CartItemResponse(
        Integer cartItemId,
        MenuItemResponse menuItem,
        Integer quantity
) {}


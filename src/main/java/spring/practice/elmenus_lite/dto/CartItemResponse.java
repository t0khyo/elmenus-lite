package spring.practice.elmenus_lite.dto;

public record CartItemResponse(
        Integer id,
        MenuItemResponse menuItem,
        Integer quantity
) {}


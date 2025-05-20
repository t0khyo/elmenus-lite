package spring.practice.elmenus_lite.dto;

import java.util.List;

public record CartResponse(
        Integer id,
        List<CartItemResponse> items
) {
}

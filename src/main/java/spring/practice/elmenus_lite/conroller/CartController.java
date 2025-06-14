package spring.practice.elmenus_lite.conroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.CartItemRequest;
import spring.practice.elmenus_lite.dto.CartItemUpdateRequest;
import spring.practice.elmenus_lite.dto.CartResponse;
import spring.practice.elmenus_lite.service.CartService;

@RestController
@RequestMapping("api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable Integer customerId,
            @RequestBody @Valid CartItemRequest cartItemRequest
    ) {
        return ResponseEntity.ok(cartService.addItemToCart(customerId, cartItemRequest));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponse> getCartByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.ok(cartService.getCartByCustomerId(customerId));
    }

    @PutMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable(name = "cartId") Integer cartId,
            @PathVariable(name = "cartItemId") Integer cartItemId,
            @RequestBody @Valid CartItemUpdateRequest cartItemUpdateRequest
    ) {
        return ResponseEntity.ok(cartService.updateCartItem(cartId, cartItemId, cartItemUpdateRequest));
    }

    @DeleteMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable(name = "cartId") Integer cartId,
            @PathVariable(name = "cartItemId") Integer cartItemId
    ) {
        return ResponseEntity.ok(cartService.removeCartItem(cartId, cartItemId));
    }

    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<CartResponse> clearCart(
            @PathVariable Integer cartId
    ) {
        return ResponseEntity.ok(cartService.clearCart(cartId));
    }
}

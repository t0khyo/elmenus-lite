package spring.practice.elmenus_lite.conroller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import spring.practice.elmenus_lite.dto.CartResponse;
import spring.practice.elmenus_lite.model.Customer;
import spring.practice.elmenus_lite.service.CartService;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponse> getCartByCustomerId(@PathVariable Integer customerId) {

        CartResponse cartResponseOpt = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(cartResponseOpt);
    }
    @DeleteMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable(name = "cartId") Integer cartId,
            @PathVariable(name = "cartItemId") Integer cartItemId
    ) {
        return ResponseEntity.ok(cartService.removeCartItem(cartId, cartItemId));
    }

    @DeleteMapping("{cartId}/clear")
    public ResponseEntity<CartResponse> clearCart(
            @PathVariable(name = "cartId") Integer cartId
    ) {
        return ResponseEntity.ok(cartService.clearCart(cartId));
    }
}

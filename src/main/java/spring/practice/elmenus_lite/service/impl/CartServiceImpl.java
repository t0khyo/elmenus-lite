package spring.practice.elmenus_lite.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import spring.practice.elmenus_lite.dto.CartResponse;
import spring.practice.elmenus_lite.mapper.CartMapper;
import spring.practice.elmenus_lite.model.Cart;
import spring.practice.elmenus_lite.model.CartItem;
import spring.practice.elmenus_lite.repostory.CartItemRepository;
import spring.practice.elmenus_lite.repostory.CartRepository;
import spring.practice.elmenus_lite.repostory.CustomerRepository;
import spring.practice.elmenus_lite.service.CartService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final CartMapper cartMapper;


    @Override
    public CartResponse getCartByCustomerId(Integer customerId){
        Optional<Cart> cartOptional = cartRepository.findByCustomerId(customerId);
        if(cartOptional.isPresent()){
            //cart entity
            Cart cart = cartOptional.get();
            // turn cart to cartResponse
            CartResponse cartResponse = cartMapper.toCartResponse(cart);
            return cartResponse;
        }
        else{
            //Check If Customer Not Found
            customerRepository.findById(customerId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer Not Found"));
            //Check If Cart Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart Not Found");
        }
    }

    @Override
    public CartResponse removeCartItem(Integer cartId, Integer cartItemId) {
        if (!isCartItemBelongsToCart(cartId, cartItemId))
            throw new RuntimeException("CartItem not found");

        CartItem cartItem = getCartItemById(cartItemId);
        cartItemRepository.delete(cartItem);

        Cart cart = getCartById(cartId);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse clearCart(Integer cartId) {
        Cart cart = getCartById(cartId);
        cartItemRepository.deleteAllInBatch(cart.getItems());
        cart.getItems().clear();

        return cartMapper.toCartResponse(cart);
    }

    // helper methods
    private Cart getCartById(int id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + id));
    }

    private CartItem getCartItemById(int id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart Item not found with id: " + id));
    }

    private Boolean isCartItemBelongsToCart(int cartId, int cartItemId) {
        return cartItemRepository.existsByIdAndCartId(cartItemId, cartId);
    }
}

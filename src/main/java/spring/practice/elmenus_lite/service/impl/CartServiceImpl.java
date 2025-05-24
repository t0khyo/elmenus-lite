package spring.practice.elmenus_lite.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import spring.practice.elmenus_lite.dto.CartItemRequest;
import spring.practice.elmenus_lite.dto.CartItemResponse;
import spring.practice.elmenus_lite.dto.CartResponse;
import spring.practice.elmenus_lite.mapper.CartMapper;
import spring.practice.elmenus_lite.model.Cart;
import spring.practice.elmenus_lite.model.CartItem;
import spring.practice.elmenus_lite.model.Customer;
import spring.practice.elmenus_lite.model.MenuItem;
import spring.practice.elmenus_lite.repostory.CartItemRepository;
import spring.practice.elmenus_lite.repostory.CartRepository;
import spring.practice.elmenus_lite.repostory.CustomerRepository;
import spring.practice.elmenus_lite.repostory.MenuItemRepository;
import spring.practice.elmenus_lite.service.CartService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final CartMapper cartMapper;

    //Refactor getCartByCustomerId
    @Override
    public CartResponse getCartByCustomerId(Integer customerId){
        //check if customer not found
        Optional<Cart> cartOptional = getCartByCustomerIdOrThrow(customerId);
        //check if customer didn't have Cart
        cartOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Cart not found for CustomerId: " + customerId));
        return cartMapper.toCartResponse(cartOptional.get());
    }

    @Override
    public CartItemResponse addItemToCart(Integer customerId, CartItemRequest cartItemRequest) {
        Integer menuItemIdRequest = cartItemRequest.menuItemId();
        //check if customer not found
        Optional<Cart> cartOptional = getCartByCustomerIdOrThrow(customerId);
        //Check if Menu item Found or throw
        Optional<MenuItem> menuItemOptional= menuItemRepository.findById(menuItemIdRequest);
        menuItemOptional.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Menu Item Not Found with id: "+menuItemIdRequest));
        MenuItem menuItem = menuItemOptional.get();
        //check if Menu Item NotAvailable
        if(!menuItem.getAvailable())
            throw new ResponseStatusException(HttpStatus.CONFLICT,"The item you requested is out of stock menuItemId: "+ menuItemIdRequest);
        //check If Customer have Cart
        if(cartOptional.isPresent()){
            Cart cart = cartOptional.get();
            //check if menuItem Added Before to cart
            CartItem savedCartItem;
            Optional<CartItem> CartItemOptional = cart.getItems().stream().filter(item -> item.getMenuItem().getId().equals(menuItemIdRequest)).findFirst();
            if(CartItemOptional.isPresent()){
                //update Quantity

                CartItem updatedCartItem = CartItemOptional.get().incrementQuantity(cartItemRequest.quantity());
                System.out.println(updatedCartItem.getId());
                 savedCartItem = cartItemRepository.save(updatedCartItem);
            }
            else{
                //creat item
                CartItem cartItem= CartItem.builder()
                        .cart(cart)
                        .menuItem(menuItem)
                        .quantity(cartItemRequest.quantity())
                        .build();
                 savedCartItem = cartItemRepository.save(cartItem);
            }
            return cartMapper.toCartItemResponse(savedCartItem);
        }
        else {
            //create cart
            Cart cart= new Cart();
            cart.setCustomer(customerRepository.findById(customerId).get());
            Cart savedCart=cartRepository.save(cart);
            //creat item
            CartItem cartItem= CartItem.builder()
                    .cart(savedCart)
                    .menuItem(menuItem)
                    .quantity(cartItemRequest.quantity())
                    .build();
            CartItem savedCartItem=cartItemRepository.save(cartItem);

            return cartMapper.toCartItemResponse(savedCartItem);
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

    private Optional<Cart> getCartByCustomerIdOrThrow(int customerId) {
        //check if customer not found
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer Not Found with id: "+customerId));
        //check if customer didn't have Cart
       return Optional.ofNullable(customer.getCart());
    }
}

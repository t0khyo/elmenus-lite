package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.model.CartItem;
import spring.practice.elmenus_lite.model.Promotion;

import java.math.BigDecimal;
import java.util.List;

public interface OrderCalculation {

    BigDecimal calculateDiscountAmount(BigDecimal subtotal, Promotion promotion);

    BigDecimal calculateSubtotal(List<CartItem> cartItems);
}

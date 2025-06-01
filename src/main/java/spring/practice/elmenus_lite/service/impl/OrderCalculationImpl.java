package spring.practice.elmenus_lite.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.model.CartItem;
import spring.practice.elmenus_lite.model.MenuItem;
import spring.practice.elmenus_lite.model.Promotion;
import spring.practice.elmenus_lite.repostory.MenuItemRepository;
import spring.practice.elmenus_lite.service.OrderCalculation;

import java.math.BigDecimal;
import java.util.List;
import java.math.RoundingMode;
@Service
@RequiredArgsConstructor
public class OrderCalculationImpl implements OrderCalculation {


    private final MenuItemRepository menuItemRepository;

    @Override
    public BigDecimal calculateDiscountAmount(BigDecimal subtotal, Promotion promotion) {
        if (promotion == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountPercent = promotion.getDiscountPercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal calculatedDiscount = subtotal.multiply(discountPercent);
        return calculatedDiscount.min(promotion.getMaxDiscount());
    }

    @Override
    public BigDecimal calculateSubtotal(List<CartItem> cartItems) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItem().getId()).get(); //Checked before
            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        return subtotal;
    }
}

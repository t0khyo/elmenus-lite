package spring.practice.elmenus_lite.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.InvalidOrderException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.CartItem;
import spring.practice.elmenus_lite.model.MenuItem;
import spring.practice.elmenus_lite.model.Order;
import spring.practice.elmenus_lite.model.Promotion;
import spring.practice.elmenus_lite.repostory.MenuItemRepository;
import spring.practice.elmenus_lite.repostory.OrderRepository;
import spring.practice.elmenus_lite.repostory.PromotionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderUtils {
    private final MenuItemRepository menuItemRepository;
    private final PromotionRepository promotionRepository;
    private final OrderRepository orderRepository;

    public Order fetchAndValidateOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ORDER_NOT_FOUND.getFinalMessage(orderId)));
    }

    public BigDecimal calculateDiscountAmount(BigDecimal subtotal, Promotion promotion) {
        if (promotion == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountPercent = promotion.getDiscountPercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal calculatedDiscount = subtotal.multiply(discountPercent);
        return calculatedDiscount.min(promotion.getMaxDiscount());
    }

    public BigDecimal calculateSubtotal(List<CartItem> cartItems) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItem().getId())
                    .orElseThrow(() -> new InvalidOrderException(
                            ErrorMessage.MENU_ITEM_NOT_FOUND.getFinalMessage(cartItem.getMenuItem().getId()))
                    );
            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        return subtotal;
    }

    public Promotion fetchAndValidatePromotion(String promotionCode) {
        if (promotionCode == null || promotionCode.trim().isEmpty()) {
            return null;
        }

        Promotion promotion = promotionRepository.findByCode(promotionCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.PROMOTION_NOT_FOUND.getFinalMessage(promotionCode))
                );

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartAt()) || now.isAfter(promotion.getEndAt())) {
            throw new InvalidOrderException(ErrorMessage.PROMOTION_EXPIRED.getFinalMessage());
        }
        return promotion;
    }
}

package spring.practice.elmenus_lite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.InvalidOrderException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.Promotion;
import spring.practice.elmenus_lite.repostory.PromotionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;

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

    public BigDecimal calculateDiscountAmount(BigDecimal subtotal, Promotion promotion) {
        if (promotion == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountPercent = promotion.getDiscountPercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal calculatedDiscount = subtotal.multiply(discountPercent);
        return calculatedDiscount.min(promotion.getMaxDiscount());
    }
}

package spring.practice.elmenus_lite.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.InvalidOrderException;
import spring.practice.elmenus_lite.model.Address;
import spring.practice.elmenus_lite.model.Cart;
import spring.practice.elmenus_lite.model.PreferredPaymentSetting;

@Service
@RequiredArgsConstructor
public class OwnershipValidator {

    public void validateCartOwnership(Cart cart, Integer customerId) {
        if (!cart.getCustomer().getId().equals(customerId)) {
            throw new InvalidOrderException(ErrorMessage.ENTITY_OWNERSHIP_VIOLATION.getFinalMessage("Cart"));
        }
    }

    public void validateAddressOwnership(Address address, Integer customerId) {
        if (!address.getCustomer().getId().equals(customerId)) {
            throw new InvalidOrderException(ErrorMessage.ENTITY_OWNERSHIP_VIOLATION.getFinalMessage("Address"));
        }
    }

    public void validatePaymentSettingOwnership(PreferredPaymentSetting paymentSetting, Integer customerId) {
        if (!paymentSetting.getCustomer().getId().equals(customerId)) {
            throw new InvalidOrderException(ErrorMessage.ENTITY_OWNERSHIP_VIOLATION.getFinalMessage("Payment setting"));
        }
    }
}
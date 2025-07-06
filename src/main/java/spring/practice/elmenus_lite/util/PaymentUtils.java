package spring.practice.elmenus_lite.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.PreferredPaymentSetting;
import spring.practice.elmenus_lite.model.Transaction;
import spring.practice.elmenus_lite.repostory.PreferredPaymentSettingRepository;
import spring.practice.elmenus_lite.repostory.TransactionRepository;

@Service
@RequiredArgsConstructor
public class PaymentUtils {
    private final PreferredPaymentSettingRepository preferredPaymentSettingRepository;
    private final TransactionRepository transactionRepository;
    private final OwnershipValidator ownershipValidator;

    public PreferredPaymentSetting fetchAndValidatePaymentSetting(Integer paymentSettingId, Integer customerId) {
        PreferredPaymentSetting paymentSetting = preferredPaymentSettingRepository.findById(paymentSettingId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PAYMENT_SETTING_NOT_FOUND.getFinalMessage(paymentSettingId)));

        ownershipValidator.validatePaymentSettingOwnership(paymentSetting, customerId);
        return paymentSetting;
    }

    public Transaction fetchTransactionByOrderId(Integer orderId) {
        return transactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ORDER_TRANSACTION_NOT_FOUND.getFinalMessage(orderId)));
    }
}
